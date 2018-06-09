<%@ page contentType="text/html;charset=utf-8" %>
<%
    String developOrderId = request.getAttribute("developOrderId") == null ? "" : (String) request.getAttribute("developOrderId");
    String workOrderId = request.getAttribute("workOrderId") == null ? "" : (String) request.getAttribute("workOrderId");
    String orderTitle = request.getAttribute("orderTitle") == null ? "" : (String) request.getAttribute("orderTitle");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>广西移动家宽管理平台-研发任务单回单</title>
    <link rel="stylesheet" href="../../../js/ui/layui/css/layui.css">
    <link rel="stylesheet" href="../../../css/custom.css">
    <style>
        .layui-table-cell .layui-form-checkbox[lay-skin=primary] {
            top: 5px;
        }

        .layui-body {
            overflow-x: scroll;
        }

        .layui-form .layui-form-label {
            font-size: 0.8em;
        }

        .layui-form .layui-form-item {
            margin-bottom: 0px;
        }
    </style>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header layui-bg-blue">
        <div class="layui-logo layui-bg-blue">广西移动家宽管理平台</div>
    </div>
    <div class="layui-row"><br/></div>
    <div class="layui-row">
        <blockquote class="layui-elem-quote layui-quote-nm" style="text-align: center;">您正在对任务单[<%=orderTitle%>]进行提交代码
        </blockquote>
    </div>
    <div class="layui-row">
        <div class="layui-col-lg6" id="svnTableLayer">
            <fieldset class="layui-elem-field" style="min-height: 500px; max-height: 500px; padding: 5px;">
                <legend>SVN提交记录</legend>
                <div class="layui-field-box">
                    <div class="layui-form">
                        <div class="layui-form-item">
                            <div class="layui-inline">
                                <label class="layui-form-label">已关联任务单</label>
                                <div class="layui-input-inline" style="width: 250px;">
                                    <input type="radio" name="isRelaOrder" value="1" title="是">
                                    <input type="radio" name="isRelaOrder" value="0" title="否" checked>
                                    <input type="radio" name="isRelaOrder" value="" title="全部">
                                </div>
                            </div>
                            <div class="layui-inline">
                                <label class="layui-form-label" style="width: 50px;">版本号</label>
                                <div class="layui-input-inline" style="width: 90px;">
                                    <input type="text" name="reversion" autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-inline">
                                <label class="layui-form-label">
                                    <button class="layui-btn layui-bg-blue" data-type="search">搜索</button>
                                </label>
                            </div>
                        </div>
                    </div>
                    <table id="svnTable" lay-filter="svnTable"></table>
                </div>
            </fieldset>
        </div>
        <div class="layui-col-lg6">
            <fieldset class="layui-elem-field" style="min-height: 500px; max-height: 500px; padding: 5px">
                <legend>已选择代码</legend>
                <div class="layui-collapse" lay-filter="test" style="overflow: auto; max-height: 470px; display: none;"
                     id="relaSVNContainer"></div>
            </fieldset>
        </div>
    </div>
    <div class="layui-row">
        <div class="layui-col-lg10 layui-col-lg-offset2">
            <div class="layui-form">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">测试人</label>
                        <div class="layui-input-inline">
                            <select name="tester" id="tester">
                                <option value="tianhuaqing" selected="">田华清</option>
                            </select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">脚本</label>
                        <div class="layui-input-inline">
                            <input type="text" name="sql" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">测试文档</label>
                        <div class="layui-input-inline">
                            <input type="text" name="testReport" autocomplete="off" class="layui-input">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="layui-row"><br/></div>
    <div class="layui-row">
        <div class="layui-col-lg6 layui-col-lg-offset5">
            <div class="layui-btn-group">
                <button class="layui-btn layui-bg-blue" data-type="submit">提交</button>
            </div>
            <div class="layui-btn-group">
                <button class="layui-btn layui-btn-danger" data-type="reset">重置</button>
            </div>
            <div class="layui-btn-group">
                <button class="layui-btn layui-btn-primary" data-type="cancel">返回</button>
            </div>
        </div>
    </div>
    <div class="layui-footer layui-bg-blue" lay-filter="footer" style="left: 0px">
        <div id="footContentDiv">您正在使用[提交代码]功能</div>
    </div>
