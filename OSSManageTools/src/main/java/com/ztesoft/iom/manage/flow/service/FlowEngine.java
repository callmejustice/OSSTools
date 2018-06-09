package com.ztesoft.iom.manage.flow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.flow.dao.FlowEngineDao;
import com.ztesoft.iom.manage.flow.dao.impl.FlowEngineDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * 流程引擎业务类，将数据库操作dao定义为全局对象，但是只在共有的方法中通过工厂类获取数据库操作dao，私有类中不获取dao
 * @author: huang.jing
 * @Date: 2018/1/14 0014 - 14:20
 */
public class FlowEngine {

    private static Logger log = LogManager.getLogger(FlowEngine.class);
    private FlowEngineDao flowEngineDaoImpl;

    /**
     * 创建流程实例并返回流程实例信息
     *
     * @param flowTemplate
     * @return
     */
    public JSONObject createFlowInstance(JSONObject flowTemplate) {
        JSONObject flowInstanceJson = new JSONObject();

        try {
            if (checkTemplateIsAvailable(flowTemplate)) {
                flowEngineDaoImpl = (FlowEngineDaoImpl) BeanFactory.getApplicationContext().getBean("flowEngineDaoImpl");
                Map qryMap = new HashMap();
                qryMap.put("seqName", "ot_flow_instance_seq");
                String seq = flowEngineDaoImpl.getFlowEngineSeq(qryMap);
                Map insertMap = new HashMap();
                insertMap.put("flowInstanceId", seq);
                insertMap.put("flowDefineId", flowTemplate.getString("flowDefineId"));
                insertMap.put("flowDefineName", flowTemplate.getString("name"));
                insertMap.put("version", flowTemplate.getString("version"));
                insertMap.put("type", flowTemplate.getString("type"));
                insertMap.put("state", "10I");
                flowEngineDaoImpl.insertFlowInstance(insertMap);
                flowInstanceJson.put("flowInstanceId", seq);
                flowInstanceJson.put("type", flowTemplate.getString("type"));
            }
        } catch (Exception e) {
            log.error("创建流程实例并返回流程实例信息异常", e);
        }

        return flowInstanceJson;
    }

    /**
     * 启动流程并返回第一个工作项
     *
     * @param flowTemplate
     * @param flowInstanceJson
     * @return
     */
    public JSONObject startFlowInstance(JSONObject flowTemplate, JSONObject flowInstanceJson) {
        JSONObject workitemJson = new JSONObject();

        try {
            if (checkTemplateIsAvailable(flowTemplate)) {
                flowEngineDaoImpl = (FlowEngineDaoImpl) BeanFactory.getApplicationContext().getBean("flowEngineDaoImpl");

                // 流程开始时，先将流程实例的状态更新为10N
                Map updateMap = new HashMap();
                updateMap.put("flowInstanceId", flowInstanceJson.getString("flowInstanceId"));
                updateMap.put("state", "10N");
                flowEngineDaoImpl.updateFlowInstance(updateMap);

                // 开始执行流程时，只需设置流程实例id，即可从第一个活动节点开始走流程
                JSONObject fromActivityInstanceJson = new JSONObject();
                fromActivityInstanceJson.put("flowInstanceId", flowInstanceJson.getString("flowInstanceId"));
                fromActivityInstanceJson.put("activityDefineId", "");
                workitemJson = getNextWorkitem(flowTemplate, fromActivityInstanceJson);
            }
        } catch (Exception e) {
            log.error("启动流程并返回第一个工作项异常", e);
        }

        return workitemJson;
    }

