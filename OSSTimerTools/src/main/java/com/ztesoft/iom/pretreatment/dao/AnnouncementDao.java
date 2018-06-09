package com.ztesoft.iom.pretreatment.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 预处理公告数据库操作接口类
 * @author: huang.jing
 * @Date: 2018/1/3 0003 - 16:34
 */
public interface AnnouncementDao {
    /**
     * 获取待汇总的公告记录
     * @param param
     * @return
     */
    public List getAnnouncementNotSumList(Map param);

    /**
     * 获取预处理公告树序列
     * @return
     */
    public String getAnnouncementLogSeq();

    /**
     * 记录预处理公告树日志
     * @param paramList
     * @return
     */
    public int insertAnnouncementLogBatch(List paramList);

    /**
     * 获取预处理公告树信息
     * @param param
     * @return
     */
    public List getAnnouncementSumList(Map param);

    /**
     * 记录预处理公告树信息
     * @param param
     * @return
     */
    public int insertAnnouncementSum(Map param);

    /**
     * 更新预处理公告树信息
     * @param param
     * @return
     */
    public int updateAnnouncementSum(Map param);

    /**
     * 获取待关联为投诉单或者抱怨单的列表
     * @param param
     * @return
     */
    public List getAnnouncementNotRelaList(Map param);

    /**
     * 获取预处理公告号码信息
     * @param param
     * @return
     */
    public Map getAnnouncementNumInfo(Map param);


    /**
     * 更新预处理公告树日志
     * @param param
     * @return
     */
    public int updateAnnouncementLog(Map param);
}
