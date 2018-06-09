/**
 * 菜单类
 * @returns {{option: {menuJson: *[]}, bindController: bindController, doDefaultService: doDefaultService, getView: getView, init: init, drawMenu: drawMenu}}
 * @constructor
 */
function MenuClass() {
    var menuClass = {
        option: {
            menuJson: [{
                'name': '研发模块',
                'children': [{
                    'name': '研发任务单管理',
                    'controller': '../../controller/views/develop/orderManage.do'
                }]
            }, {
                'name': '需求模块',
                'children': [{
                    'name': '需求任务单管理',
                    'controller': '../../controller/views/requirements/orderManage.do'
                }]
            }, {
                'name': '测试模块',
                'children': [{
                    'name': '待测试任务单管理',
                    'controller': '../../controller/views/test/orderManage.do'
                }]
            }, {
                'name': '维护模块',
                'children': [{
                    'name': '知识库',
                    'controller': '../../controller/views/kbs/knowledgeManage.do'
                },{
                    'name': '应用连接数监控',
                    'controller': '../../controller/views/monitor/portMonitor.do'
                }, {
                    'name': '接口监控',
                    'controller': '../../controller/views/monitor/interfaceMonitor.do'
                }, {
                    'name': '应用日志查询',
                    'controller': ''
                }, {
                    'name': '定时任务管理',
                    'controller': ''
                }]
            }]
        },
        /**
         * 为菜单绑定控制器
         */
        bindController: function () {
            var root = this;
            $('ul[lay-filter=' + root.option.navSelector + ']').find("dd").each(function (index, element) {
                for (var i in root.option.menuJson) {
                    for (var j in root.option.menuJson[i].children) {
                        if (root.option.menuJson[i].children[j].name == $(element).text()) {
                            $(element).data('controller', root.option.menuJson[i].children[j].controller);
                            break;
                        }
                    }
                }
            });

            /**
             * 注意，导航选项的href属性必须设置为javascript:;，否则会因为页面跳转而导致渲染失败
             */
            root.option.element.on('nav(' + root.option.navSelector + ')', function (elem) {
                elem.parents('.layui-nav-item').addClass('layui-this');
                $(root.option.titleContainerSelector).html('您正在使用[' + elem.text() + ']功能');
                $(root.option.viewContainerSelector).html('');
                if(elem.data('controller') != undefined && elem.data('controller') != '') {
                    root.getView(elem.data('controller'));
                }
            });
        },
        /**
         *  默认业务信息处理类
         */
        doDefaultService: function () {
            var root = this;
            $('ul[lay-filter=' + root.option.navSelector + ']').find('dd').first().trigger('click');
        },
        /**
         * 根据获取视图
         */
        getView: function (url) {
            var root = this;
            var loadIndex;
            $.ajax({
                url: url,
                method: 'POST',
                timeout: 30000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex);
                    $(root.option.viewContainerSelector).html(response);
                },
                error: function (XHR, status, error) {
                    root.option.layer.closeAll();
                    root.option.layer.alert('获取视图异常：' + error);
                }
            });
        },
        /**
         *  初始化
         */
        init: function (element, layer, navSelector, viewContainerSelector, titleContainerSelector) {
            var root = this;
            root.option.element = element;
            root.option.layer = layer;
            root.option.navSelector = navSelector;
            root.option.viewContainerSelector = viewContainerSelector;
            root.option.titleContainerSelector = titleContainerSelector;
        },
        /**
         * 生成菜单
         */
        drawMenu: function () {
            var root = this;
            // 生成菜单html
            var navHtml = '';
            for (var i in root.option.menuJson) {
                navHtml += '<li class="layui-nav-item">';
                navHtml += '<a href="javascript:;">' + root.option.menuJson[i].name + '</a>';
                navHtml += '<dl class="layui-nav-child">';
                for (var j in root.option.menuJson[i].children) {
                    navHtml += '<dd><a href="javascript:;">' + root.option.menuJson[i].children[j].name + '</a></dd>';
                }
                navHtml += '</dl>';
                navHtml += '</li>';
            }
            $('ul[lay-filter=' + root.option.navSelector + ']').html(navHtml);
            // 重新渲染菜单
            root.option.element.render('nav');
        }
    };
    return menuClass;
}
