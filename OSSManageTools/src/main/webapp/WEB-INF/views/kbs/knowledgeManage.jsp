<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%
    String userName = "";
    try {
        JSONObject loginSessionObject = (JSONObject) request.getSession().getAttribute("ossLoginUser");
        userName = loginSessionObject.getString("userName");
    } catch (Exception e) {
    }
%>
<style>
    .layui-quote-nm {
        width: 45%;
        float: left;
        margin-right: 15px;
    }

    .layui-quote-nm p span {
        color: #5FB878
    }

    .layui-quote-nm p.knowledge-title {
        max-height: 40px;
        overflow: hidden;
    }

    .layui-quote-nm p.knowledge-desc {
        max-height: 40px;
        overflow: hidden;
    }

    .layui-quote-nm p.knowledge-title .title {
        color: #0C0C0C;
    }
</style>
<div class="layui-row">
    <div class="layui-col-lg3" id="knowledgeForm">
        <fieldset class="layui-elem-field">
            <legend>查询条件</legend>
            <div class="layui-form" style="padding: 10px;">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">知识类型</label>
                        <div class="layui-input-inline">
                            <select name="knowledgeType" id="knowledgeType" lay-filter="knowledgeType">
                                <option value="">全部</option>
                                <optgroup label="日常卡单处理">
                                    <option value="rckd_tsgd">投诉工单</option>
                                    <option value="rckd_kd">宽带</option>
                                    <option value="rckd_mbh">魔百盒</option>
                                    <option value="rckd_xwkd">小微宽带</option>
                                    <option value="rckd_zag">障碍改</option>
                                    <option value="rckd_bcd">拨测单</option>
                                    <option value="rckd_ims">IMS工单</option>
                                    <option value="rckd_bzd">报障单</option>
                                    <option value="rckd_qt">其他</option>
                                </optgroup>
                                <optgroup label="EOMS工单处理">
                                    <option value="eoms_tsgd">投诉工单</option>
                                    <option value="eoms_wbgd">维保工单</option>
                                    <option value="eoms_tygd">通用工单</option>
                                    <option value="eoms_jqrpd">机器人派单</option>
                                    <option value="eoms_qt">其他</option>
                                </optgroup>
                                <optgroup label="系统类">
                                    <option value="sys_xtjkxzbjc">系统健康性指标核查</option>
                                    <option value="sys_cycqdcz">常用重启等操作</option>
                                </optgroup>
                                <optgroup label="提取数据脚本">
                                    <option value="tqsjjb_tqsjjb">提取数据脚本</option>
                                </optgroup>
                                <optgroup label="工具使用">
                                    <option value="gjsy_gjsy">工具使用</option>
                                </optgroup>
                            </select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">关键字</label>
                        <div class="layui-input-inline">
                            <input type="text" id="keyWord" id="keyWord" placeholder="请输入关键字" class="layui-input">
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">作者</label>
                        <div class="layui-input-inline">
                            <input type="text" id="author" id="author" placeholder="请输入作者" class="layui-input">
                        </div>
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn layui-bg-blue" data-type="search">查询</button>
                    </div>
                </div>
            </div>
        </fieldset>
        <fieldset class="layui-elem-field">
            <legend>知识库管理</legend>
            <div class="layui-form" style="padding: 10px;">
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn layui-bg-blue" data-type="add">录入知识点</button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>
    <div class="layui-col-lg9">
        <fieldset class="layui-elem-field">
            <legend>知识点列表</legend>
            <div class="layui-row" id="interfaceResult" style="height: 680px; padding: 10px; overflow: auto"></div>
        </fieldset>
    </div>
