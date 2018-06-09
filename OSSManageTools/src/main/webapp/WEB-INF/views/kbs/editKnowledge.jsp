<%@ page contentType="text/html;charset=utf-8" %>
<%
    String knowledgeId = request.getAttribute("knowledgeId") == null ? "" : (String) request.getAttribute("knowledgeId");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>广西移动家宽管理平台-录入知识点</title>
    <link rel="stylesheet" href="../../../js/ui/layui/css/layui.css">
    <link rel="stylesheet" href="../../../css/custom.css">
    <style>
        body {
            padding: 10px;
        }
    </style>
</head>
<body class="layui-layout-body">
<div class="layui-row">
    <div class="layui-col-lg6">
        <form class="layui-form layui-form-pane" action="">
            <div class="layui-form-item" style="display: none">
                <label class="layui-form-label"><span class="red">*</span>知识点ID</label>
                <div class="layui-input-block">
                    <input type="text" name="knowledgeId" id="knowledgeId" lay-verify="required" autocomplete="off"
                           placeholder="知识点ID" class="layui-input" value="<%=knowledgeId%>">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">知识点类型</label>
                <div class="layui-input-block">
                    <select name="knowledgeType" id="knowledgeType" lay-verify="required">
                        <option value="">请选择类型</option>
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
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="red">*</span>知识点标题</label>
                <div class="layui-input-block">
                    <input type="text" name="knowledgeTitle" id="knowledgeTitle" lay-verify="required"
                           autocomplete="off"
                           placeholder="知识点标题" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="red">*</span>知识点描述</label>
                <div class="layui-input-block">
                    <textarea placeholder="知识点描述" lay-verify="required" class="layui-textarea" id="knowledgeDesc"
                              name="knowledgeDesc"></textarea>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="red">*</span>知识点内容</label>
                <div class="layui-input-block">
                    <button type="button" class="layui-btn layui-btn-danger" id="uploadKnowledge" style="width: 100%">
                        <i class="layui-icon">&#xe67c;</i>上传文件
                    </button>
                    <input type="hidden" name="knowledgeFileName" lay-verify="required" placeholder="知识点内容"
                           id="knowledgeFileName" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label" style="display: none;"></label>
                <div class="layui-input-block red">只能上传doc、docx后缀的文件</div>
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
    layui.use(['form', 'laydate', 'layer', 'upload'], function () {
        // 只有执行了这一步，部分表单元素才会自动修饰成功
        var form = layui.form
            , laydate = layui.laydate
            , layer = layui.layer
            , upload = layui.upload;

        form.render();

        form.on('submit(submit)', function (data) {
            var loadIndex;
            $.ajax({
                url: '../../service/kbs/editKnowledge.do',
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
                        layer.alert('编辑知识点成功');
                        window.parent.$('body').find('button[data-type=search]').trigger('click');
                        window.parent.$('body').find('.layui-layer-iframe a.layui-layer-close').trigger('click');
                    } else {
                        layer.alert(response.meta.message);
                    }
                },
                error: function (XHR, status, error) {
                    layer.close(loadIndex);
                    layer.alert('编辑知识点异常：' + error);
                }
            });

            return false; // 阻止表单跳转。如果需要表单跳转，去掉这段即可。
        });

        //执行实例
        var uploadInst = upload.render({ //允许上传的文件后缀
            elem: '#uploadKnowledge',
            url: '../../service/kbs/uploadKnowledge.do',
            accept: 'file',
            // 只允许上传xls
            exts: 'doc|docx',
            // obj参数包含的信息，跟 choose回调完全一致，可参见上文。
            before: function (obj) {
                layer.load();
            },
            done: function (res) {
                layer.closeAll();
                if (res.meta.success) {
                    $('#knowledgeFileName').val(res.data.fileName);
                } else {
                    layer.alert(res.meta.message);
                }
            },
            error: function (index, upload) {
                layer.closeAll();
            }
        });

        $.ajax({
            url: '../../service/kbs/getKnowledge.do',
            method: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                knowledgeId: '<%=knowledgeId%>'
            }),
            timeout: 120000,
            beforeSend: function () {
                loadIndex = layer.load(0);
            },
            success: function (response) {
                layer.close(loadIndex);
                if (response.meta.success) {
                    var knowledgeList = response.data.knowledgeList;
                    $('#knowledgeType').val(knowledgeList[0].KNOWLEDGE_TYPE).trigger('click');
                    $('#knowledgeTitle').val(knowledgeList[0].KNOWLEDGE_TITLE);
                    $('#knowledgeDesc').val(knowledgeList[0].KNOWLEDGE_DESC);
                    $('#knowledgeFileName').val(knowledgeList[0].KNOWLEDGE_FILE);
                    form.render();
                } else {
                    layer.alert('查询知识点失败，无法进行编辑');
                    window.parent.$('body').find('.layui-layer-iframe a.layui-layer-close').trigger('click');
                }
            },
            error: function (XHR, status, error) {
                layer.close(loadIndex);
                layer.alert('查询知识点异常：' + error);
                window.parent.$('body').find('.layui-layer-iframe a.layui-layer-close').trigger('click');
            }
        });
    });
</script>
</body>
</html>