package com.ztesoft.iom.manage.webservice.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/5/22 0022 - 0:20
 */
public class WebserviceClientLocal {

    private static Logger log = LogManager.getLogger(WebserviceClientLocal.class);

    public JSONObject callWebservice(JSONObject buildJson) {
        System.out.println("callWebservice入口……");
        JSONObject returnJson = new JSONObject();
        boolean callFlag = false;
        Object callResult = null;

        // 接口调用起始时间
        long startMillis = -1;
        // 接口调用结束时间
        long endMillis = -1;
        String errorInfo = "";
        try {
            // 使用RPC方式调用WebService
            RPCServiceClient serviceClient = new RPCServiceClient();
            // 指定调用WebService的URL
            EndpointReference targetEPR = new EndpointReference(buildJson.getString("requestUrl"));
            Options options = serviceClient.getOptions();
            // 超时时间
            options.setTimeOutInMilliSeconds(buildJson.getLongValue("outTime"));
            // 确定目标服务地址
            options.setTo(targetEPR);

            /**
             * 指定要调用的方法及WSDL文件的命名空间
             * 如果 webservice 服务端由axis2编写
             * 命名空间 不一致导致的问题
             * org.apache.axis2.AxisFault: java.lang.RuntimeException: Unexpected subelement arg0
             */
            QName qname = new QName(buildJson.getString("namespaceURI"), buildJson.getString("localPart"));
            // 请求参数
            Object[] parameters = buildJson.getObject("requestObject", Object[].class);

            startMillis = System.currentTimeMillis();
            // 远程调用
            callResult = serviceClient.invokeBlocking(qname, parameters, buildJson.getObject("returnTypes", Class[].class));
            endMillis = System.currentTimeMillis();
            callFlag = true;
        } catch (Exception e) {
            callFlag = false;
            errorInfo = e.getMessage();
            System.out.println("调用webservice接口异常：" + e.getMessage());
        } finally {
            returnJson.put("callFlag", callFlag);
            returnJson.put("callResult", callResult);
            returnJson.put("startMillis", startMillis);
            returnJson.put("endMillis", endMillis);
            returnJson.put("errorInfo", errorInfo);
        }

        System.out.println("callWebservice出口……");
        return returnJson;
    }

    public static void main(String[] args) {
        WebserviceClientLocal webserviceClient = new WebserviceClientLocal();
        String qryJson = "{}";
        JSONObject buildJson = new JSONObject();
        buildJson.put("requestUrl", "http://10.184.233.80:18020/IomInterface/services/ConstructOrderService?wsdl");
        buildJson.put("outTime", 10000l);
        buildJson.put("namespaceURI", "http://server.ws.mobile.gx.oss.ztesoft.com");
        buildJson.put("localPart", "ConstructOrderServiceService");
        buildJson.put("requestObject", new Object[]{qryJson});
        buildJson.put("returnTypes", new Class[]{String.class});
        System.out.println(webserviceClient.callWebservice(buildJson));
    }
}