</div>
<script>
    var userName = '<%=userName%>';
    layui.use(['layer', 'form', 'element'], function () {
        var form = layui.form
            , layer = layui.layer;
        form.render();

        var $ = layui.$, active = {
            search: function () {
                var loadIndex = -1;
                var keyWord = $('#keyWord').val();
                var knowledgeType = $('#knowledgeType').val();
                var author = $('#author').val();
                $.ajax({
                    url: '../../controller/service/kbs/getKnowledge.do',
                    method: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    data: JSON.stringify({
                        knowledgeType: knowledgeType,
                        keyWord: keyWord,
                        author: author
                    }),
                    timeout: 120000,
                    beforeSend: function () {
                        loadIndex = layer.load(0);
                    },
                    success: function (response) {
                        layer.close(loadIndex);
                        if (response.meta.success) {
                            var knowledgeList = response.data.knowledgeList;

                            var html = '';
                            for (var i in knowledgeList) {
                                html += '<blockquote class="layui-elem-quote layui-quote-nm" id="' + knowledgeList[i].KNOWLEDGE_ID + '">';
                                html += '<a href="/OSSManageTools/' + knowledgeList[i].KNOWLEDGE_FILE + '" target="_blank">';
                                html += '<p class="knowledge-title"><span>知识点名称：</span><span class="title">' + knowledgeList[i].KNOWLEDGE_TITLE + '</span></p>';
                                html += '<p class="knowledge-desc"><span>知识点简介：</span>' + knowledgeList[i].KNOWLEDGE_DESC + '</p>';
                                html += '<p><span>知识点标签：</span><span class="layui-badge layui-bg-blue">' + $('#knowledgeType option[value=' + knowledgeList[i].KNOWLEDGE_TYPE + ']').text() + '</span>&nbsp;&nbsp;<span class="layui-badge layui-bg-blue">' + knowledgeList[i].AUTHOR + '</span>&nbsp;&nbsp;<span class="layui-badge layui-bg-blue">' + knowledgeList[i].MODIFY_DATE + '</span></p>';
                                html += '</a>';
                                if (userName == knowledgeList[i].AUTHOR) {
                                    html += '<p style="text-align: right"><button class="layui-btn layui-btn-primary layui-btn-xs" name="edit" title="编辑"><i class="layui-icon"></i></button>&nbsp;<button class="layui-btn layui-btn-primary layui-btn-xs" name="delete" title="删除"><i class="layui-icon"></i></button></p>';
                                } else {
                                    html += '<p style="text-align: right">&nbsp;</p>';
                                }

                                html += '</blockquote>';
                            }
                            $('#interfaceResult').html(html);

                            $('#interfaceResult').find('.layui-elem-quote').find('.layui-btn').click(function () {
                                if ($(this).attr('name') == 'edit') {
                                    editKnowledge($(this).parents('blockquote').attr('id'));
                                } else if ($(this).attr('name') == 'delete') {
                                    deleteKnowledge($(this).parents('blockquote').attr('id'));
                                }
                            });
                        } else {
                            layer.alert('查询知识点失败')
                        }
                    },
                    error: function (XHR, status, error) {
                        layer.close(loadIndex);
                        layer.alert('查询知识点异常：' + error);
                    }
                });
            },
            add: function () {
                layer.open({
                    area: ['500px', '520px'],
                    content: '../../controller/views/kbs/createKnowledge.do',
                    skin: 'custom-content',
                    title: '录入知识点',
                    type: 2
                });
            }
        };

        $('#knowledgeForm').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

    });

    function editKnowledge(knowledgeId) {
        layer.open({
            area: ['500px', '520px'],
            content: '../../controller/views/kbs/editKnowledge.do?knowledgeId=' + knowledgeId,
            skin: 'custom-content',
            title: '编辑知识点',
            type: 2
        });
    }

    function deleteKnowledge(knowledgeId) {
        var title = $('#' + knowledgeId).find('p.knowledge-title .title').text();
        layer.confirm('确认要删除[' + title + ']吗？', {
            btn: ['确定', '关闭']
        }, function () {
            var loadIndex = -1;
            $.ajax({
                url: '../../controller/service/kbs/deleteKnowledge.do',
                method: 'POST',
                contentType: 'application/json',
                dataType: 'json',
                data: JSON.stringify({
                    knowledgeId: knowledgeId
                }),
                timeout: 120000,
                beforeSend: function () {
                    loadIndex = layer.load(0);
                },
                success: function (response) {
                    layer.close(loadIndex);
                    if (response.meta.success) {
                        layer.alert('删除知识点成功');
                        $('body').find('button[data-type=search]').trigger('click');
                    } else {
                        layer.alert('删除知识点失败');
                    }
                },
                error: function (XHR, status, error) {
                    layer.close(loadIndex);
                    layer.alert('删除知识点异常：' + error);
                }
            });
        }, function () {
            layer.closeAll();
        });
    }


    Date.prototype.Format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1,                 //月份
            "d+": this.getDate(),                    //日
            "h+": this.getHours(),                   //小时
            "m+": this.getMinutes(),                 //分
            "s+": this.getSeconds(),                 //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds()             //毫秒
        };
        if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }
</script>