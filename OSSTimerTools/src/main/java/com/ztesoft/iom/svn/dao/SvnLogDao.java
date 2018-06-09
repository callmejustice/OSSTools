package com.ztesoft.iom.svn.dao;

import java.util.Map;

/**
 * @Description: svn日志操作dao接口
 * @author: huang.jing
 * @Date: 2018/1/18 0018 - 15:03
 */
public interface SvnLogDao {

    /**
     * 获取svn库表相关序列
     * @param qryMap
     * @return
     * @throws Exception
     */
    public String getSvnLogSeq(Map qryMap) throws Exception;

    /**
     * 查询svn版本号数量
     * @param qryMap
     * @return
     * @throws Exception
     */
    public int getRevisionCount(Map qryMap) throws Exception;

    /**
     * 插入svn日志记录
     * @param insertMap
     * @return
     * @throws Exception
     */
    public int insertSvnLog(Map insertMap) throws Exception;

    /**
     * 插入svn文件变更记录
     * @param insertMap
     * @return
     * @throws Exception
     */
    public int insertSvnChangeList(Map insertMap) throws Exception;

    /**
     * 获取动态参数值
     * @param qryMap
     * @return
     * @throws Exception
     */
    public String getParamConfigValue(Map qryMap) throws Exception;

    /**
     * 更新动态参数值
     * @param updateMap
     * @return
     * @throws Exception
     */
    public int updateParamConfigValue(Map updateMap) throws Exception;
}
