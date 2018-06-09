package com.ztesoft.iom.manage.develop.dao.impl;

import com.ztesoft.iom.manage.develop.dao.DevelopOrderDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.*;

/**
 * @Description: 研发任务单数据库操作实现类
 * @author: huang.jing
 * @Date: 2018/1/13 0013 - 21:50
 */
public class DevelopOrderDaoImpl extends JdbcDaoSupport implements DevelopOrderDao {

    private static Logger log = LogManager.getLogger(DevelopOrderDaoImpl.class);

    /**
     * 获取研发模块相关序列
     *
     * @param qryMap
     * @return
     */
    @Override
    public String getDevelopModuleSeq(Map qryMap) throws Exception {
        String seq = "";
        StringBuffer sql = new StringBuffer();

        try {
            // 在序列前加上yyyymmddhh24miss，每秒最多可以生成10000个不重复的序列，序列长度为固定18位
            sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(" + MapUtils.getString(qryMap, "seqName") + ".nextval, '0000')) seq FROM dual ");
            log.debug("获取研发模块相关序列脚本：" + sql.toString());
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ", "");
            }
        } catch (Exception e) {
            throw new Exception("获取研发模块相关序列异常", e);
        }
        return seq;
    }

    /**
     * 新建研发任务单
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertDevelopOrder(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_develop_order(develop_order_id, flow_instance_id, order_title, zmp_id, author, state, create_date, version) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, sysdate, ?) ");
        log.debug("新建研发任务单脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "developOrderId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "orderTitle", ""));
            paramList.add(MapUtils.getString(insertMap, "zmpId", ""));
            paramList.add(MapUtils.getString(insertMap, "author", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            paramList.add(MapUtils.getString(insertMap, "version", ""));
            log.debug("新建研发任务单参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建研发任务单异常", e);
        }
        return insertCount;
    }

    /**
     * 获取研发任务单列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getDevelopOrderList(Map qryMap) throws Exception {
        List orderList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT a.*, owo.tache_id work_order_tache_id, owo.state work_order_state, NVL(owo.deal_oper, owo.recieve_oper) work_order_oper, owo.create_date work_order_create_date, owo.order_type, ROWNUM row_index ");
        sql.append("FROM (SELECT odo.develop_order_id, odo.order_title, odo.zmp_id, odo.author, odo.state, TO_CHAR(odo.create_date, 'yyyy-mm-dd hh24:mi:ss') create_date, TO_CHAR(odo.finish_date, 'yyyy-mm-dd hh24:mi:ss') finish_date, odo.version, (SELECT MAX(owo.work_order_id) FROM ot_work_order owo WHERE owo.order_id = odo.develop_order_id) max_work_order_id ");
        sql.append("FROM ot_develop_order odo ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND odo.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "developOrderId", ""))) {
            sql.append("AND odo.develop_order_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "developOrderId", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "orderTitle", ""))) {
            sql.append("AND odo.order_title LIKE '%'||?||'%' ");
            paramList.add(MapUtils.getString(qryMap, "orderTitle", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "zmpId", ""))) {
            sql.append("AND odo.zmp_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "zmpId", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "orderState", ""))) {
            sql.append("AND odo.state = ? ");
            paramList.add(MapUtils.getString(qryMap, "orderState", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "version", ""))) {
            sql.append("AND odo.version = ? ");
            paramList.add(MapUtils.getString(qryMap, "version", ""));
        }
        sql.append("ORDER BY odo.create_date) a, ot_work_order owo ");
        sql.append("WHERE a.max_work_order_id = owo.work_order_id ");

        if (!"".equals(MapUtils.getString(qryMap, "orderType", ""))) {
            sql.append("AND owo.order_type = ? ");
            paramList.add(MapUtils.getString(qryMap, "orderType", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "tacheId", ""))) {
            sql.append("AND owo.tache_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "tacheId", ""));
        }
        sql.append(") ");
        sql.append("WHERE row_index >= ? ");
        sql.append("AND row_index <= ? ");
        log.debug("获取研发任务单列表脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "startIndex", ""));
            paramList.add(MapUtils.getString(qryMap, "endIndex", ""));
            log.debug("获取研发任务单列表参数：" + paramList);
            orderList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取研发任务单列表异常", e);
        }
        return orderList;
    }

    /**
     * 获取研发任务单列表总数
     *
     * @param qryMap
     * @return
     */
    @Override
    public int getDevelopOrderListCount(Map qryMap) throws Exception {
        int orderListCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT COUNT(1) order_list_count ");
        sql.append("FROM (SELECT odo.develop_order_id, odo.order_title, odo.zmp_id, odo.author, odo.state, odo.create_date, odo.finish_date, odo.version, (SELECT max(owo.work_order_id) ");
        sql.append("FROM ot_work_order owo WHERE owo.order_id = odo.develop_order_id) max_work_order_id ");
        sql.append("FROM ot_develop_order odo ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND odo.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "developOrderId", ""))) {
            sql.append("AND odo.develop_order_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "developOrderId", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "orderTitle", ""))) {
            sql.append("AND odo.order_title LIKE '%'||?||'%' ");
            paramList.add(MapUtils.getString(qryMap, "orderTitle", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "zmpId", ""))) {
            sql.append("AND odo.zmp_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "zmpId", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "orderState", ""))) {
            sql.append("AND odo.state = ? ");
            paramList.add(MapUtils.getString(qryMap, "orderState", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "version", ""))) {
            sql.append("AND odo.version = ? ");
            paramList.add(MapUtils.getString(qryMap, "version", ""));
        }
        sql.append("ORDER BY odo.create_date) a, ot_work_order owo ");
        sql.append("WHERE a.max_work_order_id = owo.work_order_id ");

        if (!"".equals(MapUtils.getString(qryMap, "orderType", ""))) {
            sql.append("AND owo.order_type = ? ");
            paramList.add(MapUtils.getString(qryMap, "orderType", ""));
        }
        log.debug("获取研发任务单列表总数脚本：" + sql.toString());

        try {
            log.debug("获取研发任务单列表总数参数：" + paramList);
            List orderCountList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (orderCountList != null && orderCountList.size() > 0) {
                Map orderCountMap = (Map) orderCountList.get(0);
                orderListCount = MapUtils.getIntValue(orderCountMap, "ORDER_LIST_COUNT", 0);
            }
        } catch (Exception e) {
            throw new Exception("获取研发任务单列表总数异常", e);
        }
        return orderListCount;
    }

    /**
     * 更新研发任务单
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateDevelopOrder(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_develop_order odo ");
        sql.append("SET odo.state = ? ");
        paramList.add(MapUtils.getString(updateMap, "state", ""));
        // 任务单已竣工时，需要更新结束时间
        if ("10F".equals(MapUtils.getString(updateMap, "state", ""))) {
            sql.append(", odo.finish_date = sysdate ");
        }
        if (!"".equals(MapUtils.getString(updateMap, "version", ""))) {
            sql.append(", odo.version = ? ");
            paramList.add(MapUtils.getString(updateMap, "version", ""));
        }
        sql.append("WHERE odo.develop_order_id = ? ");
        log.debug("更新研发任务单脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "developOrderId", ""));
            log.debug("更新研发任务单参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("更新研发任务单异常", e);
        }
        return updateCount;
    }

    /**
     * 获取SVN日志列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getSvnLogList(Map qryMap) throws Exception {
        List svnLogList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT a.*, ROWNUM row_index FROM (SELECT osl.svn_log_id, osl.revision, osl.author, osl.message, osl.create_date, (SELECT COUNT(1) FROM ot_develop_order_svn_rela odosr WHERE osl.svn_log_id = odosr.svn_log_id) rela_count ");
        sql.append("FROM ot_svn_log osl ");
        sql.append("WHERE 1 = 1 ");
        // 作者
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND osl.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        // 版本号
        if (!"".equals(MapUtils.getString(qryMap, "reversion", ""))) {
            sql.append("AND osl.revision = ? ");
            paramList.add(MapUtils.getString(qryMap, "reversion", ""));
        }
        sql.append("ORDER BY osl.create_date DESC) a ");
        sql.append("WHERE 1 = 1 ");
        // 是否有关联工单
        if (!"".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
            if("1".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
                sql.append("AND a.rela_count > 0 ");
            } else if("0".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
                sql.append("AND a.rela_count <= 0  ");
            }
        }
        sql.append(") ");
        sql.append("WHERE row_index >= ? ");
        sql.append("AND row_index <= ? ");

        log.debug("获取SVN日志列表脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(qryMap, "startIndex", ""));
            paramList.add(MapUtils.getString(qryMap, "endIndex", ""));
            log.debug("获取SVN日志列表参数：" + paramList);
            svnLogList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取SVN日志列表异常", e);
        }
        return svnLogList;
    }

    /**
     * 获取SVN日志列表数量
     *
     * @param qryMap
     * @return
     */
    @Override
    public int getSvnLogListCount(Map qryMap) throws Exception {
        int svnLogListCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT COUNT(1) row_index FROM (SELECT osl.svn_log_id, osl.revision, osl.author, osl.message, osl.create_date, (SELECT COUNT(1) FROM ot_develop_order_svn_rela odosr WHERE osl.svn_log_id = odosr.svn_log_id) rela_count ");
        sql.append("FROM ot_svn_log osl ");
        sql.append("WHERE 1 = 1 ");
        // 作者
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND osl.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        // 版本号
        if (!"".equals(MapUtils.getString(qryMap, "reversion", ""))) {
            sql.append("AND osl.revision = ? ");
            paramList.add(MapUtils.getString(qryMap, "reversion", ""));
        }
        sql.append("ORDER BY osl.create_date DESC) a ");
        sql.append("WHERE 1 = 1 ");
        // 是否有关联工单
        if (!"".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
            if("1".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
                sql.append("AND a.rela_count > 0 ");
            } else if("0".equals(MapUtils.getString(qryMap, "isRelaOrder", ""))) {
                sql.append("AND a.rela_count <= 0  ");
            }
        }
        log.debug("获取SVN日志列表数量脚本：" + sql.toString());

        try {
            log.debug("获取SVN日志列表数量参数：" + paramList);
            List svnLogList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (svnLogList != null && svnLogList.size() > 0) {
                Map svnLogMap = (Map) svnLogList.get(0);
                svnLogListCount = MapUtils.getIntValue(svnLogMap, "SVN_LOG_LIST_COUNT", 0);
            }
        } catch (Exception e) {
            throw new Exception("获取SVN日志列表数量异常", e);
        }
        return svnLogListCount;
    }

    /**
     * 获取SVN日志的文件变更记录
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getSvnLogChangeList(Map qryMap) throws Exception {
        List svnLogChangeList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();

        sql.append("SELECT osc.svn_log_id, osc.path, osc.type, osc.project ");
        sql.append("FROM ot_svn_changelist osc ");
        sql.append("WHERE osc.svn_log_id IN ( ");
        sql.append("SELECT svn_log_id FROM ( ");
        sql.append("SELECT a.*, ROWNUM row_index FROM (SELECT osl.svn_log_id ");
        sql.append("FROM ot_svn_log osl ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND osl.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        sql.append("ORDER BY osl.create_date DESC) a) ");
        sql.append("WHERE row_index >= ? ");
        sql.append("AND row_index <= ? ");
        sql.append(") ");
        log.debug("获取SVN日志的文件变更记录脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "startIndex", ""));
            paramList.add(MapUtils.getString(qryMap, "endIndex", ""));
            log.debug("获取SVN日志的文件变更记录参数：" + paramList);
            svnLogChangeList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取SVN日志的文件变更记录异常", e);
        }
        return svnLogChangeList;
    }

    /**
     * 记录研发任务单和svn提交记录的关系
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertDevelopSvnRela(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_develop_order_svn_rela(id, develop_order_id, work_order_id, svn_log_id) ");
        sql.append("VALUES(?, ?, ?, ?) ");
        log.debug("记录研发任务单和svn提交记录的关系脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "relaId", ""));
            paramList.add(MapUtils.getString(insertMap, "developOrderId", ""));
            paramList.add(MapUtils.getString(insertMap, "workOrderId", ""));
            paramList.add(MapUtils.getString(insertMap, "svnLogId", ""));
            log.debug("记录研发任务单和svn提交记录的关系参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("记录研发任务单和svn提交记录的关系异常", e);
        }
        return insertCount;
    }

    /**
     * 获取任务带关联的svn日志列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getSvnLogRelaList(Map qryMap) throws Exception {
        List svnLogRelaList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT osl.svn_log_id, osl.revision, osl.author, osl.message, osl.create_date ");
        sql.append("FROM ot_svn_log osl, ot_develop_order_svn_rela odosr ");
        sql.append("WHERE osl.svn_log_id = odosr.svn_log_id ");
        sql.append("AND odosr.develop_order_id IN ( ");
        List developOrderList = (List) MapUtils.getObject(qryMap,"developOrderList");
        log.debug("developOrderList：" + developOrderList);
        for (Iterator iterator = developOrderList.iterator(); iterator.hasNext(); ) {
            Map developOrderMap = (Map) iterator.next();
            sql.append("?");
            if(iterator.hasNext()) {
                sql.append(", ");
            }
            paramList.add(MapUtils.getString(developOrderMap, "developOrderId", ""));
        }
        sql.append(") ");
        log.debug("获取任务带关联的svn日志列表脚本：" + sql.toString());
        try {
            log.debug("获取任务带关联的svn日志列表参数：" + paramList);
            svnLogRelaList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取任务带关联的svn日志列表异常", e);
        }
        return svnLogRelaList;
    }

    /**
     * 获取任务带关联的svn变更文件列表
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getSvnLogRelaChangeList(Map qryMap) throws Exception {
        List svnLogChangeRelaList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();

        sql.append("SELECT osc.svn_log_id, osc.path, osc.type, osc.project ");
        sql.append("FROM ot_svn_changelist osc ");
        sql.append("WHERE osc.svn_log_id IN ( ");
        sql.append("SELECT osl.svn_log_id ");
        sql.append("FROM ot_svn_log osl, ot_develop_order_svn_rela odosr ");
        sql.append("WHERE osl.svn_log_id = odosr.svn_log_id ");
        sql.append("AND odosr.develop_order_id IN ( ");
        List developOrderList = (List) MapUtils.getObject(qryMap,"developOrderList");
        log.debug("developOrderList：" + developOrderList);
        for (Iterator iterator = developOrderList.iterator(); iterator.hasNext(); ) {
            Map developOrderMap = (Map) iterator.next();
            sql.append("?");
            if(iterator.hasNext()) {
                sql.append(", ");
            }
            paramList.add(MapUtils.getString(developOrderMap, "developOrderId", ""));
        }
        sql.append(") ");
        sql.append(") ");
        log.debug("获取任务带关联的svn变更文件列表脚本：" + sql.toString());

        try {
            log.debug("获取任务带关联的svn变更文件列表参数：" + paramList);
            svnLogChangeRelaList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取任务带关联的svn变更文件列表异常", e);
        }
        return svnLogChangeRelaList;
    }

    /**
     * 获取版本计划
     *
     * @param qryMap
     * @return
     */
    @Override
    public List getVersionList(Map qryMap) throws Exception {
        List versionList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        // 查询当前时间前后一年的版本计划信息
        sql.append("SELECT ovi.version_id, to_char(ovi.version_date, 'yyyy-mm-dd') version_date, ovi.version_desc ");
        sql.append("FROM ot_version_info ovi ");
        sql.append("WHERE ovi.version_date >= add_months(sysdate, -12) ");
        sql.append("AND ovi.version_date <= add_months(sysdate, 12) ");
        log.debug("获取版本计划脚本：" + sql.toString());
        try {
            log.debug("获取版本计划参数：" + paramList);
            versionList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("获取版本计划异常", e);
        }
        return versionList;
    }
}