    /**
     * 完成工作项，执行下一个工作项
     * 用于流程正向执行
     * @param workitemJson
     * @return
     */
    public JSONObject completeWorkitem(JSONObject flowTemplate, JSONObject workitemJson) {
        JSONObject completeJson = new JSONObject();
        try {
            if (checkTemplateIsAvailable(flowTemplate)) {
                flowEngineDaoImpl = (FlowEngineDaoImpl) BeanFactory.getApplicationContext().getBean("flowEngineDaoImpl");
                // 查询当前活动实例和工作项的信息
                Map qryMap = new HashMap();
                qryMap.put("workitemId", workitemJson.getString("workitemId"));
                Map ActivityInstanceMap = flowEngineDaoImpl.getWokritemAndActivityInstanceMap(qryMap);

                Map updateMap = new HashMap();
                int updateCount = 0;
                // 完成当前工作项
                if("10N".equals(MapUtils.getString(ActivityInstanceMap, "WORKITEM_STATE", ""))) {
                    updateMap.clear();
                    updateMap.put("workitemId", MapUtils.getString(ActivityInstanceMap, "WORKITEM_ID", ""));
                    updateMap.put("state", "10F");
                    updateCount += flowEngineDaoImpl.updateWorkitem(updateMap);
                }

                // 完成当前活动实例
                if("10N".equals(MapUtils.getString(ActivityInstanceMap, "ACTIVITY_STATE", ""))) {
                    updateMap.clear();
                    updateMap.put("activityInstanceId", MapUtils.getString(ActivityInstanceMap, "ACTIVITY_INSTANCE_ID", ""));
                    updateMap.put("state", "10F");
                    updateCount += flowEngineDaoImpl.updateActivityInstance(updateMap);
                }

                if(updateCount == 2) {
                    // 获取下一个工作项
                    JSONObject fromActivityInstanceJson = new JSONObject();
                    fromActivityInstanceJson.put("flowInstanceId", MapUtils.getString(ActivityInstanceMap, "FLOW_INSTANCE_ID", ""));
                    fromActivityInstanceJson.put("activityDefineId", MapUtils.getString(ActivityInstanceMap, "ACTIVITY_DEFINE_ID", ""));
                    completeJson = getNextWorkitem(flowTemplate, fromActivityInstanceJson);
                } else {
                    log.error("活动实例状态或者工作项状态错误，不允许完成工作项……");
                }
            }
        } catch (Exception e) {
            log.error("完成工作项，执行下一个工作项异常", e);
        }
        return completeJson;
    }

    /**
     * 作废工作项，执行下一个工作项
     * 用于流程反向执行
     * @param workitemJson
     * @return
     */
    public JSONObject disableWorkitem(JSONObject flowTemplate, JSONObject workitemJson) {
        JSONObject completeJson = new JSONObject();
        try {
            if (checkTemplateIsAvailable(flowTemplate)) {
                flowEngineDaoImpl = (FlowEngineDaoImpl) BeanFactory.getApplicationContext().getBean("flowEngineDaoImpl");
                // 查询当前活动实例和工作项的信息
                Map qryMap = new HashMap();
                qryMap.put("workitemId", workitemJson.getString("workitemId"));
                Map ActivityInstanceMap = flowEngineDaoImpl.getWokritemAndActivityInstanceMap(qryMap);

                Map updateMap = new HashMap();
                int updateCount = 0;
                // 将当前工作项标记为已回退
                if("10N".equals(MapUtils.getString(ActivityInstanceMap, "WORKITEM_STATE", ""))) {
                    updateMap.clear();
                    updateMap.put("workitemId", MapUtils.getString(ActivityInstanceMap, "WORKITEM_ID", ""));
                    updateMap.put("state", "10R");
                    updateCount += flowEngineDaoImpl.updateWorkitem(updateMap);
                }

                // 将当前活动项标记为已回退
                if("10N".equals(MapUtils.getString(ActivityInstanceMap, "ACTIVITY_STATE", ""))) {
                    updateMap.clear();
                    updateMap.put("activityInstanceId", MapUtils.getString(ActivityInstanceMap, "ACTIVITY_INSTANCE_ID", ""));
                    updateMap.put("state", "10R");
                    updateCount += flowEngineDaoImpl.updateActivityInstance(updateMap);
                }

                if(updateCount == 2) {
                    // 获取下一个工作项
                    JSONObject fromActivityInstanceJson = new JSONObject();
                    fromActivityInstanceJson.put("flowInstanceId", MapUtils.getString(ActivityInstanceMap, "FLOW_INSTANCE_ID", ""));
                    fromActivityInstanceJson.put("activityDefineId", MapUtils.getString(ActivityInstanceMap, "ACTIVITY_DEFINE_ID", ""));
                    completeJson = getPrevWorkitem(flowTemplate, fromActivityInstanceJson);
                } else {
                    log.error("活动实例状态或者工作项状态错误，不允许完成工作项……");
                }
            }
        } catch (Exception e) {
            log.error("作废工作项，执行下一个工作项异常", e);
        }
        return completeJson;
    }

