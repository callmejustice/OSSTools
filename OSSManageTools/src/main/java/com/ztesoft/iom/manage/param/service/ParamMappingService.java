package com.ztesoft.iom.manage.param.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.param.dao.ParamMappingDao;
import com.ztesoft.iom.manage.param.dao.impl.ParamMappingDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 参数映射业务类
 * @author: huang.jing
 * @Date: 2018/1/15 0015 - 11:53
 */
public class ParamMappingService {

    private static Logger log = LogManager.getLogger(ParamMappingService.class);

    private JSONObject paramMappingObject = new JSONObject();

    /**
     * 加载参数映射列表
     */
    public void loadParamMappingObject() {
        log.info("loadParamMappingObject入口……");
        try {
            ParamMappingDao ParamMappingDao = (ParamMappingDaoImpl) BeanFactory.getApplicationContext().getBean("paramMappingDaoImpl");
            Map qryMap = new HashMap();
            List paramMapList = ParamMappingDao.getParamMapList(qryMap);
            for (Iterator iterator = paramMapList.iterator(); iterator.hasNext();) {
                Map paramMap = (Map) iterator.next();
                JSONArray paramMappingValueList;
                String type = MapUtils.getString(paramMap, "TYPE");
                if(paramMappingObject.containsKey(type)) {
                    paramMappingValueList = paramMappingObject.getJSONArray(type);
                } else {
                    paramMappingValueList = new JSONArray();
                }
                paramMappingValueList.add(JSONObject.parseObject(JSON.toJSONString(paramMap)));
                paramMappingObject.put(type, paramMappingValueList);
            }
            log.info("加载参数映射列表成功，参数映射列表：" + paramMappingObject);
        } catch (Exception e) {
            log.error("加载参数映射列表异常", e);
        }

        log.info("loadParamMappingObject出口……");
    }

    /**
     * 根据参数值、类型查找映射值
     * 如果找不到映射值则返回原参数值
     * @param type
     * @param paramValue
     * @return
     */
    public String getMappingValue(String type, String paramValue) {
        String mappingValue = paramValue;
        try {
            if(paramMappingObject.containsKey(type)) {
                JSONArray paramMappingValueList = paramMappingObject.getJSONArray(type);
                for (Iterator iterator = paramMappingValueList.iterator(); iterator.hasNext();) {
                    JSONObject paramMappingValue = (JSONObject) iterator.next();
                    if(paramValue.equals(paramMappingValue.getString("PARAM_VALUE"))) {
                        mappingValue = paramMappingValue.getString("MAP_VALUE");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询映射值异常", e);
        }
        return mappingValue;
    }

    /**
     * 根据参数值、类型查找映射值
     * 如果找不到映射值则返回空
     * @param type
     * @param paramValue
     * @return
     */
    public String getMappingValueNoDefault(String type, String paramValue) {
        String mappingValue = "";
        try {
            if(paramMappingObject.containsKey(type)) {
                JSONArray paramMappingValueList = paramMappingObject.getJSONArray(type);
                for (Iterator iterator = paramMappingValueList.iterator(); iterator.hasNext();) {
                    JSONObject paramMappingValue = (JSONObject) iterator.next();
                    if(paramValue.equals(paramMappingValue.getString("PARAM_VALUE"))) {
                        mappingValue = paramMappingValue.getString("MAP_VALUE");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询映射值异常", e);
        }
        return mappingValue;
    }
}