</div>
<script type="text/javascript" src="../../../js/ui/jquery/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../../../js/ui/layui/layui.js"></script>
<script type="text/javascript" src="../../../js/workOrder/WorkOrderClass.js"></script>
<script type="text/html" id="relaCountTpl">
    {{#  if(d.RELA_COUNT == '0'){ }}
    <span class=''>{{ d.RELA_COUNT }}</span>
    {{#  } else { }}
    <span class='red'>{{ d.RELA_COUNT }}</span>
    {{#  } }}
</script>
<script>
    var svnTableLayerWidth = $("#svnTableLayer").width() * 0.93;
    var isRelaOrder = $('[name=isRelaOrder]:checked').val();
    var reversion = $('[name=reversion]').val();
    layui.use(['table', 'layer', 'element', 'form'], function () {
        var table = layui.table
            , layer = layui.layer
            , element = layui.element
            , form = layui.form;

        form.render();

        var workOrderClass = new WorkOrderClass();
        workOrderClass.init(element, layer, '<%=developOrderId%>', '<%=workOrderId%>');

        // 研发任务单列表
        table.render({
            elem: '#svnTable',
            id: 'svnTable',
            method: 'post',
            cellMinWidth: 60,
            height: 390,
            width: svnTableLayerWidth,
            url: '../../../controller/service/develop/qrySVNList.do',
            limit: 50,
            where: {
                isRelaOrder: isRelaOrder,
                reversion: reversion
            },
            cols: [[ //表头
                {type: 'checkbox', fixed: 'left'},
                {field: 'REVISION', title: '版本号', fixed: 'left'},
                {field: 'MESSAGE', title: '备注', width: 300},
                {field: 'CREATE_DATE', title: '创建时间', width: 180, sort: true},
                {field: 'AUTHOR', title: '创建人'},
                {field: 'RELA_COUNT', title: '关联任务单数量', templet: '#relaCountTpl'}
            ]]
        });

        table.on('checkbox(svnTable)', function (obj) {
            // 选中
            if (obj.checked) {
                // 将选中的数据加入列表中
                workOrderClass.addSVNList(obj.data);
            } else {
                // 将反选的数据从列表中移除
                workOrderClass.remove(obj.data);
            }
            // 获取展示用的对象
            var showObject = workOrderClass.getShowObject();

            // 将对象在页面渲染输出
            var html = '';
            for (var i in showObject) {
                html += '<div class="layui-colla-item">';
                html += '<h2 class="layui-colla-title">' + i + '(' + showObject[i].length + '个文件)</h2>';
                for (var j in showObject[i]) {
                    html += '<div class="layui-colla-content">';
                    html += '<p>' + showObject[i][j]['count'] + '个版本' + showObject[i][j]['type'] + ' ' + showObject[i][j]['path'] + '</p>';
                    html += '</div>';
                }
                html += '</div>';
            }
            $('#relaSVNContainer').html(html).show();
            if (html == '') {
                $('#relaSVNContainer').hide();
            }
            element.init();
        });

        var $ = layui.$, active = {
            /**
             * 搜索
             */
            search: function () { // 根据关键字查询
                var isRelaOrder = $('[name=isRelaOrder]:checked').val();
                var reversion = $('[name=reversion]').val();
                //执行重载
                table.reload('svnTable', {
                    where: {
                        isRelaOrder: isRelaOrder,
                        reversion: reversion
                    }
                });
            }
        };

        $('button[data-type=cancel]').bind('click', function () {
            history.back();
        });

        $('button[data-type=reset]').bind('click', function () {
            location.reload();
        });

        $('button[data-type=submit]').bind('click', function () {
            if (workOrderClass.verifyIsEmpty()) {
                layer.confirm('您还没关联SVN版本，确认要提交代码？', {
                    btn: ['确认', '取消']
                }, function (index, layero) {
                    layer.closeAll();
                    workOrderClass.finishWorkOrder();
                }, function (index) {
                    layer.closeAll();
                });
            } else {
                workOrderClass.finishWorkOrder();
            }
        });

        // 禁止全选
        $('div.layui-table-header input[name=layTableCheckbox]').remove();

        $('#svnTable').parents('.layui-field-box').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
    });
</script>
</body>
</html>