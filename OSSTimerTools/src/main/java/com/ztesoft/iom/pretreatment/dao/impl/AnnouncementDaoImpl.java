package com.ztesoft.iom.pretreatment.dao.impl;

import com.ztesoft.iom.pretreatment.dao.AnnouncementDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 预处理公告数据库操作实现类
 * @author: huang.jing
 * @Date: 2018/1/3 0003 - 16:35
 */
public class AnnouncementDaoImpl extends JdbcDaoSupport implements AnnouncementDao {

    private static Logger log = LogManager.getLogger(AnnouncementDaoImpl.class);

    /**
     * 获取待汇总的公告记录
     *
     * @param param
     * @return
     */
    public List getAnnouncementNotSumList(Map param) {
        List announcementNotSumList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (SELECT aia.batch_id, zaaa.account acc_nbr, zaaa.addr_fisrt, zaaa.addr_second, zaaa.addr_third, zaaa.addr_thourth, zaaa.addr_fifth, zaaa.addr_sixth, zaaa.addr_seventh, zaaa.addr_eighth, zaaa.addr_ninth ");
        sql.append("FROM ann_detail ad, ann_inst_address aia, zy_acct_and_addr zaaa ");
        sql.append("WHERE ad.address_id = aia.batch_id ");
        sql.append("AND ad.state_text = ? ");
        sql.append("AND aia.addr_second = zaaa.addr_second ");
        sql.append("AND aia.addr_third = zaaa.addr_third ");
        sql.append("AND (aia.addr_thourth = zaaa.addr_thourth OR zaaa.addr_thourth IS NULL) ");
        sql.append("AND (aia.addr_fifth = zaaa.addr_fifth OR zaaa.addr_fifth IS NULL) ");
        sql.append("AND (aia.addr_sixth = zaaa.addr_sixth OR zaaa.addr_thourth IS NULL) ");
        sql.append("AND (aia.addr_seventh = zaaa.addr_seventh OR zaaa.addr_seventh IS NULL) ");
        sql.append("AND aia.addr_eighth = zaaa.addr_eighth ");
        sql.append("AND (aia.addr_ninth = zaaa.addr_ninth OR zaaa.addr_ninth IS NULL) ");
        sql.append("AND NOT EXISTS (SELECT 1 FROM lgf_announcement_log lagl WHERE aia.batch_id = lagl.batch_id AND zaaa.account = lagl.acc_nbr) ");
        sql.append("ORDER BY aia.batch_id) ");
        sql.append("WHERE ROWNUM <= 3000 ");
        paramList.add("有效");

        log.debug("获取待汇总的公告记录脚本：" + sql.toString());
        try {
            announcementNotSumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取待汇总的公告记录异常", e);
        }
        return announcementNotSumList;
    }

    /**
     * 获取预处理公告树序列
     *
     * @return
     */
    public String getAnnouncementLogSeq() {
        String seq = "";
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(lgf_announcement_sum_seq.nextval, '000000')) seq FROM dual ");
        try {
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ");
            }
        } catch (Exception e) {
            log.error("获取预处理公告树序列异常", e);
        }
        return seq;
    }

    /**
     * 记录预处理公告树日志
     *
     * @param paramList
     * @return
     */
    public int insertAnnouncementLogBatch(List paramList) {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO lgf_announcement_log(batch_id, acc_nbr, state, create_date) VALUES(?, ?, ?, sysdate) ");

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
            log.error("记录预处理公告树日志异常", e);
        }
        return insertCount;
    }

    /**
     * 获取预处理公告树信息
     *
     * @param param
     * @return
     */
    public List getAnnouncementSumList(Map param) {
        List announcementSumList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id, batch_id, parent_id, node_level, node_name, md5, complain_count, summary_count ");
        sql.append("FROM lgf_announcement_sum lags ");
        sql.append("WHERE lags.batch_id = ? ");
        sql.append("AND lags.md5 = ? ");
        paramList.add(MapUtils.getString(param, "batchId"));
        paramList.add(MapUtils.getString(param, "md5"));

        log.debug("获取预处理公告树信息脚本：" + sql.toString());
        try {
            announcementSumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取预处理公告树信息异常", e);
        }
        return announcementSumList;
    }

    /**
     * 记录预处理公告树信息
     *
     * @param param
     * @return
     */
    public int insertAnnouncementSum(Map param) {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO lgf_announcement_sum(id, batch_id, parent_id, node_level, node_name, md5, complain_count, summary_count, number_count, create_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate) ");

        log.debug("记录预处理公告树信息脚本：" + sql.toString());
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
            log.error("记录预处理公告树信息异常", e);
        }
        return insertCount;
    }

    /**
     * 更新预处理公告树信息
     *
     * @param param
     * @return
     */
    public int updateAnnouncementSum(Map param) {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE lgf_announcement_sum lags ");
        if(MapUtils.getString(param, "updateType") != null && "1".equals(MapUtils.getString(param, "updateType"))) {
            sql.append("SET lags.number_count = lags.number_count + ?, lags.update_date = sysdate ");
            paramList.add(MapUtils.getLong(param, "numberCount"));
        } else {
            sql.append("SET lags.complain_count = lags.complain_count + ?, lags.summary_count = lags.summary_count + ?, lags.update_date = sysdate ");
            paramList.add(MapUtils.getLong(param, "complainCount"));
            paramList.add(MapUtils.getLong(param, "summaryCount"));
        }
        sql.append("WHERE lags.batch_id = ? ");
        sql.append("AND lags.md5 = ? ");

        log.debug("更新预处理公告树信息脚本：" + sql.toString());
        try {

            paramList.add(MapUtils.getString(param, "batchId"));
            paramList.add(MapUtils.getString(param, "md5"));
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新预处理公告树信息异常", e);
        }
        return updateCount;
    }

    /**
     * 获取待关联为投诉单或者抱怨单的列表
     *
     * @param param
     * @return
     */
    public List getAnnouncementNotRelaList(Map param) {
        List announcementNotRelaList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (");
        sql.append("SELECT lal.batch_id, lal.acc_nbr, to_char(lal.create_date, 'yyyy-mm-dd hh24:mi:ss') create_date, to_char(aqan.qry_date, 'yyyy-mm-dd hh24:mi:ss') qry_date, to_char(ies.create_date, 'yyyy-mm-dd hh24:mi:ss') eoms_create_date ");
        sql.append("FROM lgf_announcement_log lal ");
        sql.append("LEFT JOIN ann_qry_acc_nbr aqan ON lal.batch_id = aqan.batch_id AND lal.acc_nbr = aqan.acc_nbr ");
        sql.append("LEFT JOIN inf_eoms_sheet ies ON lal.acc_nbr = ies.acc_nbr AND ROWNUM <= 1 ");
        sql.append("WHERE lal.state = '0' ");
        sql.append("AND EXISTS(SELECT 1 FROM ann_detail ad WHERE lal.batch_id = ad.address_id AND ad.valid_date <= sysdate AND ad.invalid_date >= sysdate AND ad.state_text = '有效') ");
        sql.append("ORDER BY lal.update_date DESC) ");
        sql.append("WHERE ROWNUM <= 2000 ");

        log.debug("获取公告待关联为投诉单或者抱怨单的列表脚本：" + sql.toString());
        try {
            announcementNotRelaList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取公告待关联为投诉单或者抱怨单的列表异常", e);
        }
        return announcementNotRelaList;
    }

    /**
     * 获取预处理公告号码信息
     *
     * @param param
     * @return
     */
    public Map getAnnouncementNumInfo(Map param) {
        Map AnnouncementNumInfoMap = new HashMap();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT zaaa.account, zaaa.addr_fisrt, zaaa.addr_second, zaaa.addr_third, zaaa.addr_thourth, zaaa.addr_fifth, zaaa.addr_sixth, zaaa.addr_seventh, zaaa.addr_eighth, zaaa.addr_ninth ");
        sql.append("FROM zy_acct_and_addr zaaa ");
        sql.append("WHERE zaaa.account = ? ");
        paramList.add(MapUtils.getString(param, "accnbr"));
        log.debug("获取预处理公告号码信息脚本：" + sql.toString());
        try {
            List announcementNumList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (announcementNumList != null && announcementNumList.size() > 0) {
                AnnouncementNumInfoMap = (Map) announcementNumList.get(0);
            }
        } catch (Exception e) {
            log.error("获取预处理公告号码信息异常", e);
        }
        return AnnouncementNumInfoMap;
    }

    /**
     * 更新预处理公告树日志
     *
     * @param param
     * @return
     */
    public int updateAnnouncementLog(Map param) {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE lgf_announcement_log lal ");
        sql.append("SET lal.state = ?, update_date = sysdate ");
        sql.append("WHERE lal.batch_id = ? ");
        sql.append("AND lal.acc_nbr = ? ");

        log.debug("更新预处理公告树日志脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "state"));
            paramList.add(MapUtils.getString(param, "batchId"));
            paramList.add(MapUtils.getString(param, "accnbr"));
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新预处理公告树日志异常", e);
        }
        return updateCount;
    }
}
