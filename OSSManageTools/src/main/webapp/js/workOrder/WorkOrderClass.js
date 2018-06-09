/**
 * 工单操作类
 * @returns {{option: {}, init: init, finishWorkOrder: finishWorkOrder}}
 * @constructor
 */
function WorkOrderClass() {
    var workOrderClass = {
        option: {
            checkSVNList: new Array()
        },
        /**
         *
         */
        init: function (element, layer, developOrderId, workOrderId) {
            var root = this;
            root.option.element = element;
            root.option.layer = layer;
            root.option.developOrderId = developOrderId;
            root.option.workOrderId = workOrderId;
        },
        /**
         * 回单
         */
        finishWorkOrder: function () {
            var root = this;
            var svnLogIdList = new Array();
            for (var i in root.option.checkSVNList) {
                svnLogIdList.push(root.option.checkSVNList[i]['SVN_LOG_ID']);
            }
            var tester = $('#tester').val();
            var loadIndex;
            $.ajax({
                url: '../../service/develop/attachSVN.do',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    developOrderId: root.option.developOrderId,
                    workOrderId: root.option.workOrderId,
                    svnLogIdList: svnLogIdList,
                    tester: tester
                }),
                timeout: 30000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex);
                    if (response.meta.success) {
                        layer.confirm('提交代码成功', {
                            closeBtn: 0,
                            btn: ['关闭']
                        }, function (index, layero) {
                            $('button[data-type=cancel]').trigger('click');
                        });
                    } else {
                        layer.alert(response.meta.message);
                    }
                },
                error: function (XHR, status, error) {
                    layer.close(loadIndex);
                    layer.alert('提交代码成功异常：' + error);
                }
            });
        },
        /**
         * 将选中的版本放入列表中
         * @param svnLogId
         * @returns {boolean}
         */
        addSVNList: function (data) {
            var root = this;
            var addFlag = true;
            for (var i in root.option.checkSVNList) {
                if (data['SVN_LOG_ID'] == root.option.checkSVNList[i]['SVN_LOG_ID']) {
                    addFlag = false;
                }
            }

            if (addFlag) {
                root.option.checkSVNList.push(data);
            }
        },
        /**
         * 将反选的版本移出列表
         * @param data
         */
        remove: function (data) {
            var root = this;
            for (var i in root.option.checkSVNList) {
                if (data['SVN_LOG_ID'] == root.option.checkSVNList[i]['SVN_LOG_ID']) {
                    // 删除第i个项目
                    root.option.checkSVNList.splice(i, 1);
                }
            }
        },
        /**
         * 将选中的版本构造为页面展示的对象
         * @returns {{}}
         */
        getShowObject: function () {
            var root = this;
            var showObject = {};
            for (var i in root.option.checkSVNList) {
                var svnLogChangeList = root.option.checkSVNList[i]['SVN_LOG_CHANGE_LIST'];
                for (var j in svnLogChangeList) {
                    // 不展示目录
                    if (svnLogChangeList[j]['PATH'].indexOf('.') < 0) {
                        continue;
                    }

                    var project = svnLogChangeList[j]['PROJECT_NAME'];
                    if (project in showObject) {
                    } else {
                        showObject[project] = new Array();
                    }

                    var repeatIndex = root.getRepeatIndex(showObject[project], svnLogChangeList[j]);
                    if (repeatIndex > -1) {
                        // 如果变更文件已经存在列表中，则只变更类型
                        if (svnLogChangeList[j]['TYPE'] == 'A') {
                            showObject[project][repeatIndex]['type'] = svnLogChangeList[j]['TYPE_NAME'];
                        }
                        showObject[project][repeatIndex]['count'] = showObject[project][repeatIndex]['count'] + 1;
                    } else {
                        showObject[project].push({
                            'type': svnLogChangeList[j]['TYPE_NAME'],
                            'path': svnLogChangeList[j]['PATH'],
                            'count': 1
                        });
                    }
                }
            }
            return showObject;
        },
        /**
         * 获取重复数据的下标
         */
        getRepeatIndex: function (projectList, svnLogChangeObject) {
            var repeatIndex = -1;

            for (var i in projectList) {
                if (projectList[i]['path'] == svnLogChangeObject['PATH']) {
                    repeatIndex = i;
                    break;
                }
            }
            return repeatIndex;
        },
        /**
         * 校验选择列表是否为空
         * @returns {boolean}
         */
        verifyIsEmpty: function () {
            var root = this;
            var emptyFlag = true;
            if (root.option.checkSVNList.length > 0) {
                emptyFlag = false;
            }
            return emptyFlag;
        },
        /**
         * 转换代码路径，将svn的代码路径转换为远程下载用的地址
         * @param path
         * @param project
         * @returns {*}
         */
        transferPath: function(path, project) {
            if(project == '接口（未区分主应用还是手机接口）') {
                if(path.indexOf(".java") > -1) {
                    path = path.replace(/src\//g, 'ftp://iom_mobile@10.184.233.80/apache-tomcat-6.0.44_jk/webapps/IomInterface/WEB-INF/classes/');
                    path = path.replace(/\.java/g, '.class');
                } else {
                    if(path.indexOf("IomInterface/") > -1) {
                        path = path.replace(/IomInterface\//g, 'ftp://iom_mobile@10.184.233.80/apache-tomcat-6.0.44_jk/webapps/IomInterface/');
                    }
                }
            } else if(project == 'WEB主应用') {
                if(path.indexOf(".java") > -1) {
                    path = path.replace(/src\//g, 'ftp://iom_mobile@10.184.233.80/app/mainweb/IOMProjWeb/WEB-INF/classes/');
                    path = path.replace(/\.java/g, '.class');
                } else {
                    if(path.indexOf("IOMProjWeb/") > -1) {
                        path = path.replace(/IOMProjWeb\//g, 'ftp://iom_mobile@10.184.233.80/app/mainweb/IOMProjWeb/');
                    }
                }
            }
            return path
        },
        /**
         * 转派工单
         * @param postData
         */
        transferWorkOrder: function (postData, url) {
            var root = this;
            var loadIndex;
            $.ajax({
                url: url,
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: postData,
                timeout: 30000,
                beforeSend: function () {
                    loadIndex = root.option.layer.load(0);
                },
                success: function (response) {
                    root.option.layer.closeAll();
                    if (response.meta.success) {
                        root.option.layer.alert('转派成功' + response.data.transferCount + '张任务单');
                        $('button[data-type=search]').trigger('click');
                    } else {
                        layer.alert(response.meta.message);
                    }
                },
                error: function (XHR, status, error) {
                    root.option.layer.close(loadIndex);
                    root.option.layer.alert('转派异常：' + error);
                }
            });
        }
    };
    return workOrderClass;
};