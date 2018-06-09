package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.MD5Util;
import com.ztesoft.iom.pretreatment.dao.AnnouncementDao;
import com.ztesoft.iom.pretreatment.dao.impl.AnnouncementDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 预处理公告树定时任务
 * @author: huang.jing
 * @Date: 2018/1/3 0003 - 16:30
 */
public class PretreatmentAnnouncement {

    private static Logger log = LogManager.getLogger(PretreatmentAnnouncement.class);
    // 公告树各级节点映射名称表
    HashMap nodeLevelNameMap;
    // 树节点索引MAP
    HashMap nodeIndexMap;
    // 各级节点归类公告树
    JSONArray[] announcementLevelJsonArray;
    // 公告树的层级
    int treeLevel = 9;
    AnnouncementDao announcementDao;

    public void execute() {
        log.info("PretreatmentAnnouncement入口……");
        HashMap param = new HashMap();
        announcementDao = (AnnouncementDaoImpl) BeanFactory.getApplicationContext().getBean("announcementDaoImpl");
        List announcementNotSumList = announcementDao.getAnnouncementNotSumList(param);
        log.debug("announcementNotSumList：" + announcementNotSumList);
        announcementLevelJsonArray = new JSONArray[treeLevel];
        nodeIndexMap = new HashMap();
        nodeLevelNameMap = new HashMap();
        // 一级地址
        nodeLevelNameMap.put("1", "ADDR_FISRT");
        // 二级地址
        nodeLevelNameMap.put("2", "ADDR_SECOND");
        // 三级地址
        nodeLevelNameMap.put("3", "ADDR_THIRD");
        // 四级地址
        nodeLevelNameMap.put("4", "ADDR_THOURTH");
        // 五级地址
        nodeLevelNameMap.put("5", "ADDR_FIFTH");
        // 六级地址
        nodeLevelNameMap.put("6", "ADDR_SIXTH");
        // 七级地址
        nodeLevelNameMap.put("7", "ADDR_SEVENTH");
        // 八级地址
        nodeLevelNameMap.put("8", "ADDR_EIGHTH");
        // 九级地址
        nodeLevelNameMap.put("9", "ADDR_NINTH");
        // 初始化数组
        for (int i = 0; i < treeLevel; i++) {
            announcementLevelJsonArray[i] = new JSONArray();
        }

        List announcementLogParamList = new ArrayList();
        // 循环处理列表，构造成树
        for (int i = 0; i < announcementNotSumList.size(); i++) {
            Map announcementNotSumMap = (Map) announcementNotSumList.get(i);
            // 群障ID
            String batchId = MapUtils.getString(announcementNotSumMap, "BATCH_ID");
            // 号码
            String accnbr = MapUtils.getString(announcementNotSumMap, "ACC_NBR");
            List announcementLogList = new ArrayList();
            announcementLogList.add(batchId);
            announcementLogList.add(accnbr);
            announcementLogList.add("0");
            announcementLogParamList.add(announcementLogList.toArray());
            try {
                setAnnouncementJson(announcementNotSumMap);
            } catch (Exception e) {
                log.error("构造群障树异常", e);
            }
        }
        // 批量插入本次构造为树的号码信息
        try {
            int insertCount = announcementDao.insertAnnouncementLogBatch(announcementLogParamList);
            log.info("批量插入本次构造为树的号码信息共" + insertCount + "条");
        } catch (Exception e) {
            log.error("批量插入本次构造为树的号码信息异常", e);
        }

        // 同一个批次的树节点信息可能已经存在数据库中，需要先查询，不适合批量更新
        for (int i = 0; i < announcementLevelJsonArray.length; i++) {
            JSONArray announcementJsonArray = announcementLevelJsonArray[i];
            log.debug("AnnouncementLevelJsonArray[" + i + "]：" + announcementJsonArray);
            for (int j = 0; j < announcementJsonArray.size(); j++) {
                try {
                    Map announcementMap = announcementJsonArray.getJSONObject(j);
                    if (announcementDao.getAnnouncementSumList(announcementMap).size() == 0) {
                        announcementDao.insertAnnouncementSum(announcementMap);
                    } else {
                        announcementMap.put("updateType", "1");
                        announcementDao.updateAnnouncementSum(announcementMap);
                    }
                } catch (Exception e) {
                    log.error("记录预处理群障树信息异常", e);
                }
            }
        }

        // 获取待关联为投诉单或者抱怨单的列表，并更新到树中
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        List announcementNotRelaList = announcementDao.getAnnouncementNotRelaList(param);
        log.debug("announcementNotRelaList：" + announcementNotRelaList);
        for (int i = 0; i < announcementNotRelaList.size(); i++) {
            Map announcementNotRelaMap = (Map) announcementNotRelaList.get(i);
            updateAnnouncementSumInfo(announcementNotRelaMap, sdf, nowDate);
        }

        log.info("PretreatmentAnnouncement出口……");
    }

