package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.MD5Util;
import com.ztesoft.iom.pretreatment.dao.AvoidanceGroupDao;
import com.ztesoft.iom.pretreatment.dao.impl.AvoidanceGroupDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 预处理群障树定时任务入口
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 17:03
 */
public class PretreatmentAvoidanceGroup {
    private static Logger log = LogManager.getLogger(PretreatmentAvoidanceGroup.class);
    // 群障树各级节点映射名称表
    HashMap nodeLevelNameMap;
    // 树节点索引MAP
    HashMap nodeIndexMap;
    // 各级节点归类群障树
    JSONArray[] avoidanceGroupLevelJsonArray;
    // 群障树的层级
    int treeLevel = 9;
    AvoidanceGroupDao avoidanceGroupDao;

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("PretreatmentAvoidanceGroup入口……");
        HashMap param = new HashMap();
        avoidanceGroupDao = (AvoidanceGroupDaoImpl) BeanFactory.getApplicationContext().getBean("avoidanceGroupDaoImpl");
        List avoidanceGroupNotSumList = avoidanceGroupDao.getAvoidanceGroupNotSumList(param);
        log.debug("avoidanceGroupNotSumList：" + avoidanceGroupNotSumList);
        avoidanceGroupLevelJsonArray = new JSONArray[treeLevel];
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
            avoidanceGroupLevelJsonArray[i] = new JSONArray();
        }

        List avoidanceGroupLogParamList = new ArrayList();
        // 循环处理列表，构造成树
        for (int i = 0; i < avoidanceGroupNotSumList.size(); i++) {
            Map avoidanceGroupNotSumMap = (Map) avoidanceGroupNotSumList.get(i);
            // 群障ID
            String batchId = MapUtils.getString(avoidanceGroupNotSumMap, "BATCH_ID");
            // 号码
            String accnbr = MapUtils.getString(avoidanceGroupNotSumMap, "ACC_NBR");
            List avoidanceGroupLogList = new ArrayList();
            avoidanceGroupLogList.add(batchId);
            avoidanceGroupLogList.add(accnbr);
            avoidanceGroupLogList.add("0");
            avoidanceGroupLogParamList.add(avoidanceGroupLogList.toArray());
            try {
                setAvoidanceGroupJson(avoidanceGroupNotSumMap);
            } catch (Exception e) {
                log.error("构造群障树异常", e);
            }
        }
        // 批量插入本次构造为树的号码信息
        try {
            int insertCount = avoidanceGroupDao.insertAvoidanceGroupLogBatch(avoidanceGroupLogParamList);
            log.info("批量插入本次构造为树的号码信息共" + insertCount + "条");
        } catch (Exception e) {
            log.error("批量插入本次构造为树的号码信息异常", e);
        }

        // 同一个批次的树节点信息可能已经存在数据库中，需要先查询，不适合批量更新
        for (int i = 0; i < avoidanceGroupLevelJsonArray.length; i++) {
            JSONArray avoidanceGroupJsonArray = avoidanceGroupLevelJsonArray[i];
            log.debug("avoidanceGroupLevelJsonArray[" + i + "]：" + avoidanceGroupJsonArray);
            for (int j = 0; j < avoidanceGroupJsonArray.size(); j++) {
                try {
                    Map avoidanceGroupMap = avoidanceGroupJsonArray.getJSONObject(j);
                    if (avoidanceGroupDao.getAvoidanceGroupSumList(avoidanceGroupMap).size() == 0) {
                        avoidanceGroupDao.insertAvoidanceGroupSum(avoidanceGroupMap);
                    } else {
                        avoidanceGroupMap.put("updateType", "1");
                        avoidanceGroupDao.updateAvoidanceGroupSum(avoidanceGroupMap);
                    }
                } catch (Exception e) {
                    log.error("记录预处理群障树信息异常", e);
                }
            }
        }

        // 获取待关联为投诉单或者抱怨单的列表，并更新到树中
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        List avoidanceGroupNotRelaList = avoidanceGroupDao.getAvoidanceGroupNotRelaList(param);
        log.debug("avoidanceGroupNotRelaList：" + avoidanceGroupNotRelaList);
        for (int i = 0; i < avoidanceGroupNotRelaList.size(); i++) {
            Map avoidanceGroupNotRelaMap = (Map) avoidanceGroupNotRelaList.get(i);
            updateAvoidanceGroupSumInfo(avoidanceGroupNotRelaMap, sdf, nowDate);
        }

        log.info("PretreatmentAvoidanceGroup出口……");
    }

    /**
     * 将待处理的预处理群障信息拼装成为json
     * 本方法只构造树的各级节点，节点投诉单数量，抱怨单数量默认都是0
     *
     * @param avoidanceGroupNotSumMap
     */
    private void setAvoidanceGroupJson(Map avoidanceGroupNotSumMap) {
        HashMap nodeInfoMap = new HashMap();
        // 群障ID
        String batchId = MapUtils.getString(avoidanceGroupNotSumMap, "BATCH_ID");
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
                nodeNameFull += MapUtils.getString(avoidanceGroupNotSumMap, parentLevelName) == null || "".equals(MapUtils.getString(avoidanceGroupNotSumMap, parentLevelName)) ? "其它" : MapUtils.getString(avoidanceGroupNotSumMap, parentLevelName);
            }
            // 使用当前节点的完全地址生成MD5，用于快速搜索，使用MD5加密是为了防止地址长度太长，加上批次号是为了批次唯一性
            String md5 = MD5Util.MD5Encode(nodeNameFull + batchId);

            JSONObject avoidanceGroupJson = new JSONObject();
            // 用户第i+1级的标准地址映射名
            String addressLevelName = MapUtils.getString(nodeLevelNameMap, (i + 1) + "");
            // 用户第i+1级的标准地址，若地址为空，则用“其它”来代替
            String address = MapUtils.getString(avoidanceGroupNotSumMap, addressLevelName) == null || "".equals(MapUtils.getString(avoidanceGroupNotSumMap, addressLevelName)) ? "其它" : MapUtils.getString(avoidanceGroupNotSumMap, addressLevelName);
            // 如果存在节点索引，则从当前保存的节点列表中获取节点，用户获取后续信息
            if (nodeIndexMap.containsKey(md5)) {
                for (int j = 0; j < avoidanceGroupLevelJsonArray[i].size(); j++) {
                    // 对节点的md5值进行校验
                    if (md5.equals(avoidanceGroupLevelJsonArray[i].getJSONObject(j).get("md5"))) {
                        avoidanceGroupJson = avoidanceGroupLevelJsonArray[i].getJSONObject(j);
                        // 记录影响号码数量
                        avoidanceGroupJson.put("numberCount", avoidanceGroupJson.getIntValue("numberCount") + 1);
                        break;
                    }
                }
            } else {
                // 不存在节点索引信息，新建节点索引信息，且新建节点json
                String nodeId = avoidanceGroupDao.getAvoidanceGroupLogSeq();
                String parentId = "";
                if (i > 0) {
                    parentId = MapUtils.getString(nodeInfoMap, (i - 1) + "");
                }
                nodeIndexMap.put(md5, nodeNameFull);
                avoidanceGroupJson.put("id", nodeId);
                avoidanceGroupJson.put("batchId", batchId);
                avoidanceGroupJson.put("parentId", parentId);
                avoidanceGroupJson.put("nodeLevel", i);
                avoidanceGroupJson.put("nodeName", address);
                avoidanceGroupJson.put("md5", md5);
                avoidanceGroupJson.put("complainCount", complainCount);
                avoidanceGroupJson.put("summaryCount", summaryCount);
                avoidanceGroupJson.put("numberCount", numberCount);
                avoidanceGroupLevelJsonArray[i].add(avoidanceGroupJson);
            }
            nodeInfoMap.put(i + "", avoidanceGroupJson.getString("id"));
        }
    }

    /**
     * 更新用户群障信息
     *
     * @param avoidanceGroupNotRelaMap
     * @param sdf
     * @param nowDate
     */
    private void updateAvoidanceGroupSumInfo(Map avoidanceGroupNotRelaMap, SimpleDateFormat sdf, Date nowDate) {
        // 投诉单数量
        int complainCount = 0;
        // 抱怨单数量
        int summaryCount = 0;
        String state = "0";
        String batchId = MapUtils.getString(avoidanceGroupNotRelaMap, "BATCH_ID");
        String accnbr = MapUtils.getString(avoidanceGroupNotRelaMap, "ACC_NBR");
        // 投诉单立单时间
        String eomsCreateDate = MapUtils.getString(avoidanceGroupNotRelaMap, "EOMS_CREATE_DATE");
        // 号码入库时间
        String createDate = MapUtils.getString(avoidanceGroupNotRelaMap, "CREATE_DATE");
        // 号码查询大面积故障时间
        String qryDate = MapUtils.getString(avoidanceGroupNotRelaMap, "QRY_DATE");

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
                Map avoidanceGroupNumInfo = avoidanceGroupDao.getAvoidanceGroupNumInfo(param);
                log.debug("用户地址信息：" + avoidanceGroupNumInfo);
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
                        nodeNameFull += MapUtils.getString(avoidanceGroupNumInfo, parentLevelName) == null || "".equals(MapUtils.getString(avoidanceGroupNumInfo, parentLevelName)) ? "其它" : MapUtils.getString(avoidanceGroupNumInfo, parentLevelName);
                    }
                    // 使用当前节点的完全地址生成MD5，用于快速搜索，使用MD5加密是为了防止地址长度太长
                    String md5 = MD5Util.MD5Encode(nodeNameFull + batchId);

                    param.put("complainCount", complainCount);
                    param.put("summaryCount", summaryCount);
                    param.put("batchId", batchId);
                    param.put("md5", md5);
                    avoidanceGroupDao.updateAvoidanceGroupSum(param);
                }
                param.put("state", state);
                avoidanceGroupDao.updateAvoidanceGroupLog(param);
            }
        } catch (Exception e) {
            log.error("更新用户群障信息异常", e);
        }
    }
}
