package com.ztesoft.iom.manage.develop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.manage.develop.dao.DevelopOrderDao;
import com.ztesoft.iom.manage.develop.dao.impl.DevelopOrderDaoImpl;
import com.ztesoft.iom.manage.flow.service.FlowEngine;
import com.ztesoft.iom.manage.param.service.ParamMappingService;
import com.ztesoft.iom.manage.workOrder.dao.WorkOrderDao;
import com.ztesoft.iom.manage.workOrder.dao.impl.WorkOrderDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @Description: 研发任务单管理类
 * @author: huang.jing
 * @Date: 2018/1/13 0013 - 18:47
 */
public class DevelopOrderManager {

    private static Logger log = LogManager.getLogger(DevelopOrderManager.class);
    private DevelopOrderDao developOrderDao;
    private WorkOrderDao workOrderDao;

    /**
     * 创建研发任务单
     *
     * @param createJson
     * @return
     */
    public boolean createOrder(JSONObject createJson) {
        log.info("createOrder入口……");
        boolean createFlag = false;
        try {
            // 后续需要根据入参从数据库读取模板
            JSONObject flowTemplate = getFlowTemplate();
            FlowEngine flowEngine = (FlowEngine) BeanFactory.getApplicationContext().getBean("flowEngine");
            // 创建流程实例
            JSONObject flowInstanceJson = flowEngine.createFlowInstance(flowTemplate);
            if (flowInstanceJson.containsKey("flowInstanceId")) {
                developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
                workOrderDao = (WorkOrderDaoImpl) BeanFactory.getApplicationContext().getBean("workOrderDaoImpl");
                // 获取序列
                Map qryMap = new HashMap();
                qryMap.put("seqName", "ot_develop_order_seq");
                String developOrderId = developOrderDao.getDevelopModuleSeq(qryMap);

                // 插入研发任务管理单表
                Map insertMap = createJson.getInnerMap();
                insertMap.put("developOrderId", developOrderId);
                insertMap.put("flowInstanceId", flowInstanceJson.getString("flowInstanceId"));
                insertMap.put("state", "10N");
                developOrderDao.insertDevelopOrder(insertMap);

                // 开始执行流程
                JSONObject workitemJson = flowEngine.startFlowInstance(flowTemplate, flowInstanceJson);
                // 生成下一环节工单
                JSONObject orderJson = new JSONObject();
                orderJson.put("developOrderId", developOrderId);
                orderJson.put("author", createJson.getString("author"));
                createFlag = createNextWorkOrder(orderJson, workitemJson);
            } else {
                log.error("创建研发任务单失败，获得的流程实例没有[flowInstanceId]属性……");
            }
        } catch (Exception e) {
            log.error("创建研发任务单异常", e);
        }
        log.info("createOrder出口……");
        return createFlag;
    }

    /**
     * 查询研发任务单列表
     *
     * @param qryJson
     * @return
     */
    public JSONObject getDevelopOrderList(JSONObject qryJson) {
        log.info("getDevelopOrderList入口……");
        JSONObject orderInfoObject = new JSONObject();
        List orderList = new ArrayList();
        int orderListCount = 0;
        try {
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            ParamMappingService paramMappingService = (ParamMappingService) BeanFactory.getApplicationContext().getBean("paramMappingService");

            int startIndex = ((qryJson.getIntValue("page") - 1) * qryJson.getIntValue("limit")) + 1;
            int endIndex = (qryJson.getIntValue("page") * qryJson.getIntValue("limit"));
            Map qryMap = new HashMap();
            // 后续考虑根据权限是否只能查自己创建的任务单
            qryMap.put("author", qryJson.getString("author"));
            qryMap.put("developOrderId", qryJson.getString("developOrderId"));
            qryMap.put("orderTitle", qryJson.getString("orderTitle"));
            qryMap.put("zmpId", qryJson.getString("zmpId"));
            qryMap.put("orderState", qryJson.getString("orderState"));
            qryMap.put("version", qryJson.getString("version"));
            qryMap.put("orderType", qryJson.getString("orderType"));
            qryMap.put("tacheId", qryJson.getString("tacheId"));
            qryMap.put("startIndex", startIndex + "");
            qryMap.put("endIndex", endIndex + "");
            orderList = developOrderDao.getDevelopOrderList(qryMap);
            // 对列表的参数值进行映射
            for (Iterator iterator = orderList.iterator(); iterator.hasNext(); ) {
                Map orderMap = (Map) iterator.next();
                orderMap.put("STATE_NAME", paramMappingService.getMappingValue("OS", MapUtils.getString(orderMap, "STATE")));
                orderMap.put("WORK_ORDER_STATE_NAME", paramMappingService.getMappingValue("OS", MapUtils.getString(orderMap, "WORK_ORDER_STATE")));
                orderMap.put("WORK_ORDER_TACHE_NAME", paramMappingService.getMappingValue("OTA", MapUtils.getString(orderMap, "WORK_ORDER_TACHE_ID")));
            }
            orderListCount = developOrderDao.getDevelopOrderListCount(qryMap);
        } catch (Exception e) {
            log.error("查询研发任务单列表异常", e);
        } finally {
            orderInfoObject.put("orderList", orderList);
            orderInfoObject.put("orderListCount", orderListCount);
        }
        log.info("getDevelopOrderList出口……");
        return orderInfoObject;
    }

