package com.ztesoft.iom.manage.rest.dao;

import java.util.Map;

public interface UserDAO {

    /**
     * 查询登录用户
     * @param username
     * @return
     */
    public Map<String, Object> queryUserByUsername(Object username);
}
