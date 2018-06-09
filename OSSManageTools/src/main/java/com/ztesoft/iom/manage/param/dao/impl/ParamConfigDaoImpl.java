package com.ztesoft.iom.manage.param.dao.impl;

import com.ztesoft.iom.manage.param.dao.ParamConfigDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/2/11 0011 - 10:10
 */
public class ParamConfigDaoImpl extends JdbcDaoSupport implements ParamConfigDao {

    private static Logger log = LogManager.getLogger(ParamConfigDaoImpl.class);

    /**
     * 获取动态参数值
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public String getParamConfigValue(Map qryMap) throws Exception {
        String paramConfigValue = "";
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT otc.param_value ");
        sql.append("FROM ot_param_config otc ");
        sql.append("WHERE otc.param_name = ? ");
        log.debug("获取动态参数值：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "paramConfigName"));
            log.debug("获取动态参数值参数：" + paramList);
            List paramConfigList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            for (int i = 0; i < paramConfigList.size(); i++) {
                Map paramConfigMap = (Map) paramConfigList.get(i);
                paramConfigValue = MapUtils.getString(paramConfigMap, "PARAM_VALUE", "");
            }
        } catch (Exception e) {
            throw new Exception("获取动态参数值异常", e);
        }
        return paramConfigValue;
    }
}
