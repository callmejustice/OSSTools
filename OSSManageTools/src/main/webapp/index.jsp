<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>广西电信OSS管理平台</title>
    <link rel="stylesheet" href="js/ui/layui/css/layui.css">
    <style>
        .manage-row-180 {
            height: 180px;
            clear: both;
        }
    </style>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header layui-bg-blue">
        <div class="layui-logo layui-bg-blue">广西电信OSS管理平台</div>
    </div>
    <div class="layui-container">
        <div class="layui-row">
            <div class="manage-row-180"></div>
        </div>
        <div class="layui-row">
            <div class="layui-col-lg4 layui-col-lg-offset4">
                <fieldset class="layui-elem-field">
                    <legend>广西电信OSS管理平台</legend>
                    <div class="layui-field-box">
                        <form class="layui-form " action="">
                            <div class="layui-form-item">
                                <label class="layui-form-label">用户名</label>
                                <div class="layui-input-inline">
                                    <input type="text" name="userName" required lay-verify="required"
                                           placeholder="请输入用户名"
                                           autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">密码框</label>
                                <div class="layui-input-inline">
                                    <input type="password" name="password" required lay-verify="required"
                                           placeholder="请输入密码" autocomplete="off" class="layui-input">
                                </div>
                                <div class="layui-form-mid layui-word-aux"></div>
                            </div>
                            <div class="layui-form-item">
                                <div class="layui-input-block">
                                    <button class="layui-btn layui-bg-blue" lay-submit lay-filter="loginForm">立即提交
                                    </button>
                                    <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </fieldset>
            </div>
        </div>
    </div>
    <div class="layui-footer layui-bg-blue" lay-filter="footer" style="left: 0px">
        <div id="footContentDiv"></div>
    </div>
</div>
<script type="text/javascript" src="js/ui/jquery/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="js/ui/layui/layui.js"></script>
<script>
    layui.use(['form', 'layer', 'element'], function () {
        var element = layui.element
            , form = layui.form
            , layer = layui.layer;

        form.on('submit(loginForm)', function (data) {
            var loadIndex;
            $.ajax({
                url: 'controller/service/login/logon.do',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({userName: data.field.userName, password: data.field.password}),
                timeout: 30000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex)
                    if (response.meta.success) {
                        layer.load(0);
                        window.location.href = "controller/views/main.do";
                    } else {
                        layer.alert("登录失败：" + response.meta.message);
                    }
                },
                error: function (XHR, status, error) {
                    layer.close(loadIndex);
                    layer.alert("登录异常：" + error);
                }
            });
            return false;
        });
    });
</script>
</body>
</html>