    /**
     * 办结当前工单，进入下一环节
     *
     * @param finishJson
     * @return
     */
    public boolean finishWorkOrder(JSONObject finishJson) {
        log.info("finishWorkOrder入口……");
        boolean finishFlag = false;

        try {
            boolean checkFlag = false;
            // 研发任务单ID
            String developOrderId = finishJson.getString("developOrderId");
            String workOrderId = finishJson.getString("workOrderId");
            String orderType = finishJson.getString("orderType");
            // 后续需要根据入参从数据库读取模板
            JSONObject flowTemplate = getFlowTemplate();
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            workOrderDao = (WorkOrderDaoImpl) BeanFactory.getApplicationContext().getBean("workOrderDaoImpl");
            // 获取当前正在执行中的环节信息
            Map qryMap = new HashMap();
            qryMap.put("workOrderId", workOrderId);
            qryMap.put("orderType", orderType);
            Map processingWorkOrderMap = workOrderDao.getProcessingWorkOrderMap(qryMap);
            JSONArray activityList = flowTemplate.getJSONArray("activityList");
            // 根据当前执行中的环节id在模板中寻找，只有module=develop时才能回单
            for (Iterator iterator = activityList.iterator(); iterator.hasNext(); ) {
                JSONObject activityJson = (JSONObject) iterator.next();
                if (MapUtils.getString(processingWorkOrderMap, "TACHE_ID").equals(activityJson.getString("tacheId")) && orderType.equals(activityJson.getString("module"))) {
                    checkFlag = true;
                }
            }

            if (checkFlag) {
                // 将当前工单改为10F状态
                Map updateMap = new HashMap();
                updateMap.put("workOrderId", MapUtils.getString(processingWorkOrderMap, "WORK_ORDER_ID", ""));
                updateMap.put("state", "10F");
                updateMap.put("author", finishJson.getString("author"));
                workOrderDao.updateWorkOrder(updateMap);

                // 记录回单操作
                Map insertMap = new HashMap();
                qryMap.clear();
                qryMap.put("seqName", "ot_order_oper_log_seq");
                String operLogId = developOrderDao.getDevelopModuleSeq(qryMap);
                insertMap.put("operLogId", operLogId);
                insertMap.put("orderId", developOrderId);
                insertMap.put("workOrderId", MapUtils.getString(processingWorkOrderMap, "WORK_ORDER_ID", ""));

                insertMap.put("oper", finishJson.getString("author"));
                insertMap.put("operType", "finishWorkOrder");
                insertMap.put("remark", finishJson.getString("remark"));
                workOrderDao.insertOrderOperLog(insertMap);

                FlowEngine flowEngine = (FlowEngine) BeanFactory.getApplicationContext().getBean("flowEngine");
                // 完成当前工作项
                JSONObject completeWorkitemJson = new JSONObject();
                completeWorkitemJson.put("workitemId", MapUtils.getString(processingWorkOrderMap, "WORKITEM_ID", ""));
                JSONObject workitemJson = flowEngine.completeWorkitem(flowTemplate, completeWorkitemJson);
                // 生成下一环节工单
                JSONObject orderJson = new JSONObject();
                orderJson.put("developOrderId", developOrderId);

                // 如果前端入口没有传入nextWorkOrderOper，则使用当前工单的收单人为下一环节工单的收单人
                String nextWorkOrderOper = finishJson.getString("nextWorkOrderOper");
                if (nextWorkOrderOper == null || "".equals(nextWorkOrderOper)) {
                    nextWorkOrderOper = MapUtils.getString(processingWorkOrderMap, "RECIEVE_OPER", "");
                }
                orderJson.put("author", nextWorkOrderOper);
                finishFlag = createNextWorkOrder(orderJson, workitemJson);
            } else {
                log.error("当前环节不能进行回单：" + processingWorkOrderMap);
            }
        } catch (Exception e) {
            log.error("回单异常", e);
        }

        log.info("finishWorkOrder出口……");
        return finishFlag;
    }

