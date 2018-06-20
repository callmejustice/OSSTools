package com.ztesoft.iom.manage.rest.dao;

import java.util.List;
import java.util.Map;

public interface ConfigDAO {

    /**
     * 查询接口类型配置
     * @return
     */
    public List<Map<String, Object>> getInterfaceTypeConfig();

    /**
     * 查询接口名称配置
     * @return
     */
    public List<Map<String, Object>> getInterfaceNameConfig(Object code);
}
