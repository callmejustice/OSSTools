package com.ztesoft.iom.manage.monitor.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 监控信息dao接口
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 16:57
 */
public interface MonitorInfoDao {

    /**
     * 获取采集信息列表
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getGatherInfo(Map qryMap) throws Exception;

}
