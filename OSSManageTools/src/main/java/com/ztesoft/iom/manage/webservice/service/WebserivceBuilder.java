package com.ztesoft.iom.manage.webservice.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description: webservice构造接口
 * @author: huang.jing
 * @Date: 2018/2/9 0009 - 11:06
 */
public interface WebserivceBuilder {

    /**
     * 构造请求参数
     * @param paramJson
     * @return
     */
    public JSONObject buildWebserivceParam(JSONObject paramJson) throws Exception;

}
