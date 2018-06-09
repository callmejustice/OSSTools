package com.ztesoft.iom.manage.param.dao.impl;

import com.ztesoft.iom.manage.param.dao.ParamMappingDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 参数映射信息数据库dao实现类
 * @author: huang.jing
 * @Date: 2018/1/15 0015 - 12:32
 */
public class ParamMappingDaoImpl extends JdbcDaoSupport implements ParamMappingDao {

    private static Logger log = LogManager.getLogger(ParamMappingDaoImpl.class);

    /**
     * 获取参数映射列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getParamMapList(Map qryMap) throws Exception {
        List paramMapList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT otm.id, otm.type, otm.param_value, otm.map_value ");
        sql.append("FROM ot_param_map otm ");
        sql.append("ORDER BY otm.type ");
        log.debug("获取参数映射列表脚本：" + sql.toString());

        try {
            log.debug("获取参数映射列表参数：" + paramList);
            paramMapList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取参数映射列表异常", e);
        }
        return paramMapList;
    }
}
