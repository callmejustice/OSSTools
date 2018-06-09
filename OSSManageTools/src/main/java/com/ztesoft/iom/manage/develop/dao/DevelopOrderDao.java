package com.ztesoft.iom.manage.develop.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 研发任务单数据库操作接口
 * @author: huang.jing
 * @Date: 2018/1/13 0013 - 21:46
 */
public interface DevelopOrderDao {

    /**
     * 获取研发模块相关序列
     *
     * @param qryMap
     * @return
     */
    public String getDevelopModuleSeq(Map qryMap) throws Exception;

    /**
     * 新建研发任务单
     *
     * @param insertMap
     * @return
     */
    public int insertDevelopOrder(Map insertMap) throws Exception;

    /**
     * 获取研发任务单列表
     *
     * @param qryMap
     * @return
     */
    public List getDevelopOrderList(Map qryMap) throws Exception;

    /**
     * 获取研发任务单列表总数
     *
     * @param qryMap
     * @return
     */
    public int getDevelopOrderListCount(Map qryMap) throws Exception;

    /**
     * 更新研发任务单
     *
     * @param updateMap
     * @return
     */
    public int updateDevelopOrder(Map updateMap) throws Exception;

    /**
     * 获取SVN日志列表
     *
     * @param qryMap
     * @return
     */
    public List getSvnLogList(Map qryMap) throws Exception;

    /**
     * 获取SVN日志列表数量
     *
     * @param qryMap
     * @return
     */
    public int getSvnLogListCount(Map qryMap) throws Exception;

    /**
     * 获取SVN日志的文件变更记录
     *
     * @param qryMap
     * @return
     */
    public List getSvnLogChangeList(Map qryMap) throws Exception;

    /**
     * 记录研发任务单和svn提交记录的关系
     *
     * @param insertMap
     * @return
     */
    public int insertDevelopSvnRela(Map insertMap) throws Exception;

    /**
     * 获取任务带关联的svn日志列表
     *
     * @param qryMap
     * @return
     */
    public List getSvnLogRelaList(Map qryMap) throws Exception;

    /**
     * 获取任务带关联的svn变更文件列表
     *
     * @param qryMap
     * @return
     */
    public List getSvnLogRelaChangeList(Map qryMap) throws Exception;

    /**
     * 获取版本计划
     *
     * @param qryMap
     * @return
     */
    public List getVersionList(Map qryMap) throws Exception;
}
