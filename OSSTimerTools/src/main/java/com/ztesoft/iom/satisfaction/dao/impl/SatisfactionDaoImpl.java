package com.ztesoft.iom.satisfaction.dao.impl;

import com.ztesoft.iom.satisfaction.dao.SatisfactionDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 满意度问卷dao实现类
 * @author: huang.jing
 * @Date: 2018/4/2 0002 - 15:51
 */
public class SatisfactionDaoImpl extends JdbcDaoSupport implements SatisfactionDao {

    private static Logger log = LogManager.getLogger(SatisfactionDaoImpl.class);

    /**
     * 获取满意度序列
     *
     * @param qryMap
     * @return
     */
    @Override
    public String getSatisfactionSeq(Map qryMap) throws Exception {
        String seq = "";
        StringBuffer sql = new StringBuffer();

        try {
            // 直接获取下一个序列
            sql.append("SELECT " + MapUtils.getString(qryMap, "seqName") + ".nextval seq FROM dual ");
            log.debug("获取满意度序列脚本：" + sql.toString());
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ", "");
            }
        } catch (Exception e) {
            throw new Exception("获取满意度序列异常", e);
        }
        return seq;
    }

    /**
     * 插入满意度信息
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertSatisfactionInfo(final Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO om_kf_satisfaction(id, accnbr, summary_date, questionnaire_name, staff_id, org, fill_date, is_connnected, is_new_and_timeout, is_paied, oper_date, oper_id, main_question, main_answer, questionnaire_content, file_name, is_satisfied, rela_order_id) ");
        sql.append("VALUES(?, ?, to_date(?, 'yyyymmdd'), ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?) ");

        log.debug("插入满意度信息脚本：" + sql.toString());
        try {
            LobHandler lobHandler = new DefaultLobHandler();
            insertCount = this.getJdbcTemplate().execute(sql.toString(), new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException {
                    ps.setString(1, MapUtils.getString(insertMap, "id"));
                    ps.setString(2, MapUtils.getString(insertMap, "accnbr"));
                    ps.setString(3, MapUtils.getString(insertMap, "summaryDate"));
                    ps.setString(4, MapUtils.getString(insertMap, "questionnaireName"));
                    ps.setString(5, MapUtils.getString(insertMap, "staffId"));
                    ps.setString(6, MapUtils.getString(insertMap, "org"));
                    ps.setString(7, MapUtils.getString(insertMap, "fillDate"));
                    ps.setString(8, MapUtils.getString(insertMap, "isConnnected"));
                    ps.setString(9, MapUtils.getString(insertMap, "isNewAndTimeout"));
                    ps.setString(10, MapUtils.getString(insertMap, "isPaied"));
                    ps.setString(11, MapUtils.getString(insertMap, "operDate"));
                    ps.setString(12, MapUtils.getString(insertMap, "operId"));
                    ps.setString(13, MapUtils.getString(insertMap, "mainQuestion"));
                    ps.setString(14, MapUtils.getString(insertMap, "mainAnswer"));
                    lobCreator.setBlobAsBytes(ps, 15, MapUtils.getString(insertMap, "questionnaireContent").getBytes());
                    ps.setString(16, MapUtils.getString(insertMap, "fileName"));
                    ps.setString(17, MapUtils.getString(insertMap, "isSatisfied"));
                    ps.setString(18, MapUtils.getString(insertMap, "relaOrderId"));
                }
            });
        } catch (Exception e) {
            log.error("插入满意度信息异常", e);
        }
        return insertCount;
    }

    /**
     * 删除满意度信息
     *
     * @param deleteMap
     * @return
     */
    @Override
    public int deleteSatisfactionInfo(Map deleteMap) throws Exception {
        int deleteCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM om_kf_satisfaction oks ");
        sql.append("WHERE oks.file_name = ? ");
        log.debug("删除满意度信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(deleteMap, "fileName"));
            log.debug("删除满意度信息参数：" + paramList);
            deleteCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("删除满意度信息异常", e);
        }
        return deleteCount;
    }

    /**
     * 查询满意度信息列表
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getSatisfactionInfo(Map qryMap) throws Exception {
        List satisfactionInfoList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT oks.id, oks.accnbr, oks.summary_date, oks.questionnaire_name, oks.staff_id, oks.org, to_char(oks.fill_date, 'yyyy/mm/dd hh24:mi:ss') fill_date, oks.is_connnected, oks.is_new_and_timeout, oks.is_paied, oks.oper_date, oks.oper_id, oks.main_question, oks.main_answer, oks.questionnaire_content, oks.file_name ");
        sql.append("FROM om_kf_satisfaction oks ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND oks.file_name = ? ");
        paramList.add(MapUtils.getString(qryMap, "fileName"));

        log.debug("查询满意度信息列表脚本：" + sql.toString());
        try {
            log.debug("查询满意度信息列表参数：" + paramList);
            satisfactionInfoList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("查询满意度信息列表异常", e);
        }
        return satisfactionInfoList;
    }

    /**
     * 查询满意度信息总数
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public Long getSatisfactionInfoCount(Map qryMap) throws Exception {
        Long satisfactionInfoCount = 0l;
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) satisfaction_count  ");
        sql.append("FROM om_kf_satisfaction oks ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND oks.file_name = ? ");
        paramList.add(MapUtils.getString(qryMap, "fileName"));

        log.debug("查询满意度信息总数脚本：" + sql.toString());
        try {
            log.debug("查询满意度信息总数参数：" + paramList);
            List satisfactionInfoList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            for (int i = 0; i < satisfactionInfoList.size(); i++) {
                Map satisfactionInfoMap = (Map) satisfactionInfoList.get(i);
                satisfactionInfoCount = MapUtils.getLongValue(satisfactionInfoMap, "SATISFACTION_COUNT", 0l);
            }
        } catch (Exception e) {
            log.error("查询满意度信息总数异常", e);
        }
        return satisfactionInfoCount;
    }

    /**
     * 查询满意度信息入库记录
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getSatisfactionLog(Map qryMap) throws Exception {
        List satisfactionLogList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT oksl.file_name, oksl.avl_file_length, to_char(oksl.create_date, 'yyyy/mm/dd hh24:mi:ss') create_date, oksl.insert_file_length, to_char(oksl.finish_date, 'yyyy/mm/dd hh24:mi:ss') finish_date, oksl.result_code, oksl.error_info, oksl.deal_count ");
        sql.append("FROM om_kf_satisfaction_log oksl ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND oksl.file_name = ? ");
        paramList.add(MapUtils.getString(qryMap, "fileName"));

        log.debug("查询满意度信息入库记录脚本：" + sql.toString());
        try {
            log.debug("查询满意度信息入库记录参数：" + paramList);
            satisfactionLogList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("查询满意度信息入库记录异常", e);
        }
        return satisfactionLogList;
    }

    /**
     * 更新满意度信息入库记录
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateSatisfactionLog(Map updateMap) throws Exception {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE om_kf_satisfaction_log oksl ");
        sql.append("SET oksl.insert_file_length = ?, oksl.finish_date = sysdate, oksl.result_code = ?, oksl.error_info = ?, oksl.deal_count = oksl.deal_count + ? ");
        sql.append("WHERE oksl.file_name = ? ");
        log.debug("更新满意度信息入库记录脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(updateMap, "insertFileLength"));
            paramList.add(MapUtils.getString(updateMap, "resultCode"));
            paramList.add(MapUtils.getString(updateMap, "errorInfo"));
            paramList.add(MapUtils.getIntValue(updateMap, "dealCount", 1));
            paramList.add(MapUtils.getString(updateMap, "fileName"));
            log.debug("更新满意度信息入库记录参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新满意度信息入库记录异常", e);
        }
        return updateCount;
    }

    /**
     * 插入满意度信息入库记录
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    @Override
    public int insertSatisfactionLog(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO om_kf_satisfaction_log(file_name, avl_file_length, create_date, deal_count) ");
        sql.append("VALUES(?, ?, sysdate, 1) ");

        log.debug("插入满意度信息入库记录脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "fileName"));
            paramList.add(MapUtils.getString(insertMap, "avlFileLength"));
            log.debug("插入满意度信息入库记录参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入满意度信息入库记录异常", e);
        }
        return insertCount;
    }

    /**
     * 查询满意度答案
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getSatisfactionAnswer(Map qryMap) throws Exception {
        List satisfactionAnswerList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT oksa.answer, oksa.is_satisfied ");
        sql.append("FROM om_kf_satisfaction_answer oksa ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "state", ""))) {
            sql.append("AND oksa.state = ? ");
            paramList.add(MapUtils.getString(qryMap, "state", ""));
        }

        log.debug("查询满意度答案脚本：" + sql.toString());
        try {
            log.debug("查询满意度答案参数：" + paramList);
            satisfactionAnswerList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("查询满意度答案异常", e);
        }
        return satisfactionAnswerList;
    }

    /**
     * 查询定单信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getOrderInfo(Map qryMap) throws Exception {
        List orderInfoList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (SELECT oo.id, oo.order_code ");
        sql.append("FROM om_order oo, om_service_order oso ");
        sql.append("WHERE oo.id = oso.id ");
        sql.append("AND oso.object_type NOT IN ('003', '004') ");
        sql.append("AND oo.order_state = ? ");
        sql.append("AND oso.acc_nbr = ? ");
        sql.append("AND oo.finish_date >= to_date(?,'yyyy-mm-dd hh24:mi:ss') - ? ");
        sql.append("ORDER BY oo.finish_date DESC) ");
        sql.append("WHERE ROWNUM <= 1 ");

        paramList.add(MapUtils.getString(qryMap, "orderState", ""));
        paramList.add(MapUtils.getString(qryMap, "accnbr", ""));
        paramList.add(MapUtils.getString(qryMap, "fillDate", ""));
        paramList.add(MapUtils.getString(qryMap, "limitDay", ""));
        log.debug("查询定单信息脚本：" + sql.toString());
        try {
            log.debug("查询定单信息参数：" + paramList);
            orderInfoList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("查询定单信息异常", e);
        }
        return orderInfoList;
    }

    /**
     * 更新订单满意度信息
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateOrderSatisfactionInfo(Map updateMap) throws Exception {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE om_so_order_rela osor ");
        sql.append("SET osor.satisfaction_id = ? ");
        sql.append("WHERE osor.service_order_id = ? ");
        log.debug("更新订单满意度信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(updateMap, "satisfactionId"));
            paramList.add(MapUtils.getString(updateMap, "orderId"));
            log.debug("更新订单满意度信息参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新订单满意度信息异常", e);
        }
        return updateCount;
    }

    /**
     * 按网格维度获取客服问卷满意度报表信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getOrderSatisfactionReportBaseInfo(Map qryMap) throws Exception {
        List orderSatisfactionReportBaseInfo = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT oks.id, to_char(oks.summary_date, 'yyyy/mm/dd') summary_date, oks.is_satisfied, oofw.service_lv, oofw.user_area_name, nvl(oofw.county, '其它') county, nvl(oofw.exch_name, '其它') exch_name, nvl(oofw.gird, '其它') grid ");
        sql.append("FROM om_kf_satisfaction oks, om_order_finish_wid oofw ");
        sql.append("WHERE oks.rela_order_id = oofw.order_id ");
        sql.append("AND oks.file_name = ? ");

        paramList.add(MapUtils.getString(qryMap, "fileName", ""));
        log.debug("按网格维度获取客服问卷满意度报表信息脚本：" + sql.toString());
        try {
            log.debug("按网格维度获取客服问卷满意度报表信息参数：" + paramList);
            orderSatisfactionReportBaseInfo = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("按网格维度获取客服问卷满意度报表信息异常", e);
        }
        return orderSatisfactionReportBaseInfo;
    }

    /**
     * 获取客服问卷满意度报表信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getOrderSatisfactionReportInfo(Map qryMap) throws Exception {
        List orderSatisfactionReportBaseInfo = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT rks.md5, to_char(rks.summary_date, 'yyyy/mm/dd') summary_date, rks.area_name, rks.county, rks.exch_name, rks.grid_name, ");
        sql.append("rks.high_level_count, rks.high_level_satisfied_count, rks.normal_level_count, rks.normal_level_satisfied_count ");
        sql.append("FROM report_kf_satisfaction rks ");
        sql.append("WHERE rks.md5 = ? ");

        paramList.add(MapUtils.getString(qryMap, "md5", ""));
        log.debug("获取客服问卷满意度报表信息脚本：" + sql.toString());
        try {
            log.debug("获取客服问卷满意度报表信息参数：" + paramList);
            orderSatisfactionReportBaseInfo = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取客服问卷满意度报表信息异常", e);
        }
        return orderSatisfactionReportBaseInfo;
    }

    /**
     * 插入客服问卷满意度报表信息
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    @Override
    public int insertSatisfactionReportInfo(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO report_kf_satisfaction(md5, summary_date, area_name, county, exch_name, grid_name, high_level_count, high_level_answer_count, high_level_satisfied_count, normal_level_count, normal_level_answer_count, normal_level_satisfied_count, create_date, update_date) ");
        sql.append("VALUES(?, to_date(?, 'yyyy/mm/dd'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, sysdate) ");

        log.debug("插入客服问卷满意度报表信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "md5"));
            paramList.add(MapUtils.getString(insertMap, "summaryDate"));
            paramList.add(MapUtils.getString(insertMap, "areaName"));
            paramList.add(MapUtils.getString(insertMap, "county"));
            paramList.add(MapUtils.getString(insertMap, "exchName"));
            paramList.add(MapUtils.getString(insertMap, "gridName"));
            paramList.add(MapUtils.getString(insertMap, "highLevelCount"));
            paramList.add(MapUtils.getString(insertMap, "highLevelAnswerCount"));
            paramList.add(MapUtils.getString(insertMap, "highLevelSatisfiedCount"));
            paramList.add(MapUtils.getString(insertMap, "normalLevelCount"));
            paramList.add(MapUtils.getString(insertMap, "normalLevelAnswerCount"));
            paramList.add(MapUtils.getString(insertMap, "normalLevelSatisfiedCount"));
            log.debug("插入客服问卷满意度报表信息参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入客服问卷满意度报表信息异常", e);
        }
        return insertCount;
    }

    /**
     * 更新客服问卷满意度报表信息
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateSatisfactionReportInfo(Map updateMap) throws Exception {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE report_kf_satisfaction rks ");
        sql.append("SET rks.high_level_count = rks.high_level_count + ?, rks.high_level_answer_count = high_level_answer_count + ?, rks.high_level_satisfied_count = rks.high_level_satisfied_count + ?, ");
        sql.append("rks.normal_level_count = rks.normal_level_count + ?, rks.normal_level_answer_count = normal_level_answer_count + ?, rks.normal_level_satisfied_count = rks.normal_level_satisfied_count + ?, ");
        sql.append("rks.update_date = sysdate ");
        sql.append("WHERE rks.md5 = ? ");
        log.debug("更新客服问卷满意度报表信息脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(updateMap, "highLevelCount"));
            paramList.add(MapUtils.getString(updateMap, "highLevelAnswerCount"));
            paramList.add(MapUtils.getString(updateMap, "highLevelSatisfiedCount"));
            paramList.add(MapUtils.getString(updateMap, "normalLevelCount"));
            paramList.add(MapUtils.getString(updateMap, "normalLevelAnswerCount"));
            paramList.add(MapUtils.getString(updateMap, "normalLevelSatisfiedCount"));
            paramList.add(MapUtils.getString(updateMap, "md5"));
            log.debug("更新客服问卷满意度报表信息参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新客服问卷满意度报表信息异常", e);
        }
        return updateCount;
    }

    /**
     * 根据id获取需要发送的短信人列表
     *
     * @param qryMap
     * @return
     */
    public List getWarnListById(Map qryMap) {
        List warnList = new ArrayList();
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT uwl.recv_num ");
        sql.append("FROM uos_warn_list uwl ");
        sql.append("WHERE uwl.warn_id = ?");
        try {
            log.debug("根据id获取需要发送的短信人列表脚本：" + sql.toString());
            paramList.add(MapUtils.getString(qryMap, "warnId"));
            log.debug("根据id获取需要发送的短信人列表参数：" + paramList);
            warnList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());

        } catch (Exception e) {
            log.error("根据id获取需要发送的短信人列表异常", e);
        }
        return warnList;
    }

    /**
     * 插入发送短信表
     *
     * @param insertMap
     * @return
     */
    public int insertSmsLog(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO tm_note_inst(id, temp_id, content, note_type, create_date, state, disp_times, note_no, send_type, area_id) ");
        sql.append("VALUES(tm_note_inst_seq.nextval, 999, ?, 'scx', sysdate, '10I', '0', ?, '1', ?) ");

        try {
            log.debug("插入发送短信表脚本：" + sql.toString());
            paramList.add(MapUtils.getString(insertMap, "sendContent"));
            paramList.add(MapUtils.getString(insertMap, "recvNum"));
            paramList.add(MapUtils.getString(insertMap, "areaCode"));
            log.debug("插入发送短信表参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入发送短信表异常", e);
        }
        return insertCount;
    }
}
