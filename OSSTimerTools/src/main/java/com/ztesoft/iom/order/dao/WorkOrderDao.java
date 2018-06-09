package com.ztesoft.iom.order.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 开通工单dao接口
 * @author: huang.jing
 * @Date: 2018/3/15 0015 - 11:41
 */
public interface WorkOrderDao {

    /**
     * 获取待合并签名图片的定单号列表
     * @param param
     * @return
     */
    public List getNotMergeWorkOrderConfirmList(Map param);

    /**
     * 获取待合并的外线施工签名图片列表
     * @param param
     * @return
     */
    public List getWorkOrderConfirmList(Map param);

    /**
     * 插入合并后的签名图片
     * @param param
     * @return
     */
    public int insertWorkOrderConfirmMergePicInfo(Map param);
}
