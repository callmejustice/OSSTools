package com.ztesoft.iom.manage.rest.dao.impl;

import com.ztesoft.iom.manage.rest.dao.ConfigDAO;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;
import java.util.Map;

public class ConfigDAOImpl extends JdbcDaoSupport implements ConfigDAO {

    public List<Map<String, Object>> getInterfaceTypeConfig() {
        String sql = "SELECT * FROM OT_INTERFACE_TYPE_CONFIG";
        return this.getJdbcTemplate().queryForList(sql);
    }

    public List<Map<String, Object>> getInterfaceNameConfig(Object code) {
        String sql = "SELECT * FROM OT_INTERFACE_NAME_CONFIG WHERE TYPE = ?";
        return this.getJdbcTemplate().queryForList(sql, code);
    }
}
