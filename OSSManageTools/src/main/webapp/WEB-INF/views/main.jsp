<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>广西移动家宽管理平台</title>
    <link rel="stylesheet" href="../../js/ui/layui/css/layui.css">
    <link rel="stylesheet" href="../../css/custom.css">
</head>
<body>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header layui-bg-blue">
        <div class="layui-logo layui-bg-blue">广西移动家宽管理平台</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left layui-bg-blue" lay-filter="optionsNav"></ul>
        <ul class="layui-nav layui-layout-right layui-bg-blue" lay-filter="loginNav">
            <li class="layui-nav-item">
                <a href="javascript:;" id="userName"></a>
                <dl class="layui-nav-child">
                    <dd><a href="javascript:;">基本资料</a></dd>
                </dl>
            </li>
            <li class="layui-nav-item"><a href="javascript:;">退出</a></li>
        </ul>
    </div>

    <div class="layui-body" style="left: 0px">
        <div style="padding: 15px;" id="centerContentDiv"></div>
    </div>

    <div class="layui-footer layui-bg-blue" lay-filter="footer" style="left: 0px">
        <div id="footContentDiv"></div>
    </div>
</div>
<script type="text/javascript" src="../../js/ui/jquery/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="../../js/ui/echarts/4.0.2/echarts.min.js"></script>
<script type="text/javascript" src="../../js/ui/layui/layui.js"></script>
<script type="text/javascript" src="../../js/login/LoginClass.js"></script>
<script type="text/javascript" src="../../js/menu/MenuClass.js"></script>
<script type="text/javascript" src="../../js/workOrder/WorkOrderClass.js"></script>
<script>
    // JavaScript代码区域
    layui.use(['layer', 'element'], function () {
        var element = layui.element
            , layer = layui.layer;
        var loginClass = LoginClass();
        loginClass.init(element, layer);
        loginClass.bindEvent();
        loginClass.getLoginInfo();

        var menuClass = MenuClass();
        menuClass.init(element, layer, 'optionsNav', '#centerContentDiv', '#footContentDiv');
        menuClass.drawMenu();
        menuClass.bindController();
        menuClass.doDefaultService();
    });
</script>
</body>
</html>