package com.ztesoft.iom.manage.webservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.param.dao.ParamConfigDao;
import com.ztesoft.iom.manage.param.service.ParamMappingService;
import com.ztesoft.iom.manage.webservice.service.WebserivceBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 资源系统webservcice接口构造类
 * @author: huang.jing
 * @Date: 2018/2/9 0009 - 11:10
 */
public class ResourceWebserviceBuilder implements WebserivceBuilder {

    private static Logger log = LogManager.getLogger(ResourceWebserviceBuilder.class);

    /**
     * 构造请求参数
     *
     * @param paramJson
     * @return
     */
    @Override
    public JSONObject buildWebserivceParam(JSONObject paramJson) throws Exception {
        log.info("buildWebserivceParam入口……");
        JSONObject buildJson = new JSONObject();

        ParamMappingService paramMappingService = (ParamMappingService) BeanFactory.getApplicationContext().getBean("paramMappingService");
        ParamConfigDao paramConfigDao = (ParamConfigDao) BeanFactory.getApplicationContext().getBean("paramConfigDaoImpl");

        String localPart = paramMappingService.getMappingValueNoDefault("RESJK", MapUtils.getString(paramJson, "localPartName"));
        String requestXML = MapUtils.getString(paramJson, "requestXML");
        Map qryMap = new HashMap();
        qryMap.put("paramConfigName", "resourceWebserviceAddress");
        String requestUrl = paramConfigDao.getParamConfigValue(qryMap);
        if("".equals(localPart)) {
            throw new Exception("接口名[" + MapUtils.getString(paramJson, "localPartName") + "]没有配置");
        } else if("".equals(requestXML)) {
            throw new Exception("接口名报文不能为空");
        }

        // 请求参数
        buildJson.put("requestObject", new Object[]{requestXML});
        // 返回参数类型
        buildJson.put("returnTypes", new Class[]{String.class});
        // 接口地址
        buildJson.put("requestUrl", requestUrl);
        // 命名空间
        buildJson.put("namespaceURI", "http://server.irms.boco.com");
        // 请求方法
        buildJson.put("localPart", localPart);
        // 超时时间
        buildJson.put("outTime", 120000);

        log.info("buildWebserivceParam出口……");
        return buildJson;
    }
}
