/**
 * 登录类
 * @returns {{option: {}, bindEvent: bindEvent, getLoginInfo: getLoginInfo, init: init, logout: logout}}
 * @constructor
 */
function LoginClass() {
    var loginClass = {
        option: {},
        /**
         * 绑定事件
         */
        bindEvent: function () {
            var root = this;
            root.option.element.on('nav(loginNav)', function (elem) {
                if (elem.text() == '退出') {
                    root.logout();
                }
            });
        },
        /**
         * 获取用户登录信息
         */
        getLoginInfo: function () {
            var root = this;
            var loadIndex;
            $.ajax({
                url: '../../controller/service/login/getLoginInfo.do',
                method: 'POST',
                timeout: 10000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex);
                    if (response.meta.success) {
                        $('#userName').html(response.data.userName);
                        // 修改参数后需要对 lay-filter='loginNav' 所在导航重新渲染。
                        root.option.element.render('nav', 'loginNav');
                    }
                },
                error: function (XHR, status, error) {
                    layer.closeAll();
                    layer.alert('退出登录异常：' + error);
                }
            });
        },
        /**
         *  初始化
         */
        init: function (element, layer) {
            var root = this;
            root.option.element = element;
            root.option.layer = layer;
        },
        /**
         * 退出登录
         */
        logout: function () {
            var root = this;
            var loadIndex;
            $.ajax({
                url: '../../controller/service/login/logout.do',
                method: 'POST',
                timeout: 10000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    root.option.layer.close(loadIndex)
                    root.option.layer.load(0);
                    window.location.href = '../../';
                },
                error: function (XHR, status, error) {
                    root.option.layer.closeAll();
                    root.option.layer.alert('退出登录异常：' + error);
                }
            });
        }
    };
    return loginClass;
};