    /**
     * 将待处理的预处理群障信息拼装成为json
     * 本方法只构造树的各级节点，节点投诉单数量，抱怨单数量默认都是0
     *
     * @param announcementNotSumMap
     */
    private void setAnnouncementJson(Map announcementNotSumMap) {
        HashMap nodeInfoMap = new HashMap();
        // 群障ID
        String batchId = MapUtils.getString(announcementNotSumMap, "BATCH_ID");
        // 投诉单数量
        int complainCount = 0;
        // 抱怨单数量
        int summaryCount = 0;
        // 号码数量
        int numberCount = 1;

        // 循环处理，将用户的地址更新到对应的层级列表中
        for (int i = 0; i < treeLevel; i++) {
            /**
             * 当前节点的完全地址，如：
             * 第二级：广西南宁
             * 第三级：广西南宁青秀区
             */
            String nodeNameFull = "";
            for (int j = 0; j <= i; j++) {
                // 用户第j级的标准地址映射名
                String parentLevelName = MapUtils.getString(nodeLevelNameMap, (j + 1) + "");
                // 若用户某一级的地址为空，则用“其它”来代替
                nodeNameFull += MapUtils.getString(announcementNotSumMap, parentLevelName) == null || "".equals(MapUtils.getString(announcementNotSumMap, parentLevelName)) ? "其它" : MapUtils.getString(announcementNotSumMap, parentLevelName);
            }
            // 使用当前节点的完全地址生成MD5，用于快速搜索，使用MD5加密是为了防止地址长度太长，加上批次号是为了批次唯一性
            String md5 = MD5Util.MD5Encode(nodeNameFull + batchId);

            JSONObject announcementJson = new JSONObject();
            // 用户第i+1级的标准地址映射名
            String addressLevelName = MapUtils.getString(nodeLevelNameMap, (i + 1) + "");
            // 用户第i+1级的标准地址，若地址为空，则用“其它”来代替
            String address = MapUtils.getString(announcementNotSumMap, addressLevelName) == null || "".equals(MapUtils.getString(announcementNotSumMap, addressLevelName)) ? "其它" : MapUtils.getString(announcementNotSumMap, addressLevelName);
            // 如果存在节点索引，则从当前保存的节点列表中获取节点，用户获取后续信息
            if (nodeIndexMap.containsKey(md5)) {
                for (int j = 0; j < announcementLevelJsonArray[i].size(); j++) {
                    if (md5.equals(announcementLevelJsonArray[i].getJSONObject(j).get("md5"))) {
                        announcementJson = announcementLevelJsonArray[i].getJSONObject(j);
                        // 记录影响号码数量
                        announcementJson.put("numberCount", announcementJson.getIntValue("numberCount") + 1);
                        break;
                    }
                }
            } else {
                // 不存在节点索引信息，新建节点索引信息，且新建节点json
                String nodeId = announcementDao.getAnnouncementLogSeq();
                String parentId = "";
                if (i > 0) {
                    parentId = MapUtils.getString(nodeInfoMap, (i - 1) + "");
                }
                nodeIndexMap.put(md5, nodeNameFull);
                announcementJson.put("id", nodeId);
                announcementJson.put("batchId", batchId);
                announcementJson.put("parentId", parentId);
                announcementJson.put("nodeLevel", i);
                announcementJson.put("nodeName", address);
                announcementJson.put("md5", md5);
                announcementJson.put("complainCount", complainCount);
                announcementJson.put("summaryCount", summaryCount);
                announcementJson.put("numberCount", numberCount);
                announcementLevelJsonArray[i].add(announcementJson);
            }
            nodeInfoMap.put(i + "", announcementJson.getString("id"));
        }
    }

