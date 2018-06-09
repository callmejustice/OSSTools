package com.ztesoft.iom.gather.dao;

import java.util.List;

/**
 * @Description: 信息采集dao接口
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 11:52
 */
public interface GatherDao {

    /**
     * 批量插入采集信息
     * @param paramList
     * @return
     */
    public int insertGatherInfoBatch(List paramList);

}
