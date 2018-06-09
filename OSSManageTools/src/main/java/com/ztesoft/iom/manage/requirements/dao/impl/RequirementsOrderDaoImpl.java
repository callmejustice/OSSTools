package com.ztesoft.iom.manage.requirements.dao.impl;

import com.ztesoft.iom.manage.requirements.dao.RequirementsOrderDao;
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
 * @Date: 2018/1/23 0023 - 15:56
 */
public class RequirementsOrderDaoImpl extends JdbcDaoSupport implements RequirementsOrderDao {

    private static Logger log = LogManager.getLogger(RequirementsOrderDaoImpl.class);

    /**
     * 获取需求模块相关序列
     *
     * @param qryMap
     * @return
     */
    @Override
    public String getRequirementsModuleSeq(Map qryMap) throws Exception {
        String seq = "";
        StringBuffer sql = new StringBuffer();

        try {
            // 在序列前加上yyyymmddhh24miss，每秒最多可以生成10000个不重复的序列，序列长度为固定18位
            sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(" + MapUtils.getString(qryMap, "seqName") + ".nextval, '0000')) seq FROM dual ");
            log.debug("获取需求模块相关序列脚本：" + sql.toString());
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ", "");
            }
        } catch (Exception e) {
            throw new Exception("获取需求模块相关序列异常", e);
        }
        return seq;
    }

    /**
     * 全字段新增需求EOMS单
     * @param paramList
     * @return
     * @throws Exception
     */
    @Override
    public int insertRequirementsEOMSOrderAllCol(List paramList) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ot_requirements_eoms_order(requirements_code, requirements_title, requirements_state, process_state, timeout_alert, oper, project, project_detail, create_date, create_depart, author, workload, urgency, requirements_level, recieve_depart, requirements_user, requirements_desc, expect_pre_ana_date, real_pre_ana_date, transfer_survey_date, first_survey_date, expect_ana_start_date, expect_ana_finish_date, ana_finish_date, finish_date, review_date, expect_start_date, expect_test_date, design_finish_date, inner_test_date, confirm_start_date, confirm_finish_date, expect_release_date, release_date, release_code, evaluate_date, rate, admin_rate, summary, admin_summary, model_ana_workload, analytic_workload, model_dev_workload, develop_workload, id) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyy/mm/dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), to_date(?, 'yyyy/mm/dd hh24:mi:ss'), ?, to_date(?, 'yyyy/mm/dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
        log.debug("全字段新增需求EOMS单脚本：" + sql.toString());

        try {
            log.debug("全字段新增需求EOMS单参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("全字段新增需求EOMS单异常：" + e.getMessage(), e);
        }
        return insertCount;
    }

    /**
     * 全字段更新需求EOMS单
     * @param paramList
     * @return
     * @throws Exception
     */
    @Override
    public int updatetRequirementsEOMSOrderAllCol(List paramList) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ot_requirements_eoms_order oreo ");
        sql.append("SET oreo.requirements_title = ?, ");
        sql.append("oreo.requirements_state = ?, ");
        sql.append("oreo.process_state = ?, ");
        sql.append("oreo.timeout_alert = ?, ");
        sql.append("oreo.oper = ?, ");
        sql.append("oreo.project = ?, ");
        sql.append("oreo.project_detail = ?, ");
        sql.append("oreo.create_date = ?, ");
        sql.append("oreo.create_depart = ?, ");
        sql.append("oreo.author = ?, ");
        sql.append("oreo.workload = ?, ");
        sql.append("oreo.urgency = ?, ");
        sql.append("oreo.requirements_level = ?, ");
        sql.append("oreo.recieve_depart = ?, ");
        sql.append("oreo.requirements_user = ?, ");
        sql.append("oreo.requirements_desc = ?, ");
        sql.append("oreo.expect_pre_ana_date = ?, ");
        sql.append("oreo.real_pre_ana_date = ?, ");
        sql.append("oreo.transfer_survey_date = ?, ");
        sql.append("oreo.first_survey_date = ?, ");
        sql.append("oreo.expect_ana_start_date = ?, ");
        sql.append("oreo.expect_ana_finish_date = ?, ");
        sql.append("oreo.ana_finish_date = ?, ");
        sql.append("oreo.finish_date = ?, ");
        sql.append("oreo.review_date = ?, ");
        sql.append("oreo.expect_start_date = ?, ");
        sql.append("oreo.expect_test_date = ?, ");
        sql.append("oreo.design_finish_date = ?, ");
        sql.append("oreo.inner_test_date = ?, ");
        sql.append("oreo.confirm_start_date = ?, ");
        sql.append("oreo.confirm_finish_date = ?, ");
        sql.append("oreo.expect_release_date = ?, ");
        sql.append("oreo.release_date = ?, ");
        sql.append("oreo.release_code = ?, ");
        sql.append("oreo.evaluate_date = ?, ");
        sql.append("oreo.rate = ?, ");
        sql.append("oreo.admin_rate = ?, ");
        sql.append("oreo.summary = ?, ");
        sql.append("oreo.admin_summary = ?, ");
        sql.append("oreo.model_ana_workload = ?, ");
        sql.append("oreo.analytic_workload = ?, ");
        sql.append("oreo.model_dev_workload = ?, ");
        sql.append("oreo.develop_workload = ? ");
        sql.append("WHERE oreo.requirements_code = ? ");
        log.debug("全字段更新需求EOMS单脚本：" + sql.toString());

        try {
            log.debug("全字段更新需求EOMS单参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("全字段更新需求EOMS单异常", e);
        }
        return updateCount;
    }

    /**
     * 获取需求EOMS单列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getRequirementsEOMSOrderList(Map qryMap) throws Exception {
        List orderList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();

        sql.append("SELECT *, row_index FROM ( ");
        sql.append("SELECT a.*, ROWNUM FROM ( ");
        sql.append("SELECT oreo.id, oreo.requirements_code, oreo.requirements_title, oreo.requirements_state, oreo.process_state, ");
        sql.append("oreo.timeout_alert, oreo.oper, oreo.project, oreo.project_detail, to_char(oreo.create_date, 'yyyy/mm/dd hh24:mi:ss') create_date, ");
        sql.append("oreo.create_depart, oreo.author, oreo.workload, oreo.urgency, oreo.requirements_level, ");
        sql.append("oreo.recieve_depart, oreo.requirements_user, oreo.requirements_desc, to_char(oreo.expect_pre_ana_date, 'yyyy/mm/dd hh24:mi:ss') expect_pre_ana_date, to_char(oreo.real_pre_ana_date, 'yyyy/mm/dd hh24:mi:ss') real_pre_ana_date, ");
        sql.append("to_char(oreo.transfer_survey_date, 'yyyy/mm/dd hh24:mi:ss') transfer_survey_date, to_char(oreo.first_survey_date, 'yyyy/mm/dd hh24:mi:ss') first_survey_date, to_char(oreo.expect_ana_start_date, 'yyyy/mm/dd hh24:mi:ss') expect_ana_start_date, to_char(oreo.expect_ana_finish_date, 'yyyy/mm/dd hh24:mi:ss') expect_ana_finish_date, to_char(oreo.ana_finish_date, 'yyyy/mm/dd hh24:mi:ss') ana_finish_date, ");
        sql.append("to_char(oreo.finish_date, 'yyyy/mm/dd hh24:mi:ss') finish_date, to_char(oreo.review_date, 'yyyy/mm/dd hh24:mi:ss') review_date, to_char(oreo.expect_start_date, 'yyyy/mm/dd hh24:mi:ss') expect_start_date, to_char(oreo.expect_test_date, 'yyyy/mm/dd hh24:mi:ss') expect_test_date, to_char(oreo.design_finish_date, 'yyyy/mm/dd hh24:mi:ss') design_finish_date, ");
        sql.append("to_char(oreo.inner_test_date, 'yyyy/mm/dd hh24:mi:ss') inner_test_date, to_char(oreo.confirm_start_date, 'yyyy/mm/dd hh24:mi:ss') confirm_start_date, to_char(oreo.confirm_finish_date, 'yyyy/mm/dd hh24:mi:ss') confirm_finish_date, to_char(oreo.expect_release_date, 'yyyy/mm/dd hh24:mi:ss') expect_release_date, to_char(oreo.release_date, 'yyyy/mm/dd hh24:mi:ss') release_date, ");
        sql.append("oreo.release_code, to_char(oreo.evaluate_date, 'yyyy/mm/dd hh24:mi:ss') evaluate_date, oreo.rate, oreo.admin_rate, oreo.summary, ");
        sql.append("oreo.admin_summary, oreo.model_ana_workload, oreo.analytic_workload, oreo.model_dev_workload, oreo.develop_workload ");
        sql.append("FROM ot_requirements_eoms_order oreo ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "requirementsCode", ""))) {
            sql.append("AND oreo.requirements_code = ? ");
            paramList.add(MapUtils.getString(qryMap, "requirementsCode", ""));
        }
        sql.append("ORDER BY oreo.requirements_code) a ");
        sql.append(") ");
        sql.append("WHERE row_index >= ? ");
        sql.append("AND row_index <= ? ");
        log.debug("获取需求EOMS单列表脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "startIndex", ""));
            paramList.add(MapUtils.getString(qryMap, "endIndex", ""));
            log.debug("获取需求EOMS单列表参数：" + paramList);
            orderList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取需求EOMS单列表异常", e);
        }
        return orderList;
    }

    /**
     * 获取需求EOMS单列表总数
     *
     * @param qryMap
     * @return
     */
    @Override
    public int getRequirementsEOMSOrderListCount(Map qryMap) throws Exception {
        int orderListCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();

        sql.append("SELECT COUNT(1) ORDER_LIST_COUNT ");
        sql.append("FROM ot_requirements_eoms_order oreo ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "requirementsCode", ""))) {
            sql.append("AND oreo.requirements_code = ? ");
            paramList.add(MapUtils.getString(qryMap, "requirementsCode", ""));
        }
        log.debug("获取需求EOMS单列表总数脚本：" + sql.toString());

        try {
            log.debug("获取需求EOMS单列表总数参数：" + paramList);
            List orderCountList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (orderCountList != null && orderCountList.size() > 0) {
                Map orderCountMap = (Map) orderCountList.get(0);
                orderListCount = MapUtils.getIntValue(orderCountMap, "ORDER_LIST_COUNT", 0);
            }
        } catch (Exception e) {
            throw new Exception("获取需求EOMS单列表总数异常", e);
        }
        return orderListCount;
    }
}
