package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.MD5Util;
import com.ztesoft.iom.workOrder.material.dao.MaterialDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 耗材信息统计报表入库定时任务
 * @author: huang.jing
 * @Date: 2018/4/25 0025 - 17:08
 */
public class MaterialReportTimer {

    private static Logger log = LogManager.getLogger(MaterialReportTimer.class);

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("MaterialReportTimer入口……");
        JSONObject indexJson = new JSONObject();
        Map qryMap = new HashMap();
        Map insertMap = new HashMap();

        try {
            MaterialDao materialDaoImpl = (MaterialDao) BeanFactory.getApplicationContext().getBean("materialDaoImpl");

            List reportWorkOrderMaterialBaseInfoList = materialDaoImpl.getReportWorkOrderMaterialBaseInfo(qryMap);
            for (int i = 0; i < reportWorkOrderMaterialBaseInfoList.size(); i++) {
                Map reportWorkOrderMaterialBaseInfoMap = (Map) reportWorkOrderMaterialBaseInfoList.get(i);

                String finishDate = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "FINISH_DATE");
                String userAreaName = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "USER_AREA_NAME");
                String county = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "COUNTY");
                String exchName = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "EXCH_NAME");
                String grid = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "GRID");
                String serviceId = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "SERVICE_ID");
                String constructType = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "CONSTRUCT_TYPE");
                String materialName = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "MATERIAL_NAME");
                String accessType = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "ACCESS_TYPE");
                long useNum = MapUtils.getLongValue(reportWorkOrderMaterialBaseInfoMap, "USE_NUM");
                String unit = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "UNIT");
                String typeName = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "TYPE_NAME");
                String orderId = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "ORDER_ID");
                String workOrderId = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "WORK_ORDER_ID");
                String typeId = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "TYPE_ID");
                String orderCode = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "ORDER_CODE");
                String productType = MapUtils.getString(reportWorkOrderMaterialBaseInfoMap, "PRODUCT_TYPE");

                // 将信息构造成md5，作为主键
                String md5 = MD5Util.MD5Encode(finishDate + userAreaName + county + exchName + grid + serviceId + constructType + materialName + accessType);
                qryMap.put("md5", md5);

                JSONObject reportJson = new JSONObject();
                if(indexJson.containsKey(md5)) {
                    reportJson = indexJson.getJSONObject(md5);
                    reportJson.put("orderNum", reportJson.getLongValue("orderNum") + 1);
                    reportJson.put("useNum", reportJson.getLongValue("useNum") + useNum);
                } else {
                    reportJson.put("md5", md5);
                    reportJson.put("areaName", userAreaName);
                    reportJson.put("county", county);
                    reportJson.put("grid", grid);
                    reportJson.put("exchName", exchName);
                    reportJson.put("serviceId", serviceId);
                    reportJson.put("constType", constructType);
                    reportJson.put("finishDate", finishDate);
                    reportJson.put("materialName", materialName);
                    reportJson.put("accessType", accessType);
                    reportJson.put("orderNum", 1);
                    reportJson.put("useNum", useNum);
                    reportJson.put("unit", unit);
                }
                indexJson.put(md5, reportJson);

                // 插入日志记录
                insertMap.clear();
                insertMap.put("orderId", orderId);
                insertMap.put("workOrderId", workOrderId);
                insertMap.put("orderCode", orderCode);
                insertMap.put("typeId", typeId);
                insertMap.put("productType", productType);
                insertMap.put("md5", md5);
                insertMap.put("typeName", typeName);
                insertMap.put("useNum", useNum);
                insertMap.put("unit", unit);
                materialDaoImpl.insertReportWorkOrderMaterialLog(insertMap);
            }

            // 耗材信息统计报表信息
            Iterator it = indexJson.keySet().iterator();
            while (it.hasNext()) {
                String keyName = (String) it.next();
                Map paramMap = indexJson.getObject(keyName, Map.class);
                if(materialDaoImpl.getReportWorkOrderMaterial(paramMap).size() > 0) {
                    materialDaoImpl.updateReportWorkOrderMaterial(paramMap);
                } else {
                    materialDaoImpl.insertReportWorkOrderMaterial(paramMap);
                }
            }
        } catch (Exception e) {
            log.error("耗材信息统计报表入库异常", e);
        }

        log.info("MaterialReportTimer入口……");
    }
}