    /**
     * 回退当前工单，进入下一环节
     *
     * @param finishJson
     * @return
     */
    public boolean returnWorkOrder(JSONObject finishJson) {
        log.info("returnWorkOrder入口……");
        boolean finishFlag = false;

        try {
            boolean checkFlag = false;
            // 研发任务单ID
            String developOrderId = finishJson.getString("developOrderId");
            String workOrderId = finishJson.getString("workOrderId");
            String orderType = finishJson.getString("orderType");
            // 后续需要根据入参从数据库读取模板
            JSONObject flowTemplate = getFlowTemplate();
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            workOrderDao = (WorkOrderDaoImpl) BeanFactory.getApplicationContext().getBean("workOrderDaoImpl");
            // 获取当前正在执行中的环节信息
            Map qryMap = new HashMap();
            qryMap.put("workOrderId", workOrderId);
            qryMap.put("orderType", orderType);
            Map processingWorkOrderMap = workOrderDao.getProcessingWorkOrderMap(qryMap);
            JSONArray activityList = flowTemplate.getJSONArray("activityList");
            // 根据当前执行中的环节id在模板中寻找，只有module=develop时才能回单
            for (Iterator iterator = activityList.iterator(); iterator.hasNext(); ) {
                JSONObject activityJson = (JSONObject) iterator.next();
                if (MapUtils.getString(processingWorkOrderMap, "TACHE_ID").equals(activityJson.getString("tacheId")) && orderType.equals(activityJson.getString("module"))) {
                    checkFlag = true;
                }
            }

            if (checkFlag) {
                // 将当前工单改为10R状态
                Map updateMap = new HashMap();
                updateMap.put("workOrderId", MapUtils.getString(processingWorkOrderMap, "WORK_ORDER_ID", ""));
                updateMap.put("state", "10R");
                updateMap.put("author", finishJson.getString("author"));
                workOrderDao.updateWorkOrder(updateMap);

                // 记录回单操作
                Map insertMap = new HashMap();
                qryMap.clear();
                qryMap.put("seqName", "ot_order_oper_log_seq");
                String operLogId = developOrderDao.getDevelopModuleSeq(qryMap);
                insertMap.put("operLogId", operLogId);
                insertMap.put("orderId", developOrderId);
                insertMap.put("workOrderId", MapUtils.getString(processingWorkOrderMap, "WORK_ORDER_ID", ""));
                insertMap.put("oper", finishJson.getString("author"));
                insertMap.put("operType", "returnWorkOrder");
                insertMap.put("remark", finishJson.getString("remark"));
                workOrderDao.insertOrderOperLog(insertMap);

                FlowEngine flowEngine = (FlowEngine) BeanFactory.getApplicationContext().getBean("flowEngine");
                // 完成当前工作项
                JSONObject completeWorkitemJson = new JSONObject();
                completeWorkitemJson.put("workitemId", MapUtils.getString(processingWorkOrderMap, "WORKITEM_ID", ""));
                JSONObject workitemJson = flowEngine.disableWorkitem(flowTemplate, completeWorkitemJson);
                // 生成下一环节工单
                JSONObject orderJson = new JSONObject();
                orderJson.put("developOrderId", developOrderId);
                // 如果前端入口没有传入nextWorkOrderOper，则使用当前工单的收单人为下一环节工单的收单人
                String nextWorkOrderOper = finishJson.getString("nextWorkOrderOper");
                if (nextWorkOrderOper == null || "".equals(nextWorkOrderOper)) {
                    nextWorkOrderOper = MapUtils.getString(processingWorkOrderMap, "RECIEVE_OPER", "");
                }
                orderJson.put("author", nextWorkOrderOper);
                finishFlag = createNextWorkOrder(orderJson, workitemJson);
            } else {
                log.error("当前环节不能进行退单：" + processingWorkOrderMap);
            }
        } catch (Exception e) {
            log.error("回单异常", e);
        }

        log.info("returnWorkOrder出口……");
        return finishFlag;
    }

