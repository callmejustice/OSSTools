package com.ztesoft.iom.workOrder.material.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 耗材信息dao接口类
 * @author: huang.jing
 * @Date: 2018/4/25 0025 - 16:09
 */
public interface MaterialDao {

    /**
     * 插入耗材统计报表日志记录
     *
     * @param insertMap
     * @return
     */
    public int insertReportWorkOrderMaterialLog(Map insertMap) throws Exception;

    /**
     * 获取耗材统计报表基本信息
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getReportWorkOrderMaterialBaseInfo(Map qryMap) throws Exception;

    /**
     * 获取耗材统计报表信息
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getReportWorkOrderMaterial(Map qryMap) throws Exception;

    /**
     * 插入耗材统计报表
     *
     * @param insertMap
     * @return
     */
    public int insertReportWorkOrderMaterial(Map insertMap) throws Exception;

    /**
     * 更新耗材统计报表
     *
     * @param updateMap
     * @return
     */
    public int updateReportWorkOrderMaterial(Map updateMap) throws Exception;
}
