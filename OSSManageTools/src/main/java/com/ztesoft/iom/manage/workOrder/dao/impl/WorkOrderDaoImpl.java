package com.ztesoft.iom.manage.workOrder.dao.impl;

import com.ztesoft.iom.manage.workOrder.dao.WorkOrderDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 任务工单数据库操作实现类
 * @author: huang.jing
 * @Date: 2018/1/16 0016 - 11:25
 */
public class WorkOrderDaoImpl extends JdbcDaoSupport implements WorkOrderDao {

    private static Logger log = LogManager.getLogger(WorkOrderDaoImpl.class);

    /**
     * 新建研发任务工单
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertWorkOrder(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_work_order(work_order_id, order_id, workitem_id, order_type, tache_id, state, create_date, recieve_oper) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, sysdate, ?) ");
        log.debug("新建研发任务工单脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "workOrderId", ""));
            paramList.add(MapUtils.getString(insertMap, "orderId", ""));
            paramList.add(MapUtils.getString(insertMap, "workitemId", ""));
            paramList.add(MapUtils.getString(insertMap, "orderType", ""));
            paramList.add(MapUtils.getString(insertMap, "tacheId", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            paramList.add(MapUtils.getString(insertMap, "recieveOper", ""));
            log.debug("新建研发任务工单参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建研发任务工单异常", e);
        }
        return insertCount;
    }

    /**
     * 更新研发任务工单
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateWorkOrder(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_work_order owo ");
        sql.append("SET ");
        // 状态
        if (!"".equals(MapUtils.getString(updateMap, "state", ""))) {
            // 根据参数表判断是否需要加逗号
            if(paramList.size() > 0) {
                sql.append(", ");
            }
            sql.append("owo.state = ? ");
            paramList.add(MapUtils.getString(updateMap, "state", ""));
        }
        // 工单已竣工时，需要更新结束时间和处理人
        if ("10F".equals(MapUtils.getString(updateMap, "state", ""))) {
            // 根据参数表判断是否需要加逗号
            if(paramList.size() > 0) {
                sql.append(", ");
            }
            sql.append("owo.finish_date = sysdate, owo.deal_oper = ? ");
            paramList.add(MapUtils.getString(updateMap, "author", ""));
        }
        // 接收人
        if (!"".equals(MapUtils.getString(updateMap, "recieveOper", ""))) {
            // 根据参数表判断是否需要加逗号
            if(paramList.size() > 0) {
                sql.append(", ");
            }
            sql.append("owo.recieve_oper = ? ");
            paramList.add(MapUtils.getString(updateMap, "recieveOper", ""));
        }
        sql.append("WHERE owo.work_order_id = ? ");
        log.debug("更新研发任务工单脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "workOrderId", ""));
            log.debug("更新研发任务工单参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("更新研发任务工单异常", e);
        }
        return updateCount;
    }

    /**
     * 获取正在执行中的工单信息
     *
     * @param qryMap
     * @return
     */
    @Override
    public Map getProcessingWorkOrderMap(Map qryMap) throws Exception {
        Map processingWorkOrderMap = new HashMap();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT owo.work_order_id, owo.workitem_id, owo.order_type, owo.tache_id, owo.recieve_oper ");
        sql.append("FROM ot_work_order owo ");
        sql.append("WHERE owo.state <> '10F' ");
        sql.append("AND owo.order_type = ? ");
        sql.append("AND owo.work_order_id = ? ");
        log.debug("获取正在执行中的工单信息脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "orderType", ""));
            paramList.add(MapUtils.getString(qryMap, "workOrderId", ""));
            log.debug("获取正在执行中的工单信息参数：" + paramList);
            List processingWorkOrderList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (processingWorkOrderList != null && processingWorkOrderList.size() > 0) {
                processingWorkOrderMap = (Map) processingWorkOrderList.get(0);
            }
        } catch (Exception e) {
            throw new Exception("获取正在执行中的工单信息异常", e);
        }
        return processingWorkOrderMap;
    }

    /**
     * 记录任务工单操作信息
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertOrderOperLog(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_order_oper_log(oper_log_id, order_id, work_order_id, oper, oper_type, oper_time, remark) ");
        sql.append("VALUES(?, ?, ?, ?, ?, sysdate, ?) ");
        log.debug("记录任务工单操作信息脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "operLogId", ""));
            paramList.add(MapUtils.getString(insertMap, "orderId", ""));
            paramList.add(MapUtils.getString(insertMap, "workOrderId", ""));
            paramList.add(MapUtils.getString(insertMap, "oper", ""));
            paramList.add(MapUtils.getString(insertMap, "operType", ""));
            paramList.add(MapUtils.getString(insertMap, "remark", ""));
            log.debug("记录任务工单操作信息参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("记录任务工单操作信息异常", e);
        }
        return insertCount;
    }
}
