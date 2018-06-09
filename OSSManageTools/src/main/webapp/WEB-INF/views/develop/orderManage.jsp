<%@ page contentType="text/html;charset=utf-8" %>
<%
    String versionJson = request.getAttribute("versionJson") == null ? "" : (String) request.getAttribute("versionJson");
%>
<style>
    .layui-table-cell .layui-form-checkbox[lay-skin=primary] {
        top: 5px;
    }

    .layui-body {
        overflow-x: scroll;
    }
</style>
<form id="svnForm" method="post" action="develop/attachSVN.do">
    <input type="hidden" name="developOrderId"/>
    <input type="hidden" name="workOrderId"/>
    <input type="hidden" name="orderTitle"/>
</form>
<div class="layui-row">
    <div class="layui-col-lg3">
        <div class="layui-row">
            <fieldset class="layui-elem-field">
                <legend>研发任务单汇总</legend>
                <div class="layui-field-box">
                    这里是图表
                </div>
            </fieldset>
        </div>
        <div>
            <fieldset class="layui-elem-field">
                <legend>研发任务单时间线</legend>
                <div class="layui-field-box">
                    <ul class="layui-timeline">
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis">&#xe63f;</i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">某年某月某日</h3>
                                <p>这里是状态记录</p>
                            </div>
                        </li>
                    </ul>
                </div>
            </fieldset>
        </div>
    </div>
    <div class="layui-col-lg9" id="developOrderTableLayer">
        <fieldset class="layui-elem-field">
            <legend>研发任务单列表</legend>
            <div class="layui-field-box">
                <div class="layui-form">
                    <div class="layui-form-item">
                        <div class="layui-inline">
                            <label class="layui-form-label">标题</label>
                            <div class="layui-input-inline">
                                <input class="layui-input" name="orderTitle" id="orderTitle" autocomplete="off">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-form-label">ZMP单号</label>
                            <div class="layui-input-inline">
                                <input class="layui-input" name="zmpId" id="zmpId" autocomplete="off">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-form-label">状态</label>
                            <div class="layui-input-inline">
                                <select name="orderState" id="orderState">
                                    <option value="" selected="">全部</option>
                                    <option value="10N">进行中</option>
                                    <option value="10E">异常</option>
                                    <option value="10F">已结束</option>
                                    <option value="10C">已撤销</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-form-label">环节</label>
                            <div class="layui-input-inline">
                                <select name="tacheId" id="tacheId">
                                    <option value="" selected="">全部</option>
                                    <option value="1">研发中</option>
                                    <option value="2">内部测试</option>
                                    <option value="3">客户测试</option>
                                    <option value="4">发布版本</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-form-label">版本计划</label>
                            <div class="layui-input-inline">
                                <input type="text" name="version" id="version" lay-verify="date"
                                       placeholder="yyyy-MM-dd" autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <button class="layui-btn layui-bg-blue" data-type="search">搜索</button>
                        </div>
                    </div>
                </div>

                <div class="layui-btn-group">
                    <button class="layui-btn layui-btn-normal layui-btn-sm" data-type="createOrder">创建任务单</button>
                </div>
                <div class="layui-btn-group">
                    <button class="layui-btn layui-btn-warm layui-btn-sm" data-type="transferWorkOrder">转派任务单</button>
                </div>
                <table id="developOrderTable" lay-filter="developOrderTable"></table>
            </div>
        </fieldset>
    </div>
