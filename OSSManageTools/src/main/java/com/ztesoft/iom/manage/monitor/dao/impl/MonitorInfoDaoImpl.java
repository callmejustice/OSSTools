package com.ztesoft.iom.manage.monitor.dao.impl;

import com.ztesoft.iom.manage.monitor.dao.MonitorInfoDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 监控信息dao实现类
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 16:58
 */
public class MonitorInfoDaoImpl extends JdbcDaoSupport implements MonitorInfoDao {

    private static Logger log = LogManager.getLogger(MonitorInfoDaoImpl.class);

    /**
     * 获取采集信息列表
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getGatherInfo(Map qryMap) throws Exception {
        List orderList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT ogi.id, ogi.gather_type, ogi.gather_machine, ogi.gather_machine_desc, ogi.gather_name, ogi.gather_value, to_char(ogi.gather_time, 'yyyy/mm/dd hh24:mi')||':00' gather_time ");
        sql.append("FROM ot_gather_info ogi ");
        sql.append("WHERE ogi.gather_type = ? ");
        paramList.add(MapUtils.getString(qryMap, "gatherType", ""));

        if (!"".equals(MapUtils.getString(qryMap, "gatherName", ""))) {
            sql.append("AND ogi.gather_name = ? ");
            paramList.add(MapUtils.getString(qryMap, "gatherName", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "gatherMachineDesc", ""))) {
            sql.append("AND ogi.gather_machine_desc = ? ");
            paramList.add(MapUtils.getString(qryMap, "gatherMachineDesc", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "startTime", ""))) {
            sql.append("AND ogi.gather_time >= to_date(?, 'yyyy/mm/dd hh24:mi:ss') ");
            paramList.add(MapUtils.getString(qryMap, "startTime", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "endTime", ""))) {
            sql.append("AND ogi.gather_time >= to_date(?, 'yyyy/mm/dd hh24:mi:ss') ");
            paramList.add(MapUtils.getString(qryMap, "endTime", ""));
        }
        sql.append("ORDER BY ogi.gather_machine, ogi.gather_time ");

        log.debug("获取采集信息列表脚本：" + sql.toString());

        try {
            log.debug("获取采集信息列表参数：" + paramList);
            orderList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取采集信息列表异常", e);
        }
        return orderList;
    }
}
