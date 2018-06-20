package com.ztesoft.iom.manage.rest.dao.impl;

import com.ztesoft.iom.manage.rest.dao.UserDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.Map;

public class UserDAOImpl extends JdbcDaoSupport implements UserDAO {

    public Map<String, Object> queryUserByUsername(Object username) {
        String sql = "SELECT * FROM OT_STAFF WHERE USERNAME = ?";
        return this.getJdbcTemplate().queryForMap(sql, username);
    }
}