</div>
<%--根据定单的状态上色--%>
<script type="text/html" id="stateTpl">
    {{#  if(d.STATE == '10F'){ }}
    <span class='molv'>{{ d.STATE_NAME }}</span>
    {{#  } else if(d.STATE == '10C'){ }}
    <span class='gray'>{{ d.STATE_NAME }}</span>
    {{#  } else { }}
    <span class='red'>{{ d.STATE_NAME }}</span>
    {{#  } }}
</script>
<script type="text/html" id="workOrderStateTpl">
    {{#  if(d.WORK_ORDER_STATE == '10F'){ }}
    <span class='molv'>{{ d.WORK_ORDER_STATE_NAME }}</span>
    {{#  } else if(d.WORK_ORDER_STATE == '10R'){ }}
    <span class='gray'>{{ d.WORK_ORDER_STATE_NAME }}</span>
    {{#  } else { }}
    <span class='red'>{{ d.WORK_ORDER_STATE_NAME }}</span>
    {{#  } }}
</script>
<%--只能对研发环节模块的进行回单--%>
<script type="text/html" id="finishTpl">
    {{# if(d.ORDER_TYPE == 'develop' && d.WORK_ORDER_STATE == '10N'){ }}
    <a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="finishWorkOrder">提交代码</a>
    {{# } else if(d.ORDER_TYPE == 'test'){ }}
    <a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="showSVNRela">查看代码</a>
    {{#  } else { }}
    {{#  } }}
</script>
<script>
    var versionJson = '<%=versionJson%>';
    try {
        versionJson = eval("(" + versionJson + ")");
    } catch (e) {
        versionJson = {};
    }
    var developOrderTableLayerWidth = $("#developOrderTableLayer").width() * 0.98;
    layui.use(['layer', 'table', 'laydate', 'form', 'element'], function () {
        var form = layui.form
            , table = layui.table
            , element = layui.element
            , laydate = layui.laydate
            , layer = layui.layer;

        form.render();

        // 研发任务单列表
        table.render({
            elem: '#developOrderTable',
            id: 'developOrderTable',
            method: 'post',
            cellMinWidth: 100,
            height: 450,
            width: developOrderTableLayerWidth,
            url: '../../controller/service/develop/qryOrderList.do',
            page: {
                theme: 'custom-theme'
            },
            cols: [[ //表头
                {type: 'checkbox', fixed: 'left'},
                {field: 'ORDER_TITLE', title: '任务单标题', fixed: 'left'},
                {field: 'ZMP_ID', title: 'ZMP单号', fixed: 'left'},
                {field: 'OPERATION', title: '操作', templet: '#finishTpl', fixed: 'left'},
                {field: 'AUTHOR', title: '创建人', sort: true},
                {field: 'STATE_NAME', title: '状态', templet: '#stateTpl', event: 'showStateChangeList'},
                {field: 'CREATE_DATE', title: '创建时间', sort: true},
                {field: 'finish_DATE', title: '结束时间', sort: true},
                {field: 'VERSION', title: '版本计划'},
                {field: 'WORK_ORDER_TACHE_NAME', title: '当前环节'},
                {field: 'WORK_ORDER_STATE_NAME', title: '当前工单状态', templet: '#workOrderStateTpl'},
                {field: 'WORK_ORDER_OPER', title: '当前工单处理人'},
                {field: 'WORK_ORDER_CREATE_DATE', title: '当前工单创建时间'},
                {field: 'DUP_ORDER_COUNT', title: '重叠代码任务单数量'}
            ]]
        });
        // 监听单元格事件
        table.on('tool(developOrderTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'showStateChangeList') {
                layer.open({
                    type: 1
                    , title: '修改记录'
                    , closeBtn: false
                    , area: '300px;'
                    , shade: 0.8
                    , id: 'lay_' + data.DEVELOP_ORDER_ID + '_stateChangeList' //设定一个id，防止重复弹出
                    , btn: ['关闭']
                    , btnAlign: 'c'
                    , moveType: 1 //拖拽模式，0或者1
                    , content: '<div style="padding: 10px;">' +
                    '<ul class="layui-timeline">\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 16:00:00</h3>\n' +
                    '      <p>huang.jing将ZMP单修改为客户测试通过</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 15:00:00</h3>\n' +
                    '      <p>huang.jing将ZMP单修改为内部测试通过</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 15:00:00</h3>\n' +
                    '      <p>ZMP单由huang.jing研发完成</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '</ul>' +
                    '</div>'
                });
            } else if (obj.event === 'finishWorkOrder') {
                $('#svnForm input[name=developOrderId]').val(data.DEVELOP_ORDER_ID);
                $('#svnForm input[name=workOrderId]').val(data.MAX_WORK_ORDER_ID);
                $('#svnForm input[name=orderTitle]').val(encodeURI(data.ORDER_TITLE));
                $('#svnForm').submit();
            } else if (obj.event === 'showSVNRela') {
                var developOrderList = new Array();
                developOrderList.push({
                    developOrderId: data.DEVELOP_ORDER_ID
                });
                var loadIndex;
                $.ajax({
                    url: '../../controller/service/develop/qrySVNRelaList.do',
                    method: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    data: JSON.stringify({
                        developOrderList: developOrderList
                    }),
                    timeout: 30000,
                    beforeSend: function () {
                        loadIndex = layer.load(0);
                    },
                    success: function (response) {
                        layer.close(loadIndex);
                        if (response.meta.success) {
                            var svnLogRelaList = response.data.svnLogRelaList;
                            var workOrderClass = new WorkOrderClass();
                            workOrderClass.init(element, layer, data.DEVELOP_ORDER_ID, data.MAX_WORK_ORDER_ID);
                            for (var i in svnLogRelaList) {
                                // 将数据加入列表中
                                workOrderClass.addSVNList(svnLogRelaList[i]);
                            }
                            // 获取展示用的对象
                            var showObject = workOrderClass.getShowObject();
                            // 将对象在页面渲染输出
                            var html = '<div class="layui-collapse">';
                            for (var i in showObject) {
                                html += '<div class="layui-colla-item">';
                                html += '<h2 class="layui-colla-title">' + i + '(' + showObject[i].length + '个文件)</h2>';
                                for (var j in showObject[i]) {
                                    var path = showObject[i][j]['path'];
                                    path = workOrderClass.transferPath(path, i);

                                    html += '<div class="layui-colla-content">';
                                    html += '<p>' + showObject[i][j]['count'] + '个版本' + showObject[i][j]['type'] + ' ' + path + '</p>';
                                    html += '</div>';
                                }
                                html += '</div>';
                            }
                            html += '</div>';
                            layer.open({
                                type: 1,
                                title: '任务单关联SVN代码列表',
                                closeBtn: false,
                                skin: 'custom-content',
                                area: ['500px', '500px'],
                                btn: ['关闭'],
                                btnAlign: 'c',
                                moveType: 1,
                                content: html,
                                success: function () {
                                    element.init();
                                }
                            });

                        } else {
                            layer.alert('查询任务单关联的SVN列表错误')
                        }
                    },
                    error: function (XHR, status, error) {
                        layer.close(loadIndex);
                        layer.alert('查询任务单关联的SVN列表异常：' + error);
                    }
                });
            }
        });

        laydate.render({
            elem: '#version',
            theme: 'custom-theme',
            showBottom: false,
            trigger: 'click',
            mark: versionJson,
            done: function (value, date) {
                // 作为查询条件时，不校验选择的日期是否具备版本
            }
        });

        var $ = layui.$, active = {
            /**
             *  转派任务单
             */
            transferWorkOrder: function () {
                var checkStatus = table.checkStatus('developOrderTable')
                    , data = checkStatus.data;

                if (data.length == 0) {
                    layer.alert('请先选中任务单再进行转派');
                    return;
                }

                var html = '<div class="layui-row"><br /></div>';
                html += '<div class="layui-row">';
                html += '   <div class="layui-form" id="transferForm">';
                html += '       <div class="layui-form-item">';
                html += '           <div class="layui-inline">';
                html += '               <label class="layui-form-label">转派至</label>';
                html += '               <div class="layui-input-inline"><input type="text" name="recieveOper" autocomplete="off" class="layui-input"></div>';
                html += '           </div>';
                html += '           <div class="layui-inline">';
                html += '               <label class="layui-form-label">转派原因</label>';
                html += '               <div class="layui-input-inline"><input type="text" name="remark" autocomplete="off" class="layui-input"></div>';
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
                html += '</div>';

                layer.open({
                    type: 1,
                    title: '转派任务单',
                    closeBtn: false,
                    skin: 'custom-content',
                    area: '500px;',
                    btn: ['确认', '关闭'],
                    btnAlign: 'c',
                    moveType: 1,
                    content: html,
                    yes: function (index, layero) {
                        var recieveOper = $.trim($('#transferForm [name=recieveOper]').val());
                        var remark = $.trim($('#transferForm [name=remark]').val());
                        if (recieveOper == '') {
                            layer.alert('请填写转派至工号');
                            return false;
                        } else if (remark == '') {
                            layer.alert('请填写转派原因');
                            return false;
                        }

                        var workOrderList = new Array();
                        for (var i in checkStatus.data) {
                            workOrderList.push({
                                workOrderId: checkStatus.data[i].MAX_WORK_ORDER_ID,
                                orderType: 'develop',
                                developOrderId: checkStatus.data[i].DEVELOP_ORDER_ID
                            });
                        }

                        var workOrderClass = new WorkOrderClass();
                        workOrderClass.init('', layer, '', '');
                        workOrderClass.transferWorkOrder(JSON.stringify({
                            recieveOper: recieveOper,
                            remarl: remark,
                            workOrderList: workOrderList
                        }), '../../controller/service/develop/transferWorkOrder.do');
                    }
                });
                form.render();

            },
            /**
             * 创建任务单
             */
            createOrder: function () {
                layer.open({
                    area: ['500px', '520px'],
                    content: '../../controller/views/develop/createOrder.do',
                    skin: 'custom-content',
                    title: '创建任务单',
                    type: 2
                });
            }
            /**
             * 搜索
             */
            , search: function () { // 根据关键字查询
                var orderTitle = $('#orderTitle').val();
                var zmpId = $('#zmpId').val();
                var orderState = $('#orderState').val();
                var version = $('#version').val();
                var tacheId = $('#tacheId').val();
                //执行重载
                table.reload('developOrderTable', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        orderTitle: orderTitle,
                        zmpId: zmpId,
                        orderState: orderState,
                        version: version,
                        tacheId: tacheId
                    }
                });
            }
        };
        $('#developOrderTable').parents('.layui-field-box').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
</script>