    /**
     * 查询SVN日志及变更文件列表
     *
     * @param qryJson
     * @return
     */
    public JSONObject getSvnLogWithChangeList(JSONObject qryJson) {
        log.info("getSvnLogWithChangeList入口……");
        JSONObject svnLogObject = new JSONObject();
        List svnLogList = new ArrayList();
        int svnLogListCount = 0;
        try {
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            ParamMappingService paramMappingService = (ParamMappingService) BeanFactory.getApplicationContext().getBean("paramMappingService");
            int startIndex = ((qryJson.getIntValue("page") - 1) * qryJson.getIntValue("limit")) + 1;
            int endIndex = (qryJson.getIntValue("page") * qryJson.getIntValue("limit"));
            Map qryMap = new HashMap();
            qryMap.put("author", qryJson.getString("author"));
            qryMap.put("startIndex", startIndex + "");
            qryMap.put("endIndex", endIndex + "");
            qryMap.put("isRelaOrder", qryJson.getString("isRelaOrder"));
            qryMap.put("reversion", qryJson.getString("reversion"));
            svnLogList = developOrderDao.getSvnLogList(qryMap);
            svnLogListCount = developOrderDao.getSvnLogListCount(qryMap);

            List svnLogChangeListAll = developOrderDao.getSvnLogChangeList(qryMap);

            // 对列表的参数值进行映射
            for (Iterator iterator = svnLogList.iterator(); iterator.hasNext(); ) {
                Map svnLogMap = (Map) iterator.next();
                String svnLogId = MapUtils.getString(svnLogMap, "SVN_LOG_ID", "");
                List svnLogChangeList = new ArrayList();

                for (Iterator iteratorC = svnLogChangeListAll.iterator(); iteratorC.hasNext(); ) {
                    Map svnLogChangeMap = (Map) iteratorC.next();
                    if (svnLogId.equals(MapUtils.getString(svnLogChangeMap, "SVN_LOG_ID", ""))) {
                        svnLogChangeMap.put("TYPE_NAME", paramMappingService.getMappingValue("SCT", MapUtils.getString(svnLogChangeMap, "TYPE")));
                        svnLogChangeMap.put("PROJECT_NAME", paramMappingService.getMappingValue("PT", MapUtils.getString(svnLogChangeMap, "PROJECT")));
                        svnLogChangeList.add(svnLogChangeMap);
                    }
                }
                svnLogMap.put("SVN_LOG_CHANGE_LIST", svnLogChangeList);
            }
        } catch (Exception e) {
            log.error("查询SVN日志及变更文件列表异常", e);
        } finally {
            svnLogObject.put("svnLogList", svnLogList);
            svnLogObject.put("svnLogListCount", svnLogListCount);
        }
        log.info("getSvnLogWithChangeList出口……");
        return svnLogObject;
    }


    /**
     * 提交代码
     *
     * @param attachJson
     * @return
     */
    public boolean attachSVN(JSONObject attachJson) {
        log.info("attachSVN出口……");
        boolean attachFlag = false;
        try {
            // 如果关联的svn记录不为空，循环插入关联信息
            List svnLogIdList = attachJson.getObject("svnLogIdList", List.class);
            if (svnLogIdList.size() > 0) {
                developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
                for (Iterator iterator = svnLogIdList.iterator(); iterator.hasNext(); ) {
                    String svnLogId = (String) iterator.next();
                    Map qryMap = new HashMap();
                    qryMap.put("seqName", "ot_develop_order_svn_rela_seq");
                    String relaId = developOrderDao.getDevelopModuleSeq(qryMap);

                    Map insertMap = new HashMap();
                    insertMap.put("relaId", relaId);
                    insertMap.put("svnLogId", svnLogId);
                    insertMap.put("developOrderId", attachJson.getString("developOrderId"));
                    insertMap.put("workOrderId", attachJson.getString("workOrderId"));
                    developOrderDao.insertDevelopSvnRela(insertMap);
                }
            }
            // 回单
            attachFlag = finishWorkOrder(attachJson);
        } catch (Exception e) {
            log.error("提交代码异常", e);
        }

        log.info("attachSVN出口……");
        return attachFlag;
    }

