package com.ztesoft.iom.manage.monitor.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.monitor.dao.MonitorInfoDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 系统监控业务类
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 16:51
 */
public class MonitorService {

    private static Logger log = LogManager.getLogger(MonitorService.class);
    private MonitorInfoDao monitorInfoDao;

    /**
     * 获取采集信息列表
     * @param paramJson
     * @return
     */
    public JSONObject getGatherInfo(JSONObject paramJson) {
        JSONObject returnJson = new JSONObject();
        boolean serviceFlag = false;
        try {
            monitorInfoDao = (MonitorInfoDao) BeanFactory.getApplicationContext().getBean("monitorInfoDaoImpl");
            Map qryMap = new HashMap();
            qryMap.put("gatherType", paramJson.getString("gatherType"));
            qryMap.put("gatherName", paramJson.getString("gatherName"));
            qryMap.put("gatherMachineDesc", paramJson.getString("gatherMachineDesc"));
            qryMap.put("startTime", paramJson.getString("startTime"));
            qryMap.put("endTime", paramJson.getString("endTime"));

            JSONObject gatherJson = new JSONObject();
            List gatherInfoList = monitorInfoDao.getGatherInfo(qryMap);
            for (int i = 0; i < gatherInfoList.size(); i++) {
                Map gatherInfoMap = (Map) gatherInfoList.get(i);

                JSONArray gatherMachineArray = new JSONArray();
                if(gatherJson.containsKey(MapUtils.getString(gatherInfoMap, "GATHER_MACHINE"))) {
                    gatherMachineArray = gatherJson.getJSONArray(MapUtils.getString(gatherInfoMap, "GATHER_MACHINE"));
                }
                JSONObject gatherMachineJson = new JSONObject();
                gatherMachineJson.put("gatherMachineDesc", MapUtils.getString(gatherInfoMap, "GATHER_MACHINE_DESC"));
                gatherMachineJson.put("gatherTime", MapUtils.getString(gatherInfoMap, "GATHER_TIME"));
                gatherMachineJson.put("gatherValue", MapUtils.getString(gatherInfoMap, "GATHER_VALUE"));
                gatherMachineArray.add(gatherMachineJson);
                gatherJson.put(MapUtils.getString(gatherInfoMap, "GATHER_MACHINE"), gatherMachineArray);
            }

            returnJson.put("gatherInfo", gatherJson);
            serviceFlag = true;
        } catch (Exception e) {
            log.error("获取采集信息列表异常", e);
        } finally {
            returnJson.put("serviceFlag", serviceFlag);
        }
        return returnJson;
    }

}
