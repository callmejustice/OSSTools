package com.ztesoft.iom.pretreatment.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 预处理群障数据库操作接口类
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 17:09
 */
public interface AvoidanceGroupDao {

    /**
     * 获取待汇总的群障记录
     * @param param
     * @return
     */
    public List getAvoidanceGroupNotSumList(Map param);

    /**
     * 获取预处理群障树序列
     * @return
     */
    public String getAvoidanceGroupLogSeq();

    /**
     * 记录预处理群障树日志
     * @param paramList
     * @return
     */
    public int insertAvoidanceGroupLogBatch(List paramList);

    /**
     * 获取预处理群障树信息
     * @param param
     * @return
     */
    public List getAvoidanceGroupSumList(Map param);

    /**
     * 记录预处理群障树信息
     * @param param
     * @return
     */
    public int insertAvoidanceGroupSum(Map param);

    /**
     * 更新预处理群障树信息
     * @param param
     * @return
     */
    public int updateAvoidanceGroupSum(Map param);

    /**
     * 获取待关联为投诉单或者抱怨单的列表
     * @param param
     * @return
     */
    public List getAvoidanceGroupNotRelaList(Map param);

    /**
     * 获取预处理群障号码信息
     * @param param
     * @return
     */
    public Map getAvoidanceGroupNumInfo(Map param);


    /**
     * 更新预处理群障树日志
     * @param param
     * @return
     */
    public int updateAvoidanceGroupLog(Map param);
}
