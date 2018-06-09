package com.ztesoft.iom.manage.workOrder.dao;

import java.util.Map;

/**
 * @Description: 任务工单数据库操作接口
 * @author: huang.jing
 * @Date: 2018/1/16 0016 - 11:23
 */
public interface WorkOrderDao {
    /**
     * 新建研发任务工单
     *
     * @param insertMap
     * @return
     */
    public int insertWorkOrder(Map insertMap) throws Exception;

    /**
     * 更新研发任务工单
     *
     * @param updateMap
     * @return
     */
    public int updateWorkOrder(Map updateMap) throws Exception;

    /**
     * 获取正在执行中的工单信息
     *
     * @param qryMap
     * @return
     */
    public Map getProcessingWorkOrderMap(Map qryMap) throws Exception;

    /**
     * 记录任务工单操作信息
     *
     * @param insertMap
     * @return
     */
    public int insertOrderOperLog(Map insertMap) throws Exception;
}