    /**
     * 转派工单
     *
     * @param transferJson
     * @return
     */
    public JSONObject transferWorkOrder(JSONObject transferJson) {
        log.info("transferWorkOrder入口……");
        JSONObject returnJson = new JSONObject();
        int transferCount = 0;
        try {
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            workOrderDao = (WorkOrderDaoImpl) BeanFactory.getApplicationContext().getBean("workOrderDaoImpl");
            JSONArray workOrderList = transferJson.getJSONArray("workOrderList");
            String recieveOper = transferJson.getString("recieveOper");
            String remark = transferJson.getString("remark");
            // 支撑批量转派
            for (Iterator iterator = workOrderList.iterator(); iterator.hasNext(); ) {
                JSONObject workOrderJson = (JSONObject) iterator.next();
                String workOrderId = workOrderJson.getString("workOrderId");
                String orderType = workOrderJson.getString("orderType");
                String developOrderId = workOrderJson.getString("developOrderId");
                // 获取当前正在执行中的环节信息
                Map qryMap = new HashMap();
                qryMap.put("workOrderId", workOrderId);
                qryMap.put("orderType", orderType);
                Map processingWorkOrderMap = workOrderDao.getProcessingWorkOrderMap(qryMap);
                if (processingWorkOrderMap.isEmpty()) {
                    log.error("根据工单号[" + workOrderId + "]查询不到正在进行中的工单，不能转派……");
                    continue;
                }

                String remarkLog = "将工单从[" + MapUtils.getString(processingWorkOrderMap, "RECIEVE_OPER", "") + "]转派给[" + recieveOper + "]";
                remarkLog += "。转派原因为[" + remark + "]";

                // 修改工单收单人
                Map updateMap = new HashMap();
                updateMap.put("workOrderId", workOrderId);
                updateMap.put("recieveOper", recieveOper);
                transferCount += workOrderDao.updateWorkOrder(updateMap);

                // 转派回单操作
                Map insertMap = new HashMap();
                qryMap.clear();
                qryMap.put("seqName", "ot_order_oper_log_seq");
                String operLogId = developOrderDao.getDevelopModuleSeq(qryMap);
                insertMap.put("operLogId", operLogId);
                insertMap.put("orderId", developOrderId);
                insertMap.put("workOrderId", workOrderId);
                insertMap.put("oper", transferJson.getString("author"));
                insertMap.put("operType", "returnWorkOrder");
                insertMap.put("remark", remarkLog);
                workOrderDao.insertOrderOperLog(insertMap);
            }
        } catch (Exception e) {
            log.error("转派工单异常", e);
        } finally {
            returnJson.put("transferCount", transferCount);
        }
        log.info("transferWorkOrder出口……");
        return returnJson;
    }

    /**
     * 查询任务单已关联的SVN日志及变更列表
     * @param qryJson
     * @return
     */
    public JSONObject getSvnLogWithChangeRelaList(JSONObject qryJson) {
        log.info("getSvnLogWithChangeRelaList入口……");
        JSONObject svnLogObject = new JSONObject();
        List svnLogRelaList = new ArrayList();
        try {
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            ParamMappingService paramMappingService = (ParamMappingService) BeanFactory.getApplicationContext().getBean("paramMappingService");
            Map qryMap = new HashMap();
            qryMap.put("developOrderList", qryJson.getObject("developOrderList", List.class));
            svnLogRelaList = developOrderDao.getSvnLogRelaList(qryMap);

            List svnLogChangeRelaListAll = developOrderDao.getSvnLogRelaChangeList(qryMap);

            // 对列表的参数值进行映射
            for (Iterator iterator = svnLogRelaList.iterator(); iterator.hasNext(); ) {
                Map svnLogMap = (Map) iterator.next();
                String svnLogId = MapUtils.getString(svnLogMap, "SVN_LOG_ID", "");
                List svnLogChangeList = new ArrayList();

                for (Iterator iteratorC = svnLogChangeRelaListAll.iterator(); iteratorC.hasNext(); ) {
                    Map svnLogChangeMap = (Map) iteratorC.next();
                    if (svnLogId.equals(MapUtils.getString(svnLogChangeMap, "SVN_LOG_ID", ""))) {
                        svnLogChangeMap.put("TYPE_NAME", paramMappingService.getMappingValue("SCT", MapUtils.getString(svnLogChangeMap, "TYPE")));
                        svnLogChangeMap.put("PROJECT_NAME", paramMappingService.getMappingValue("PT", MapUtils.getString(svnLogChangeMap, "PROJECT")));
                        svnLogChangeList.add(svnLogChangeMap);
                    }
                }
                svnLogMap.put("SVN_LOG_CHANGE_LIST", svnLogChangeList);
            }
        } catch (Exception e) {
            log.error("查询任务单已关联的SVN日志及变更列表异常", e);
        } finally {
            svnLogObject.put("svnLogRelaList", svnLogRelaList);
        }
        log.info("getSvnLogWithChangeRelaList出口……");
        return svnLogObject;
    }

