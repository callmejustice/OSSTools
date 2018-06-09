package com.ztesoft.iom.manage.param.dao;

import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/2/11 0011 - 10:10
 */
public interface ParamConfigDao {

    /**
     * 获取动态参数值
     * @param qryMap
     * @return
     * @throws Exception
     */
    public String getParamConfigValue(Map qryMap) throws Exception;

}
