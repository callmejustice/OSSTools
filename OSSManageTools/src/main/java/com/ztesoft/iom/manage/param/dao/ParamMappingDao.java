package com.ztesoft.iom.manage.param.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 参数映射信息数据库dao接口
 * @author: huang.jing
 * @Date: 2018/1/15 0015 - 12:28
 */
public interface ParamMappingDao {

    /**
     * 获取参数映射列表
     *
     * @param qryMap
     * @return
     */
    public List getParamMapList(Map qryMap) throws Exception;

}
