<%@ page contentType="text/html;charset=utf-8" %>
<style>
    .layui-table-cell .layui-form-checkbox[lay-skin=primary] {
        top: 5px;
    }
</style>
<div class="layui-row">
    <div class="layui-col-lg3">
        <div class="layui-row">
            <fieldset class="layui-elem-field">
                <legend>需求单汇总</legend>
                <div class="layui-field-box">
                    这里是图表
                </div>
            </fieldset>
        </div>
        <div>
            <fieldset class="layui-elem-field">
                <legend>需求单时间线</legend>
                <div class="layui-field-box">
                    <ul class="layui-timeline">
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis">&#xe63f;</i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">2017/12/20</h3>
                                <p>huang.jing 将需求单gx-0000-0001状态修改为需求开发</p>
                            </div>
                        </li>
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis">&#xe63f;</i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">2017/12/19</h3>
                                <p>huang.jing 将需求单gx-0000-0001状态修改为需求分析</p>
                            </div>
                        </li>
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis">&#xe63f;</i>
                            <div class="layui-timeline-content layui-text">
                                <h3 class="layui-timeline-title">2017/12/18</h3>
                                <p>huang.jing 提交了需求单gx-0000-0001</p>
                            </div>
                        </li>
                        <li class="layui-timeline-item">
                            <i class="layui-icon layui-timeline-axis">&#xe63f;</i>
                            <div class="layui-timeline-content layui-text">
                                <div class="layui-timeline-title">过去</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </fieldset>
        </div>
    </div>
    <div class="layui-col-lg9">
        <fieldset class="layui-elem-field">
            <legend>研发单列表</legend>
            <div class="layui-field-box">
                搜索关键字：
                <div class="layui-inline">
                    <input class="layui-input" name="searchInput" id="searchInput" autocomplete="off">
                </div>
                <div class="layui-btn-group">
                    <button class="layui-btn layui-bg-blue" data-type="search">搜索</button>
                </div>
                <div class="layui-btn-group">
                    <button type="button" class="layui-btn layui-bg-blue" id="test1">
                        <i class="layui-icon">&#xe67c;</i>导入厂家内容看板
                    </button>
                </div>
                <table id="zmpTable" lay-filter="zmpTable"></table>
            </div>
        </fieldset>
    </div>
</div>
<script type="text/html" id="stateTpl">
    {{#  if(d.state === '客户测试通过'){ }}
    <span style='color: #009688;'>{{ d.state }}</span>
    {{#  } else { }}
    <span style='color: #FF5722;'>{{ d.state }}</span>
    {{#  } }}
</script>
<script>
    layui.use(['layer', 'table', 'upload'], function () {
        var table = layui.table
            , layer = layui.layer
            , upload = layui.upload;

        //执行实例
        var uploadInst = upload.render({ //允许上传的文件后缀
            elem: '#test1',
            url: '../../controller/service/requirements/importRequirementsExcel.do',
            accept: 'file',
            // 只允许上传xls
            exts: 'xls|xlsx',
            // obj参数包含的信息，跟 choose回调完全一致，可参见上文。
            before: function(obj){
                layer.load();
            },
            done: function (res) {
                layer.closeAll();
                if(res.meta.success) {
                    var msg = '成功导入' + res.data.successCount + '条记录，失败' + res.data.failureCount + '条记录。';
                    if(parseInt(res.data.failureCount) > 0) {
                        msg += '<br />失败原因：' + res.data.errorInfo.replace(/\r\n/g, '<br />');
                        layer.open({
                            title: '导入结果',
                            content: msg,
                            area: ['800px', '500px']
                        });
                    } else {
                        layer.alert(msg);
                    }
                } else {
                    layer.alert(res.meta.message);
                }
            },
            error: function(index, upload){
                layer.closeAll('loading');
            }
        });

        //第一个实例
        table.render({
            elem: '#zmpTable'
            , id: 'zmpTable'
            , method: 'post'
            , cellMinWidth: 80
            , height: 500
            , url: '../../controller/service/requirements/qryOrderList.do' //数据接口
            , page: {
                theme: '#1E9FFF'
            }
            , cols: [[ //表头
                {type: 'checkbox'}
                , {field: 'id', title: '需求单号', sort: true}
                , {field: 'name', title: '需求标题', sort: true}
                , {field: 'author', title: '提交人'}
                , {field: 'state', title: '状态', templet: '#stateTpl', event: 'showStateChangeList'}
                , {field: 'createDate', title: '提交时间'}
                , {field: 'relaCount', title: '关联ZMP单', event: 'showRelaZMP'}
            ]]
        });
        // 监听单元格事件
        table.on('tool(zmpTable)', function (obj) {
            var data = obj.data;
            if (obj.event === 'showRelaZMP') {
                layer.open({
                    type: 1
                    , title: '关联ZMP单'
                    , closeBtn: false
                    , area: '300px;'
                    , shade: 0.8
                    , id: 'lay_' + data.id + '_showRelaZMP' //设定一个id，防止重复弹出
                    , btn: ['关闭']
                    , btnAlign: 'c'
                    , moveType: 1 //拖拽模式，0或者1
                    , content: '<div style="padding: 10px;">' +
                    '<fieldset class="layui-elem-field">\n' +
                    '  <legend>ZMP单号：123</legend>\n' +
                    '  <div class="layui-field-box">\n' +
                    '    开发中\n' +
                    '  </div>\n' +
                    '</fieldset>' +
                    '</div>'
                });
            } else if (obj.event === 'showStateChangeList') {
                layer.open({
                    type: 1
                    , title: '修改记录'
                    , closeBtn: false
                    , area: '300px;'
                    , shade: 0.8
                    , id: 'lay_' + data.id + '_stateChangeList' //设定一个id，防止重复弹出
                    , btn: ['关闭']
                    , btnAlign: 'c'
                    , moveType: 1 //拖拽模式，0或者1
                    , content: '<div style="padding: 10px;">' +
                    '<ul class="layui-timeline">\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 16:00:00</h3>\n' +
                    '      <p>huang.jing将需求单修改为已上线</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 15:00:00</h3>\n' +
                    '      <p>huang.jing将需求单修改为客户测试通过</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '  <li class="layui-timeline-item">\n' +
                    '    <i class="layui-icon layui-timeline-axis"></i>\n' +
                    '    <div class="layui-timeline-content layui-text">\n' +
                    '      <h3 class="layui-timeline-title">2017/12/30 15:00:00</h3>\n' +
                    '      <p>huang.jing将需求单修改为需求分析</p>\n' +
                    '    </div>\n' +
                    '  </li>\n' +
                    '</ul>' +
                    '</div>'
                });
            }
        });
        var $ = layui.$, active = {
            downloadCheckRows: function () { // 下载选中ZMP单代码
                var checkStatus = table.checkStatus('zmpTable')
                    , data = checkStatus.data;
                layer.alert('即将下载' + data.length + '个ZMP单的代码列表');
            }
            , search: function () { // 根据关键字查询
                //执行重载
                table.reload('zmpTable', {
                    page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        keyword: $('#searchInput').val()
                    }
                });
            }
        };
        $('#zmpTable').parents('.layui-field-box').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
</script>