    /**
     * 校验当前模板是否可用
     *
     * @return
     */
    private boolean checkTemplateIsAvailable(JSONObject flowTemplate) {
        boolean availableFlag = true;

        if (flowTemplate == null || flowTemplate.isEmpty()) {
            availableFlag = false;
            log.error("当前模板未进行初始化，校验失败……");
        } else if (flowTemplate.getJSONArray("activityList").size() == 0) {
            availableFlag = false;
            log.error("当前模板没有定义活动列表，校验失败……");
        }

        return availableFlag;
    }

    /**
     * 获取下一个工作项
     *
     * @param flowTemplate
     * @param fromActivityInstanceJson
     * @return
     */
    private JSONObject getNextWorkitem(JSONObject flowTemplate, JSONObject fromActivityInstanceJson) {
        JSONObject returnWorkitemJson = new JSONObject();
        try {
            JSONArray activityList = flowTemplate.getJSONArray("activityList");
            JSONObject toActivityJson = new JSONObject();
            String fromActivityDefineId = fromActivityInstanceJson.getString("activityDefineId");
            if (fromActivityDefineId == null || "".equals(fromActivityDefineId)) {
                // 活动定义ID为空，代表执行第一个活动实例
                toActivityJson = activityList.getJSONObject(0);
            } else {
                // 活动定义ID不为空，根据ID寻找活动实例，找到活动实例后取该活动实例的下一个活动实例
                for (Iterator iterator = activityList.iterator(); iterator.hasNext();) {
                    JSONObject jsonObject = (JSONObject) iterator.next();
                    if(fromActivityDefineId.equals(jsonObject.getString("activityDefineId"))) {
                        if(iterator.hasNext()) {
                            toActivityJson = (JSONObject) iterator.next();
                        }
                        break;
                    }
                }
            }

            if(toActivityJson.isEmpty()) {
                // 没有下一个活动实例，代表流程已经结束，需要将状态更新为10F
                Map updateMap = new HashMap();
                updateMap.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                updateMap.put("state", "10F");
                flowEngineDaoImpl.updateFlowInstance(updateMap);

                returnWorkitemJson.put("isEnd", "yes");
                return returnWorkitemJson;
            }

            JSONObject transitionInstanceJson = new JSONObject();
            transitionInstanceJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
            transitionInstanceJson.put("fromActivityId", fromActivityInstanceJson.getString("activityDefineId"));
            transitionInstanceJson.put("toActivityId", toActivityJson.getString("activityDefineId"));
            // 线条默认状态为10F（已完成）
            transitionInstanceJson.put("state", "10F");
            // 正向线条
            transitionInstanceJson.put("direction", "1");
            // 创建线条实例
            createTransitionInstance(transitionInstanceJson);

            if("yes".equals(toActivityJson.getString("isWorkitem"))) {
                // isWorkitem=yes代表需要生成工单项
                JSONObject workitemJson = new JSONObject();
                workitemJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                workitemJson.put("tacheId", toActivityJson.getString("tacheId"));
                workitemJson.put("state", "10N");
                String workitemId = createWorkitem(workitemJson);

                if(!"".equals(workitemId)) {
                    toActivityJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                    toActivityJson.put("state", "10N");
                    toActivityJson.put("workitemId", workitemId);
                    if(createActivityInstance(toActivityJson)) {
                        // 环节ID
                        returnWorkitemJson.put("tacheId", toActivityJson.getString("tacheId"));
                        // 工作项ID
                        returnWorkitemJson.put("workitemId", workitemId);
                        // 是否结束
                        returnWorkitemJson.put("isEnd", "no");
                        // 活动归属模块
                        returnWorkitemJson.put("module", toActivityJson.getString("module"));
                    } else {
                        log.error("创建活动实例失败……");
                    }
                } else {
                    log.error("创建工作项失败……");
                }

            } else {
                // 当前没有工作项，活动实例直接完成
                toActivityJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                toActivityJson.put("state", "10F");
                toActivityJson.put("workitemId", "");
                if(createActivityInstance(toActivityJson)) {
                    // 当前活动实例无需返回工单项，继续寻找
                    returnWorkitemJson = getNextWorkitem(flowTemplate, toActivityJson);
                } else {
                    log.error("创建活动实例失败……");
                }
            }

        } catch (Exception e) {
            log.error("获取下一个工作项异常", e);
        }
        return returnWorkitemJson;
    }

