<%@ page contentType="text/html;charset=utf-8" %>
<div class="layui-row">
    <div class="layui-col-lg3">
        <fieldset class="layui-elem-field">
            <legend>接口监控查询条件</legend>
            <div class="layui-form" id="interfaceForm" style="padding: 10px;">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">接口类型</label>
                        <div class="layui-input-inline">
                            <select name="resourceWebserviceBuilder" id="resourceWebserviceBuilder" lay-filter="resourceWebserviceBuilder">
                                <option value="resourceWebserviceBuilder" selected>资源接口</option>
                                <option value="eomsWebserviceBuilder">EOMS接口</option>
                            </select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">接口名称</label>
                        <div class="layui-input-inline">
                            <select name="localPartName" id="localPartName" lay-filter="localPartName"></select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">请求报文</label>
                        <div class="layui-input-inline">
                            <textarea placeholder="请输入内容" class="layui-textarea" id="requestXML"
                                      name="requestXML"></textarea>
                        </div>
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn layui-bg-blue" data-type="search">模拟请求</button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>
    <div class="layui-col-lg9">
        <fieldset class="layui-elem-field">
            <legend>接口监控查询情况</legend>
            <div class="layui-row" id="interfaceResult" style="height: 680px; padding: 10px; overflow: auto"></div>
        </fieldset>
    </div>
</div>
<script>
    var localPartNameOptions = [
        {'interfaceType': 'webservice', 'type': 'resourceWebserviceBuilder', 'value': 'getUserAddressByTeleNo', 'text': '4.15根据宽带账号查询用户安装地址和标准地址', 'requestInfo': '<?xml version="1.0" encoding="UTF-8"?><Data><teleNo>13978878884</teleNo></Data>'},
        {'interfaceType': 'webservice', 'type': 'resourceWebserviceBuilder', 'value': 'getEachLevelAddress', 'text': '4.16查询各级标准地址接口', 'requestInfo': '<?xml version="1.0" encoding="UTF-8"?><Data><AddrFullName>广西桂林平乐县张家镇榕津村委鸡仔岭村12号侧墙GF0015</AddrFullName><orderId>11718180</orderId></Data>'},
        {'interfaceType': 'webservice', 'type': 'resourceWebserviceBuilder', 'value': 'getStandardAddress', 'text': '根据标准地址查询用户的城乡标识接口', 'requestInfo': '<?xml version="1.0" encoding="UTF-8"?><Data><standardAddress>广西柳州融安县大将镇富乐村大将富乐村片区七屯6号左12民房</standardAddress></Data>'},
        {'interfaceType': 'webservice', 'type': 'eomsWebserviceBuilder', 'value': 'HomeBroadbandComplaint', 'text': '投诉待办查询接口', 'requestInfo': '{"requestJson":{"opTime":"2018-04-08 13:22:45","sortFieldDeal":"DESC","complaintType":"","sheetId":"","sortField":"SENDTIME","address":"","currentPageIndex":0,"opUserName":"廖国桥","serialNO":"","pageSize":10,"importantCust":"all","opUserId":"liaoguoqiao"},"opType":"gx_op101"}'},
        {'interfaceType': 'webservice', 'type': 'eomsWebserviceBuilder', 'value': 'HomeBroadbandFaultService ', 'text': '网络故障代办', 'requestInfo': '{"requestJson":{"orderState":"unProcessed","importantFlag":"all","sortFieldDeal":"DESC","accountNumber":"","sortField":"SENDTIME","currentPageIndex":0,"pageSize":10,"operateUserId":"liaoguoqiao","keyWord":""},"opType":"sheetListToQuery"}'}
    ];
    var requestUrl = '';

    layui.use(['layer', 'form', 'element'], function () {
        var element = layui.element
            , form = layui.form
            , layer = layui.layer;

        var $ = layui.$, active = {
            search: function () {
                var loadIndex = -1;
                var resourceWebserviceBuilder = $('#resourceWebserviceBuilder').val();
                var localPartName = $('#localPartName').val();
                var requestXML = $('#requestXML').val();

                $.ajax({
                    url: requestUrl,
                    method: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    data: JSON.stringify({
                        resourceWebserviceBuilder: resourceWebserviceBuilder,
                        localPartName: localPartName,
                        requestXML: requestXML
                    }),
                    timeout: 120000,
                    beforeSend: function () {
                        loadIndex = layer.load(0);
                    },
                    success: function (response) {
                        layer.close(loadIndex);
                        if (response.meta.success) {
                            var callFlag = response.data.callFlag;
                            var callResult = response.data.callResult;
                            var endMillis = response.data.endMillis;
                            var startMillis = response.data.startMillis;
                            var callLength = endMillis - startMillis;

                            var html = '<blockquote class="layui-elem-quote layui-quote-nm">';
                            html += '接口名：' + $('#localPartName option:selected').text() + '</br>';
                            html += '请求时间：' + new Date().Format("yyyy-MM-dd hh:mm:ss") + '</br>';
                            if (callFlag) {
                                html += '请求报文：<span name="request"></span></br>';
                                html += '返回报文：<span name="response"></span></br>';

                                var color = 'color: #5FB878';
                                if(callLength >= 5000) {
                                    color = 'color: #FF5722';
                                }
                                html += '响应时长：<span style="font-weight: bold; ' + color + '">' + callLength + '（毫秒）</span></br>';
                            } else {
                                html += '请求失败：' + response.data.errorInfo;
                            }
                            html += '</blockquote>';

                            $('#interfaceResult').prepend(html);
                            $('#interfaceResult').find('blockquote:first').find('[name=request]').text(requestXML);
                            $('#interfaceResult').find('blockquote:first').find('[name=response]').text(callResult);
                        } else {
                            layer.alert('模拟接口请求失败')
                        }
                    },
                    error: function (XHR, status, error) {
                        layer.close(loadIndex);
                        layer.alert('模拟接口请求异常：' + error);
                    }
                });
            }
        };
        // 监听下拉框变事件
        form.on('select(resourceWebserviceBuilder)', function (data) {
            setLocalPartName(data);
            element.init();
            form.render();
        });
        form.on('select(localPartName)', function (data) {
            setRequestInro(data);
        });

        $('#interfaceForm').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });

        // 默认加载资源接口
        setLocalPartName({'value': 'resourceWebserviceBuilder'});
        // 渲染
        element.init();
        form.render();

    });

    /**
     * 设置请求报文文本框的内容
     * @param data
     */
    function setRequestInro(data) {
        var requestXML = ''
        for(var i = 0; i < localPartNameOptions.length; i ++) {
            if(localPartNameOptions[i].value == data.value) {
                requestXML = localPartNameOptions[i].requestInfo;

                if(localPartNameOptions[i].interfaceType == 'webservice') {
                    requestUrl = '../../controller/service/webservice/speedMonitor.do';
                } if(localPartNameOptions[i].interfaceType == 'http') {
                    requestUrl = '../../controller/service/http/speedMonitor.do';
                }
            }
        }
        $('#requestXML').val(requestXML);
    }

    /**
     * 设置接口名称下拉框的内容
     * @param data
     */
    function setLocalPartName(data) {
        var options = '';
        for(var i = 0; i < localPartNameOptions.length; i ++) {
            if(localPartNameOptions[i].type == data.value) {
                options += '<option value="' + localPartNameOptions[i].value + '" >' + localPartNameOptions[i].text + '</option>';
            }
        }
        $('#localPartName').html(options);

        setRequestInro({'value': $('#localPartName').val()});
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