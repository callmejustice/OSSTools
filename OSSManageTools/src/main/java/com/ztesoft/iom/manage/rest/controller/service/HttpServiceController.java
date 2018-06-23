package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.HttpKit;
import com.ztesoft.iom.common.vo.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Description: http接口控制器
 * @author: huang.jing
 * @Date: 2018/4/17 0017 - 15:05
 */
@RestController
@RequestMapping("/controller/service/http")
public class HttpServiceController {

    private static Logger log = LogManager.getLogger(HttpServiceController.class);

    /**
     * 接口响应速度监控
     * @param httpSession
     * @param speedMonitorJson
     * @return
     */
    @RequestMapping(value = "/speedMonitor.do",  method = RequestMethod.POST)
    public @ResponseBody
    Response speedMonitor(HttpSession httpSession, @RequestBody JSONObject speedMonitorJson) {
        Response response = new Response();

        JSONObject returnJson = new JSONObject();
        boolean callFlag = false;
        Object callResult = null;

        // 接口调用起始时间
        long startMillis = -1;
        // 接口调用结束时间
        long endMillis = -1;
        String errorInfo = "";

        try {

            startMillis = System.currentTimeMillis();
            callResult = HttpKit.post(speedMonitorJson.getString("url"), speedMonitorJson.getString("requestXML"));

            endMillis = System.currentTimeMillis();
            callFlag = true;
        } catch (Exception e) {
            log.error("调用http接口异常", e);
            callFlag = false;
            errorInfo = e.getMessage();
        } finally {
            returnJson.put("callFlag", callFlag);
            returnJson.put("callResult", callResult);
            returnJson.put("startMillis", startMillis);
            returnJson.put("endMillis", endMillis);
            returnJson.put("errorInfo", errorInfo);
        }

        response.success(returnJson);
        log.info("speedMonitor出口……");
        return response;
    }



}