    /**
     * 更新用户群障信息
     *
     * @param announcementNotRelaMap
     * @param sdf
     * @param nowDate
     */
    private void updateAnnouncementSumInfo(Map announcementNotRelaMap, SimpleDateFormat sdf, Date nowDate) {
        // 投诉单数量
        int complainCount = 0;
        // 抱怨单数量
        int summaryCount = 0;
        String state = "0";
        String batchId = MapUtils.getString(announcementNotRelaMap, "BATCH_ID");
        String accnbr = MapUtils.getString(announcementNotRelaMap, "ACC_NBR");
        // 投诉单立单时间
        String eomsCreateDate = MapUtils.getString(announcementNotRelaMap, "EOMS_CREATE_DATE");
        // 号码入库时间
        String createDate = MapUtils.getString(announcementNotRelaMap, "CREATE_DATE");
        // 号码查询大面积故障时间
        String qryDate = MapUtils.getString(announcementNotRelaMap, "QRY_DATE");

        try {
            if (eomsCreateDate != null && !"".equals(eomsCreateDate)
                    && qryDate != null && !"".equals(qryDate)) { // 已立投诉单
                // 号码查询大面积故障时间和投诉单入库时间相差大于两个小时，认为用户立了抱怨单
                if (sdf.parse(eomsCreateDate).getTime() - sdf.parse(qryDate).getTime() > 2 * 60 * 60 * 1000
                        || sdf.parse(qryDate).getTime() - sdf.parse(eomsCreateDate).getTime() > 2 * 60 * 60 * 1000) {
                    summaryCount++;
                    state = "2";
                } else {
                    // 号码查询大面积故障时间和投诉单入库时间相差小于等于两个小时，认为用户立了投诉单
                    complainCount++;
                    state = "1";
                }
            } else if ((eomsCreateDate == null && "".equals(eomsCreateDate))
                    || (qryDate != null && !"".equals(qryDate))) { // 未立投诉单
                // 号码查询大面积故障时间和当前时间相差大于两个小时且没有立投诉单，认为用户立了抱怨单
                if (sdf.parse(sdf.format(new Date())).getTime() - sdf.parse(qryDate).getTime() > 2 * 60 * 60 * 1000
                        || sdf.parse(qryDate).getTime() - sdf.parse(sdf.format(new Date())).getTime() > 2 * 60 * 60 * 1000) {
                    summaryCount++;
                    state = "2";
                }
            }

            if (complainCount + summaryCount > 0) {
                Map param = new HashMap();
                param.put("accnbr", accnbr);
                Map announcementNumInfo = announcementDao.getAnnouncementNumInfo(param);
                log.debug("用户地址信息：" + announcementNumInfo);
                // 循环处理，将用户的地址更新到对应的层级列表中
                for (int i = 0; i < treeLevel; i++) {
                    /**
                     * 当前节点的完全地址，如：
                     * 第二级：广西南宁
                     * 第三级：广西南宁青秀区
                     */
                    String nodeNameFull = "";
                    for (int j = 0; j <= i; j++) {
                        // 用户第j级的标准地址映射名
                        String parentLevelName = MapUtils.getString(nodeLevelNameMap, (j + 1) + "");
                        // 若用户某一级的地址为空，则用“其它”来代替
                        nodeNameFull += MapUtils.getString(announcementNumInfo, parentLevelName) == null || "".equals(MapUtils.getString(announcementNumInfo, parentLevelName)) ? "其它" : MapUtils.getString(announcementNumInfo, parentLevelName);
                    }
                    // 使用当前节点的完全地址生成MD5，用于快速搜索，使用MD5加密是为了防止地址长度太长
                    String md5 = MD5Util.MD5Encode(nodeNameFull + batchId);

                    param.put("complainCount", complainCount);
                    param.put("summaryCount", summaryCount);
                    param.put("batchId", batchId);
                    param.put("md5", md5);
                    announcementDao.updateAnnouncementSum(param);
                }
                param.put("state", state);
                announcementDao.updateAnnouncementLog(param);
            }
        } catch (Exception e) {
            log.error("更新用户群障信息异常", e);
        }
    }

}
