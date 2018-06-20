package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.manage.monitor.service.MonitorService;
import com.ztesoft.iom.manage.rest.dao.ConfigDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Description: 系统监控业务控制器
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 17:10
 */
@RestController
@RequestMapping("/controller/service/monitor")
public class MonitorServiceController {

    private static Logger log = LogManager.getLogger(MonitorServiceController.class);

    /**
     * 获取采集信息
     * @param httpSession
     * @param qryJson
     * @return
     */
    @RequestMapping(value = "/getGatherInfo.do", method = RequestMethod.POST)
    public @ResponseBody
    Response getGatherInfo(HttpSession httpSession, @RequestBody JSONObject qryJson) {
        log.info("getGatherInfo入口……");
        Response response = new Response();
        MonitorService monitorService = (MonitorService) BeanFactory.getApplicationContext().getBean("monitorService");
        JSONObject gatherJson = monitorService.getGatherInfo(qryJson);
        if(gatherJson.getBoolean("serviceFlag")) {
            response.success(gatherJson.getJSONObject("gatherInfo"));
        } else {
            response.failure("获取采集信息失败");
        }
        log.info("getGatherInfo出口……");
        return response;
    }

    @RequestMapping(value = "/getInterfaceTypeConfig.do", method = RequestMethod.POST)
    public @ResponseBody
    Response getInterfaceTypeConfig(HttpSession httpSession, @RequestBody JSONObject qryJson) {
        log.info("getGatherInfo入口……");
        Response response = new Response();
        ConfigDAO configDAO = (ConfigDAO) BeanFactory.getApplicationContext().getBean("configDAOImpl");
        response.success(JSON.toJSONString(configDAO.getInterfaceTypeConfig()));
        return response;
    }

    @RequestMapping(value = "/getInterfaceNameConfig.do", method = RequestMethod.POST)
    public @ResponseBody
    Response getInterfaceNameConfig(HttpSession httpSession, @RequestBody JSONObject qryJson) {
        log.info("getGatherInfo入口……");
        Response response = new Response();
        ConfigDAO configDAO = (ConfigDAO) BeanFactory.getApplicationContext().getBean("configDAOImpl");
        response.success(JSON.toJSONString(configDAO.getInterfaceNameConfig(qryJson.get("code"))));
        return response;
    }

}