    /**
     * 获取版本计划
     * @param qryJson
     * @return
     */
    public JSONObject getVersionList(JSONObject qryJson) {
        log.info("getVersionList入口……");
        JSONObject versionObject = new JSONObject();
        try {
            developOrderDao = (DevelopOrderDaoImpl) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            Map qryMap = new HashMap();
            List versionList = developOrderDao.getVersionList(qryMap);
            for (Iterator iterator = versionList.iterator(); iterator.hasNext(); ) {
                Map versionMap = (Map) iterator.next();
                versionObject.put(MapUtils.getString(versionMap, "VERSION_DATE"), "版本");
            }
        } catch (Exception e) {
            log.error("获取版本计划异常", e);
        }
        log.info("getVersionList出口……");
        return versionObject;
    }

    /**
     * 读取模板信息，后续需要从数据库读取模板
     *
     * @return
     */
    private JSONObject getFlowTemplate() {
        return JSONObject.parseObject(ParamConfig.getInstance().getParamValue("developFlowTemplate"));
    }

    /**
     * 创建下一个工单或者任务单竣工
     *
     * @param orderJson
     * @param workitemJson
     * @return
     */
    private boolean createNextWorkOrder(JSONObject orderJson, JSONObject workitemJson) {
        boolean createFlag = false;
        try {
            // 任务单号
            String developOrderId = orderJson.getString("developOrderId");
            // 处理人
            String oper = orderJson.getString("author");

            if ("yes".equals(workitemJson.getString("isEnd"))) {
                // 没有需要执行的工作项
                createFlag = true;
                // 将任务单更新为竣工状态
                Map updateMap = new HashMap();
                updateMap.put("developOrderId", developOrderId);
                updateMap.put("state", "10F");
                developOrderDao.updateDevelopOrder(updateMap);
            } else if ("no".equals(workitemJson.getString("isEnd"))) {
                // 有需要执行的工作项
                createFlag = true;

                Map qryMap = new HashMap();
                qryMap.put("seqName", "ot_work_order_seq");
                String workOrderId = developOrderDao.getDevelopModuleSeq(qryMap);
                // 创建工单信息
                Map insertMap = new HashMap();
                insertMap.put("workOrderId", workOrderId);
                insertMap.put("orderId", developOrderId);
                insertMap.put("workitemId", workitemJson.getString("workitemId"));
                insertMap.put("orderType", workitemJson.getString("module"));
                insertMap.put("tacheId", workitemJson.getString("tacheId"));
                insertMap.put("state", "10N");
                insertMap.put("recieveOper", oper);
                workOrderDao.insertWorkOrder(insertMap);
            } else if ("cancel".equals(workitemJson.getString("isEnd"))) {
                // 没有需要执行的工作项
                createFlag = true;
                // 将任务单更新为已撤单
                Map updateMap = new HashMap();
                updateMap.put("developOrderId", developOrderId);
                updateMap.put("state", "10C");
                developOrderDao.updateDevelopOrder(updateMap);
            } else {
                log.error("创建研发任务单失败，获得的工作项没有[isEnd]属性……");
            }
        } catch (Exception e) {
            log.error("创建下一个工单或者任务单竣工异常", e);
        }
        return createFlag;
    }
}
