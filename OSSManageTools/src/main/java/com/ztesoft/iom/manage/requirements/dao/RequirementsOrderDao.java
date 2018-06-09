package com.ztesoft.iom.manage.requirements.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 需求任务单数据库操作接口
 * @author: huang.jing
 * @Date: 2018/1/23 0023 - 15:52
 */
public interface RequirementsOrderDao {

    /**
     * 获取需求模块相关序列
     *
     * @param qryMap
     * @return
     */
    public String getRequirementsModuleSeq(Map qryMap) throws Exception;

    /**
     * 全字段新增需求EOMS单
     * @param paramList
     * @return
     * @throws Exception
     */
    public int insertRequirementsEOMSOrderAllCol(List paramList) throws Exception;

    /**
     * 全字段更新需求EOMS单
     * @param paramList
     * @return
     * @throws Exception
     */
    public int updatetRequirementsEOMSOrderAllCol(List paramList) throws Exception;

    /**
     * 获取需求EOMS单列表
     *
     * @param qryMap
     * @return
     */
    public List getRequirementsEOMSOrderList(Map qryMap) throws Exception;

    /**
     * 获取需求EOMS单列表总数
     *
     * @param qryMap
     * @return
     */
    public int getRequirementsEOMSOrderListCount(Map qryMap) throws Exception;
}
