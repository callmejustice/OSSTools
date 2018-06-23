<%@ page contentType="text/html;charset=utf-8" %>
<%
    String versionJson = request.getAttribute("versionJson") == null ? "" : (String) request.getAttribute("versionJson");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>广西电信OSS管理平台-创建任务单</title>
    <link rel="stylesheet" href="../../../js/ui/layui/css/layui.css">
    <link rel="stylesheet" href="../../../css/custom.css">
    <style>
        body{padding: 10px;}
    </style>
</head>
<body class="layui-layout-body">
<div class="layui-row">
    <div class="layui-col-lg6">
        <form class="layui-form layui-form-pane" action="">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="red">*</span>任务单标题</label>
                <div class="layui-input-block">
                    <input type="text" name="orderTitle" lay-verify="required" autocomplete="off"
                           placeholder="请输入任务单标题" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">ZMP单号</label>
                <div class="layui-input-block">
                    <input type="text" name="zmpId" autocomplete="off"
                           placeholder="请输入ZMP单号" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">版本计划</label>
                <div class="layui-input-block">
                    <input type="text" name="version" autocomplete="off"
                           placeholder="请选择版本计划" class="layui-input" id="version" readonly="readonly">
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn layui-bg-blue" lay-submit="" lay-filter="submit">立即提交</button>
                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                </div>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript" src="../../../js/ui/jquery/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../../../js/ui/layui/layui.js"></script>
<script>
    var versionJson = '<%=versionJson%>';
    try {
        versionJson = eval("(" + versionJson + ")");
    } catch (e) {
        versionJson = {};
    }
    layui.use(['form', 'laydate', 'layer'], function () {
        // 只有执行了这一步，部分表单元素才会自动修饰成功
        var form = layui.form
            , laydate = layui.laydate
            , layer = layui.layer;

        form.render();

        form.on('submit(submit)', function(data){
            var loadIndex;
            $.ajax({
                url: '../../../controller/service/develop/createOrder.do',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify(data.field),
                timeout: 30000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex);
                    if (response.meta.success) {
                        layer.confirm('创建研发任务单成功，继续创建任务吗？', {
                            btn: ['继续创建', '关闭']
                        }, function(index, layero){
                            layer.closeAll();
                        }, function(index){
                            window.parent.$('body').find('button[data-type=search]').trigger('click');
                            window.parent.$('body').find('.layui-layer-iframe a.layui-layer-close').trigger('click');
                        });
                    } else {
                        layer.alert(response.meta.message);
                    }
                },
                error: function (XHR, status, error) {
                    layer.close(loadIndex);
                    layer.alert('创建研发任务单异常：' + error);
                }
            });

            return false; // 阻止表单跳转。如果需要表单跳转，去掉这段即可。
        });

        laydate.render({
            elem: '#version',
            theme: 'custom-theme',
            showBottom: false,
            trigger: 'click',
            mark: versionJson,
            done: function(value, date){
                if(versionJson[value]){
                } else {
                    $('#version').val('');
                    layer.alert("对不起，当期日期未设置版本计划，不能选择！");
                }
            }
        });
    });
</script>
</body>
</html>