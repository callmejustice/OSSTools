package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.manage.webservice.service.WebserviceClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Description: webservice接口业务控制器
 * @author: huang.jing
 * @Date: 2018/2/11 0011 - 17:47
 */
@RestController
@RequestMapping("/controller/service/webservice")
public class WebserviceServiceController {

    private static Logger log = LogManager.getLogger(WebserviceServiceController.class);

    /**
     * 接口响应速度监控
     * @param httpSession
     * @param speedMonitorJson
     * @return
     */
    @RequestMapping(value = "/speedMonitor.do", method = RequestMethod.POST)
    public @ResponseBody
    Response speedMonitor(HttpSession httpSession, @RequestBody JSONObject speedMonitorJson) {
        log.info("speedMonitor入口……");
        Response response = new Response();

        try {
            // 调用webservice接口
            JSONObject paramJson = new JSONObject();
            paramJson.put("webserviceBeanName", speedMonitorJson.getString("resourceWebserviceBuilder"));
            paramJson.put("localPartName", speedMonitorJson.getString("localPartName"));
            paramJson.put("requestXML", speedMonitorJson.getString("requestXML"));

            WebserviceClient webserviceClient = (WebserviceClient) BeanFactory.getApplicationContext().getBean("webserviceClient");
            response.success(webserviceClient.callWebservice(paramJson));
        } catch (Exception e) {
            log.error("调用webservice接口异常", e);
            response.failure(e.getMessage());
        }
        log.info("speedMonitor出口……");
        return response;
    }

}