    /**
     * 获取前一个工作项
     *
     * @param flowTemplate
     * @param fromActivityInstanceJson
     * @return
     */
    private JSONObject getPrevWorkitem(JSONObject flowTemplate, JSONObject fromActivityInstanceJson) {
        JSONObject returnWorkitemJson = new JSONObject();
        try {
            JSONArray activityList = flowTemplate.getJSONArray("activityList");
            JSONObject toActivityJson = new JSONObject();
            String fromActivityDefineId = fromActivityInstanceJson.getString("activityDefineId");
            if (fromActivityDefineId == null || "".equals(fromActivityDefineId)) {
                log.error("找不到当前活动实例的活动模板ID……");
            } else {
                // 对活动列表进行逆序排列
                Collections.reverse(activityList);
                // 活动定义ID不为空，根据ID寻找活动实例，找到活动实例后取该活动实例的下一个活动实例
                String returnTo = "";
                for (Iterator iterator = activityList.iterator(); iterator.hasNext();) {
                    JSONObject jsonObject = (JSONObject) iterator.next();
                    if(fromActivityDefineId.equals(jsonObject.getString("activityDefineId"))) {
                        // 如果活动模板中定义了returnTo，则回退时不是前走，而是直接回退至指定活动项
                        if(jsonObject.containsKey("returnTo")) {
                            // 设置回退的活动项定义ID
                            returnTo = jsonObject.getString("returnTo");
                        } else {
                            if(iterator.hasNext()) {
                                toActivityJson = (JSONObject) iterator.next();
                            }
                            break;
                        }
                    } else if(returnTo.equals(jsonObject.getString("activityDefineId"))){
                        toActivityJson = jsonObject;
                        break;
                    }
                }
            }

            if(toActivityJson.isEmpty()) {
                // 没有上一个活动实例，代表已经退到第一个活动项，需要将状态更新为10R
                Map updateMap = new HashMap();
                updateMap.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                updateMap.put("state", "10R");
                flowEngineDaoImpl.updateFlowInstance(updateMap);

                returnWorkitemJson.put("isEnd", "cancel");
                return returnWorkitemJson;
            }

            JSONObject transitionInstanceJson = new JSONObject();
            transitionInstanceJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
            transitionInstanceJson.put("fromActivityId", fromActivityInstanceJson.getString("activityDefineId"));
            transitionInstanceJson.put("toActivityId", toActivityJson.getString("activityDefineId"));
            // 线条默认状态为10F（已完成）
            transitionInstanceJson.put("state", "10F");
            // 正向线条
            transitionInstanceJson.put("direction", "0");
            // 创建线条实例
            createTransitionInstance(transitionInstanceJson);

            if("yes".equals(toActivityJson.getString("isWorkitem"))) {
                // isWorkitem=yes代表需要生成工单项
                JSONObject workitemJson = new JSONObject();
                workitemJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                workitemJson.put("tacheId", toActivityJson.getString("tacheId"));
                workitemJson.put("state", "10N");
                String workitemId = createWorkitem(workitemJson);

                if(!"".equals(workitemId)) {
                    toActivityJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                    toActivityJson.put("state", "10N");
                    toActivityJson.put("workitemId", workitemId);
                    if(createActivityInstance(toActivityJson)) {
                        // 环节ID
                        returnWorkitemJson.put("tacheId", toActivityJson.getString("tacheId"));
                        // 工作项ID
                        returnWorkitemJson.put("workitemId", workitemId);
                        // 是否结束
                        returnWorkitemJson.put("isEnd", "no");
                        // 活动归属模块
                        returnWorkitemJson.put("module", toActivityJson.getString("module"));
                    } else {
                        log.error("创建活动实例失败……");
                    }
                } else {
                    log.error("创建工作项失败……");
                }

            } else {
                // 当前没有工作项，活动实例直接完成
                toActivityJson.put("flowInstanceId", fromActivityInstanceJson.getString("flowInstanceId"));
                toActivityJson.put("state", "10F");
                toActivityJson.put("workitemId", "");
                if(createActivityInstance(toActivityJson)) {
                    // 当前活动实例无需返回工单项，继续寻找
                    returnWorkitemJson = getPrevWorkitem(flowTemplate, toActivityJson);
                } else {
                    log.error("创建活动实例失败……");
                }
            }

        } catch (Exception e) {
            log.error("获取前一个工作项异常", e);
        }
        return returnWorkitemJson;
    }

