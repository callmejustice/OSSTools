package com.ztesoft.iom.pretreatment.dao.impl;

import com.ztesoft.iom.pretreatment.dao.AvoidanceGroupDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 预处理群障数据库操作实现类
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 17:09
 */
public class AvoidanceGroupDaoImpl extends JdbcDaoSupport implements AvoidanceGroupDao {

    private static Logger log = LogManager.getLogger(AvoidanceGroupDaoImpl.class);

    /**
     * 获取待汇总的群障记录
     *
     * @param param
     * @return
     */
    public List getAvoidanceGroupNotSumList(Map param) {
        List avoidanceGroupNotSumList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (SELECT ld.lgf_name, ld.lgf_type, ld.lgf_code, ld.batch_id, lan.acc_nbr, zaaa.addr_fisrt, zaaa.addr_second, zaaa.addr_third, zaaa.addr_thourth, zaaa.addr_fifth, zaaa.addr_sixth, zaaa.addr_seventh, zaaa.addr_eighth, zaaa.addr_ninth ");
        sql.append("FROM lgf_detail ld, lgf_acc_nbr lan, zy_acct_and_addr zaaa ");
        sql.append("WHERE ld.batch_id = lan.batch_id ");
        sql.append("AND ld.state = ? ");
        sql.append("AND ld.valid_date <= sysdate ");
        sql.append("AND ld.invalid_date >= sysdate ");
        sql.append("AND lan.acc_nbr = zaaa.account ");
        sql.append("AND NOT EXISTS (SELECT 1 FROM lgf_avoidance_group_log lagl WHERE ld.batch_id = lagl.batch_id AND lan.acc_nbr = lagl.acc_nbr) ");
        sql.append("ORDER BY ld.batch_id) ");
        sql.append("WHERE ROWNUM <= 3000 ");
        paramList.add("1");

        log.debug("获取待汇总的群障记录查询脚本：" + sql.toString());
        try {
            avoidanceGroupNotSumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取待汇总的群障列表异常", e);
        }
        return avoidanceGroupNotSumList;
    }

    /**
     * 获取预处理群障树序列
     *
     * @return
     */
    public String getAvoidanceGroupLogSeq() {
        String seq = "";
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(lgf_avoidance_group_sum_seq.nextval, '000000')) seq FROM dual ");
        try {
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ");
            }
        } catch (Exception e) {
            log.error("获取预处理群障树序列异常", e);
        }
        return seq;
    }

    /**
     * 记录预处理群障树日志
     *
     * @param paramList
     * @return
     */
    public int insertAvoidanceGroupLogBatch(List paramList) {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO lgf_avoidance_group_log(batch_id, acc_nbr, state, create_date) VALUES(?, ?, ?, sysdate) ");

        log.debug("记录预处理群障树日志脚本：" + sql.toString());
        try {
            int[] insertCountTmp = this.getJdbcTemplate().batchUpdate(sql.toString(), paramList);
            for (int i = 0; i < insertCountTmp.length; i++) {
                if (insertCountTmp[i] > 0) {
                    insertCount += insertCountTmp[i];
                } else if (insertCountTmp[i] == -2) {
                    insertCount++;
                }

            }
        } catch (Exception e) {
            log.error("记录预处理群障树日志异常", e);
        }
        return insertCount;
    }

    /**
     * 获取预处理群障树信息
     *
     * @param param
     * @return
     */
    public List getAvoidanceGroupSumList(Map param) {
        List avoidanceGroupSumList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id, batch_id, parent_id, node_level, node_name, md5, complain_count, summary_count ");
        sql.append("FROM lgf_avoidance_group_sum lags ");
        sql.append("WHERE lags.batch_id = ? ");
        sql.append("AND lags.md5 = ? ");
        paramList.add(MapUtils.getString(param, "batchId"));
        paramList.add(MapUtils.getString(param, "md5"));

        log.debug("获取预处理群障树信息脚本：" + sql.toString());
        try {
            avoidanceGroupSumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取预处理群障树信息异常", e);
        }
        return avoidanceGroupSumList;
    }

    /**
     * 记录预处理群障树信息
     *
     * @param param
     * @return
     */
    public int insertAvoidanceGroupSum(Map param) {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO lgf_avoidance_group_sum(id, batch_id, parent_id, node_level, node_name, md5, complain_count, summary_count, number_count, create_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate) ");

        log.debug("记录预处理群障树信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "id"));
            paramList.add(MapUtils.getString(param, "batchId"));
            paramList.add(MapUtils.getString(param, "parentId"));
            paramList.add(MapUtils.getString(param, "nodeLevel"));
            paramList.add(MapUtils.getString(param, "nodeName"));
            paramList.add(MapUtils.getString(param, "md5"));
            paramList.add(MapUtils.getString(param, "complainCount"));
            paramList.add(MapUtils.getString(param, "summaryCount"));
            paramList.add(MapUtils.getString(param, "numberCount"));
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("记录预处理群障树信息异常", e);
        }
        return insertCount;
    }

    /**
     * 更新预处理群障树信息
     *
     * @param param
     * @return
     */
    public int updateAvoidanceGroupSum(Map param) {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE lgf_avoidance_group_sum lags ");
        if(MapUtils.getString(param, "updateType") != null && "1".equals(MapUtils.getString(param, "updateType"))){
            sql.append("SET lags.number_count = lags.number_count + ?, lags.update_date = sysdate ");
            paramList.add(MapUtils.getLong(param, "numberCount"));
        } else {
            sql.append("SET lags.complain_count = lags.complain_count + ?, lags.summary_count = lags.summary_count + ?, lags.update_date = sysdate ");
            paramList.add(MapUtils.getLong(param, "complainCount"));
            paramList.add(MapUtils.getLong(param, "summaryCount"));
        }

        sql.append("WHERE lags.batch_id = ? ");
        sql.append("AND lags.md5 = ? ");

        log.debug("更新预处理群障树信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "batchId"));
            paramList.add(MapUtils.getString(param, "md5"));
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新预处理群障树信息异常", e);
        }
        return updateCount;
    }

    /**
     * 获取待关联为投诉单或者抱怨单的列表
     *
     * @param param
     * @return
     */
    public List getAvoidanceGroupNotRelaList(Map param) {
        List avoidanceGroupNotRelaList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (");
        sql.append("SELECT lagl.batch_id, lagl.acc_nbr, to_char(lagl.create_date, 'yyyy-mm-dd hh24:mi:ss') create_date, to_char(lqan.qry_date, 'yyyy-mm-dd hh24:mi:ss') qry_date, to_char(ies.create_date, 'yyyy-mm-dd hh24:mi:ss') eoms_create_date ");
        sql.append("FROM lgf_avoidance_group_log lagl ");
        sql.append("LEFT JOIN lgf_qry_acc_nbr lqan ON lagl.batch_id = lqan.batch_id AND lagl.acc_nbr = lqan.acc_nbr ");
        sql.append("LEFT JOIN inf_eoms_sheet ies ON lagl.acc_nbr = ies.acc_nbr AND ROWNUM <= 1 ");
        sql.append("WHERE lagl.state = '0' ");
        sql.append("AND EXISTS(SELECT 1 FROM lgf_detail ld WHERE lagl.batch_id = ld.batch_id AND ld.valid_date <= sysdate AND ld.invalid_date >= sysdate AND ld.state = '1') ");
        sql.append("ORDER BY lagl.update_date DESC) ");
        sql.append("WHERE ROWNUM <= 2000 ");

        log.debug("获取群障待关联为投诉单或者抱怨单的列表脚本：" + sql.toString());
        try {
            avoidanceGroupNotRelaList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取群障待关联为投诉单或者抱怨单的列表异常", e);
        }
        return avoidanceGroupNotRelaList;
    }

    /**
     * 获取预处理群障号码信息
     *
     * @param param
     * @return
     */
    public Map getAvoidanceGroupNumInfo(Map param) {
        Map AvoidanceGroupNumInfoMap = new HashMap();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT zaaa.account, zaaa.addr_fisrt, zaaa.addr_second, zaaa.addr_third, zaaa.addr_thourth, zaaa.addr_fifth, zaaa.addr_sixth, zaaa.addr_seventh, zaaa.addr_eighth, zaaa.addr_ninth ");
        sql.append("FROM zy_acct_and_addr zaaa ");
        sql.append("WHERE zaaa.account = ? ");
        paramList.add(MapUtils.getString(param, "accnbr"));
        log.debug("获取预处理群障号码信息脚本：" + sql.toString());
        try {
            List avoidanceGroupNumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (avoidanceGroupNumList != null && avoidanceGroupNumList.size() > 0) {
                AvoidanceGroupNumInfoMap = (Map) avoidanceGroupNumList.get(0);
            }
        } catch (Exception e) {
            log.error("获取预处理群障号码信息异常", e);
        }
        return AvoidanceGroupNumInfoMap;
    }

    /**
     * 更新预处理群障树日志
     *
     * @param param
     * @return
     */
    public int updateAvoidanceGroupLog(Map param) {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE lgf_avoidance_group_log lagl ");
        sql.append("SET lagl.state = ?, update_date = sysdate ");
        sql.append("WHERE lagl.batch_id = ? ");
        sql.append("AND lagl.acc_nbr = ? ");

        log.debug("更新预处理群障树日志脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "state"));
            paramList.add(MapUtils.getString(param, "batchId"));
            paramList.add(MapUtils.getString(param, "accnbr"));
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新预处理群障树日志信息异常", e);
        }
        return updateCount;
    }

}
