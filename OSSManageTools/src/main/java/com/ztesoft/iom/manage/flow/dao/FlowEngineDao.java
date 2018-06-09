package com.ztesoft.iom.manage.flow.dao;

import java.util.Map;

/**
 * @Description: 流程引擎数据库操作类
 * @author: huang.jing
 * @Date: 2018/1/14 0014 - 16:07
 */
public interface FlowEngineDao {

    /**
     * 获取流程引擎相关序列
     *
     * @param qryMap
     * @return
     */
    public String getFlowEngineSeq(Map qryMap) throws Exception;

    /**
     * 新建流程实例
     *
     * @param insertMap
     * @return
     */
    public int insertFlowInstance(Map insertMap) throws Exception;

    /**
     * 新建线条实例
     *
     * @param insertMap
     * @return
     */
    public int insertTransitionInstance(Map insertMap) throws Exception;

    /**
     * 新建活动实例
     *
     * @param insertMap
     * @return
     */
    public int insertActivityInstance(Map insertMap) throws Exception;

    /**
     * 新建工作项
     *
     * @param insertMap
     * @return
     */
    public int insertWorkitem(Map insertMap) throws Exception;

    /**
     * 更新流程实例
     *
     * @param updateMap
     * @return
     */
    public int updateFlowInstance(Map updateMap) throws Exception;

    /**
     * 获取活动实例信息
     *
     * @param qryMap
     * @return
     */
    public Map getWokritemAndActivityInstanceMap(Map qryMap) throws Exception;

    /**
     * 更新活动实例
     *
     * @param updateMap
     * @return
     */
    public int updateActivityInstance(Map updateMap) throws Exception;

    /**
     * 更新工作项
     *
     * @param updateMap
     * @return
     */
    public int updateWorkitem(Map updateMap) throws Exception;
}
