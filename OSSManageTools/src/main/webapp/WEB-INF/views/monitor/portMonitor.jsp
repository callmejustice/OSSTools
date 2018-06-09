<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    String startTime = sdf.format(cal.getTime());
%>
<div class="layui-row">
    <div class="layui-col-lg3">
        <fieldset class="layui-elem-field">
            <legend>连接情况查询条件</legend>
            <div class="layui-form" id="gatherForm" style="padding: 10px;">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">应用类型</label>
                        <div class="layui-input-inline">
                            <select name="gatherMachineDesc" id="gatherMachineDesc">
                                <option value="手机平台" selected>手机平台</option>
                                <option value="主应用">主应用</option>
                                <option value="主应用接口">主应用接口</option>
                                <option value="手机接口">手机接口</option>
                                <option value="主应用报表">主应用报表</option>
                                <option value="IOS应用">IOS应用</option>
                            </select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">连接类型</label>
                        <div class="layui-input-inline">
                            <select name="gatherName" id="gatherName">
                                <option value="ESTABLISHED" selected>ESTABLISHED</option>
                                <option value="TIME_WAIT">TIME_WAIT</option>
                                <option value="CLOSE_WAIT">CLOSE_WAIT</option>
                            </select>
                        </div>
                    </div>
                    <div class="layui-inline">
                        <label class="layui-form-label">起始时间</label>
                        <div class="layui-input-inline">
                            <input type="text" name="startTime" autocomplete="off"
                                   placeholder="请选择起始时间" class="layui-input" id="startTime" readonly="readonly"
                                   value="<%=startTime%>">
                        </div>
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn layui-bg-blue" data-type="search">搜索</button>
                    </div>
                </div>
            </div>
        </fieldset>
    </div>
    <div class="layui-col-lg9">
        <fieldset class="layui-elem-field">
            <legend>连接情况监控</legend>
            <div class="layui-field-box" id="portListenChart" style="min-height: 680px; max-height: 700px;"></div>
        </fieldset>
    </div>
</div>
<script>
    var myChart = echarts.init(document.getElementById('portListenChart'));
    myChart.setOption(getOption());

    layui.use(['layer', 'form', 'element'], function () {
        var form = layui.form
            , laydate = layui.laydate
            , layer = layui.layer;
        form.render();

        var $ = layui.$, active = {
            search: function () {
                var loadIndex = -1;
                var gatherName = $('#gatherName').val();
                var gatherMachineDesc = $('#gatherMachineDesc').val();
                var startTime = $('#startTime').val();
                var endTime = $('#endTime').val();
                $.ajax({
                    url: '../../controller/service/monitor/getGatherInfo.do',
                    method: 'POST',
                    contentType: 'application/json',
                    dataType: 'json',
                    data: JSON.stringify({
                        gatherType: 'portListen',
                        gatherMachineDesc: gatherMachineDesc,
                        gatherName: gatherName,
                        startTime: startTime
                    }),
                    timeout: 30000,
                    beforeSend: function () {
                        loadIndex = layer.load(0);
                    },
                    success: function (response) {
                        layer.close(loadIndex);
                        if (response.meta.success) {
                            var series = new Array();
                            var legend = new Array();
                            for (var i in response.data) {

                                var data = new Array();
                                for (var j in response.data[i]) {
                                    data.push([new Date(response.data[i][j].gatherTime.replace(/-/g, "/")), parseInt(response.data[i][j].gatherValue), response.data[i][j].gatherMachineDesc]);
                                }

                                legend.push(i);
                                series.push({
                                    name: i,
                                    type: 'line',
                                    showSymbol: false,
                                    markPoint: {
                                        data: [
                                            {name: '最高连接数', type: 'max'},
                                            {name: '最低连接数', type: 'min'}
                                        ]
                                    },
                                    data: data
                                });

                                myChart.setOption({
                                    legend: {
                                        data: legend
                                    },
                                    series: series
                                });
                            }
                        } else {
                            layer.alert('获取连接数情况失败')
                        }
                    },
                    error: function (XHR, status, error) {
                        layer.close(loadIndex);
                        layer.alert('获取连接数情况异常：' + error);
                    }
                });
            }
        };

        laydate.render({
            elem: '#startTime',
            theme: 'custom-theme',
            showBottom: false,
            trigger: 'click',
            mark: versionJson,
            done: function (value, date) {
            }
        });

        $('#gatherForm').find('.layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        }).filter('[data-type=search]').trigger('click');

        // 每3分钟自动查询
        var t = setInterval('autoSearch();', 3 * 60000);
    });

    function autoSearch() {
        $('#gatherForm').find('.layui-btn[data-type=search]').trigger('click')
    }

    function getOption() {
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    if (params.value.length > 1) {
                        var date = new Date(params.value[0]);
                        data = date.getFullYear() + '-'
                            + (date.getMonth() + 1) + '-'
                            + date.getDate() + ' '
                            + date.getHours() + ':'
                            + date.getMinutes();
                        return data + '<br/>' + params.value[1] + ', ' + params.value[2];
                    } else {
                        return params.name + ':' + params.value;
                    }

                }
            },
            dataZoom: {
                show: true,
                start: 50
            },
            xAxis: [
                {
                    type: 'time',
                    splitNumber: 24
                }
            ],
            yAxis: [
                {
                    type: 'value'
                }
            ]
        };
        return option;
    }
</script>