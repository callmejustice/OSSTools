package com.ztesoft.iom.satisfaction.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 满意度问卷dao接口类
 * @author: huang.jing
 * @Date: 2018/4/2 0002 - 15:45
 */
public interface SatisfactionDao {

    /**
     * 获取满意度序列
     *
     * @param qryMap
     * @return
     */
    public String getSatisfactionSeq(Map qryMap) throws Exception;

    /**
     * 插入满意度信息
     *
     * @param insertMap
     * @return
     */
    public int insertSatisfactionInfo(Map insertMap) throws Exception;

    /**
     * 删除满意度信息
     *
     * @param deleteMap
     * @return
     */
    public int deleteSatisfactionInfo(Map deleteMap) throws Exception;

    /**
     * 查询满意度信息列表
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getSatisfactionInfo(Map qryMap) throws Exception;

    /**
     * 查询满意度信息总数
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public Long getSatisfactionInfoCount(Map qryMap) throws Exception;

    /**
     * 查询满意度信息入库记录
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getSatisfactionLog(Map qryMap) throws Exception;

    /**
     * 更新满意度信息入库记录
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    public int updateSatisfactionLog(Map updateMap) throws Exception;

    /**
     * 插入满意度信息入库记录
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    public int insertSatisfactionLog(Map insertMap) throws Exception;

    /**
     * 查询满意度答案
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getSatisfactionAnswer(Map qryMap) throws Exception;

    /**
     * 查询定单信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getOrderInfo(Map qryMap) throws Exception;

    /**
     * 更新订单满意度信息
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    public int updateOrderSatisfactionInfo(Map updateMap) throws Exception;

    /**
     * 获取客服问卷满意度报表原始信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getOrderSatisfactionReportBaseInfo(Map qryMap) throws Exception;

    /**
     * 按网格维度获取客服问卷满意度报表信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getOrderSatisfactionReportInfo(Map qryMap) throws Exception;

    /**
     * 插入客服问卷满意度报表信息
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    public int insertSatisfactionReportInfo(Map insertMap) throws Exception;

    /**
     * 更新客服问卷满意度报表信息
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    public int updateSatisfactionReportInfo(Map updateMap) throws Exception;

    /**
     * 根据id获取需要发送的短信人列表
     *
     * @param qryMap
     * @return
     */
    public List getWarnListById(Map qryMap) throws Exception;

    /**
     * 插入发送短信表
     *
     * @param insertMap
     * @return
     */
    public int insertSmsLog(Map insertMap) throws Exception;
}