    /**
     * 创建线条实例
     * @param createJson
     * @return
     */
    private boolean createTransitionInstance(JSONObject createJson) {
        boolean createFlag = false;

        try {
            Map qryMap = new HashMap();
            qryMap.put("seqName", "ot_transition_instance_seq");
            String seq = flowEngineDaoImpl.getFlowEngineSeq(qryMap);
            Map insertMap = new HashMap();
            insertMap.put("transitionInstanceId", seq);
            insertMap.put("flowInstanceId", createJson.getString("flowInstanceId"));
            insertMap.put("fromActivityId", createJson.getString("fromActivityId"));
            insertMap.put("toActivityId", createJson.getString("toActivityId"));
            insertMap.put("state", createJson.getString("state"));
            insertMap.put("direction", createJson.getString("direction"));
            flowEngineDaoImpl.insertTransitionInstance(insertMap);

            createFlag = true;
        } catch (Exception e) {
            log.error("创建线条实例异常", e);
        }

        return createFlag;
    }

    /**
     * 创建活动实例
     * @param createJson
     * @return
     */
    private boolean createActivityInstance(JSONObject createJson) {
        boolean createFlag = false;

        try {
            Map qryMap = new HashMap();
            qryMap.put("seqName", "ot_activity_instance_seq");
            String seq = flowEngineDaoImpl.getFlowEngineSeq(qryMap);
            Map insertMap = new HashMap();
            insertMap.put("activityInstanceId", seq);
            insertMap.put("flowInstanceId", createJson.getString("flowInstanceId"));
            insertMap.put("activityDefineId", createJson.getString("activityDefineId"));
            insertMap.put("tacheId", createJson.getString("tacheId"));
            insertMap.put("state", createJson.getString("state"));
            insertMap.put("workitemId", createJson.getString("workitemId"));
            flowEngineDaoImpl.insertActivityInstance(insertMap);

            createFlag = true;
        } catch (Exception e) {
            log.error("创建活动实例异常", e);
        }

        return createFlag;
    }

    /**
     * 创建工作项
     * @param createJson
     * @return
     */
    private String createWorkitem(JSONObject createJson) {
        String workitem = "";

        try {
            Map qryMap = new HashMap();
            qryMap.put("seqName", "ot_workitem_seq");
            String seq = flowEngineDaoImpl.getFlowEngineSeq(qryMap);
            Map insertMap = new HashMap();
            insertMap.put("workitemId", seq);
            insertMap.put("flowInstanceId", createJson.getString("flowInstanceId"));
            insertMap.put("tacheId", createJson.getString("tacheId"));
            insertMap.put("state", createJson.getString("state"));
            flowEngineDaoImpl.insertWorkitem(insertMap);

            workitem = seq;
        } catch (Exception e) {
            log.error("创建工作项异常", e);
        }

        return workitem;
    }
}
