package com.ztesoft.iom.manage.flow.dao.impl;

import com.ztesoft.iom.manage.flow.dao.FlowEngineDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/1/14 0014 - 16:19
 */
public class FlowEngineDaoImpl extends JdbcDaoSupport implements FlowEngineDao {

    private static Logger log = LogManager.getLogger(FlowEngineDaoImpl.class);

    /**
     * 获取流程引擎相关序列
     *
     * @param qryMap
     * @return
     */
    @Override
    public String getFlowEngineSeq(Map qryMap) throws Exception {
        String seq = "";
        StringBuffer sql = new StringBuffer();

        try {
            // 在序列前加上yyyymmddhh24miss，每秒最多可以生成10000个不重复的序列，序列长度为固定18位
            sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(" + MapUtils.getString(qryMap, "seqName") + ".nextval, '0000')) seq FROM dual ");
            log.debug("获取流程引擎相关序列脚本：" + sql.toString());
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ", "");
            }
        } catch (Exception e) {
            throw new Exception("获取流程引擎相关序列异常", e);
        }
        return seq;
    }

    /**
     * 新建流程实例
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertFlowInstance(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_flow_instance(id, flow_define_id, flow_define_name, version, type, state, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, sysdate) ");
        log.debug("新建流程实例脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "flowInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowDefineId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowDefineName", ""));
            paramList.add(MapUtils.getString(insertMap, "version", ""));
            paramList.add(MapUtils.getString(insertMap, "type", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            log.debug("新建流程实例参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建流程实例异常", e);
        }
        return insertCount;
    }

    /**
     * 新建线条实例
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertTransitionInstance(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_transition_instance(id, flow_instance_id, from_activity_id, to_activity_id, state, direction, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, sysdate) ");
        log.debug("新建线条实例脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "transitionInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "fromActivityId", ""));
            paramList.add(MapUtils.getString(insertMap, "toActivityId", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            paramList.add(MapUtils.getString(insertMap, "direction", ""));
            log.debug("新建线条实例参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建线条实例异常", e);
        }
        return insertCount;
    }

    /**
     * 新建活动实例
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertActivityInstance(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_activity_instance(id, flow_instance_id, activity_define_id, tache_id, state, workitem_id, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, sysdate) ");
        log.debug("新建活动实例脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "activityInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "activityDefineId", ""));
            paramList.add(MapUtils.getString(insertMap, "tacheId", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            paramList.add(MapUtils.getString(insertMap, "workitemId", ""));
            log.debug("新建活动实例参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建活动实例异常", e);
        }
        return insertCount;
    }

    /**
     * 新建工作项
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertWorkitem(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_workitem(id, flow_instance_id, tache_id, state, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, sysdate) ");
        log.debug("新建工作项脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "workitemId", ""));
            paramList.add(MapUtils.getString(insertMap, "flowInstanceId", ""));
            paramList.add(MapUtils.getString(insertMap, "tacheId", ""));
            paramList.add(MapUtils.getString(insertMap, "state", ""));
            log.debug("新建工作项参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("新建工作项异常", e);
        }
        return insertCount;
    }

    /**
     * 更新流程实例
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateFlowInstance(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_flow_instance ");
        sql.append("SET state = ? ");
        paramList.add(MapUtils.getString(updateMap, "state", ""));
        // 10F代表流程已经运行完成，需要更新finish_date字段
        if ("10F".equals(MapUtils.getString(updateMap, "state", ""))) {
            sql.append(",finish_date = sysdate ");
        }
        sql.append("WHERE id = ? ");
        log.debug("更新流程实例脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "flowInstanceId", ""));
            log.debug("更新流程实例参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("更新流程实例异常", e);
        }
        return updateCount;
    }

    /**
     * 获取活动实例信息
     *
     * @param qryMap
     * @return
     */
    @Override
    public Map getWokritemAndActivityInstanceMap(Map qryMap) throws Exception {
        log.info("qryMap：" + qryMap);
        Map activityInstanceMap = new HashMap();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT oai.id activity_instance_id, oai.flow_instance_id, oai.activity_define_id, oai.tache_id, oai.state activity_state, oai.workitem_id, ow.state workitem_state, ow.id workitem_id ");
        sql.append("FROM ot_activity_instance oai, ot_workitem ow ");
        sql.append("WHERE oai.workitem_id = ow.id ");
        if (!"".equals(MapUtils.getString(qryMap, "workitemId", ""))) {
            sql.append("AND oai.workitem_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "workitemId", ""));
        }
        log.debug("获取活动实例信息脚本：" + sql.toString());

        try {

            log.debug("获取活动实例信息参数：" + paramList);
            List activityInstanceList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (activityInstanceList != null && activityInstanceList.size() > 0) {
                activityInstanceMap = (Map) activityInstanceList.get(0);
            }
        } catch (Exception e) {
            throw new Exception("获取活动实例信息异常", e);
        }
        return activityInstanceMap;
    }

    /**
     * 更新活动实例
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateActivityInstance(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_activity_instance ");
        sql.append("SET state = ? ");
        paramList.add(MapUtils.getString(updateMap, "state", ""));
        // 10F代表流程已经运行完成，需要更新finish_date字段
        if ("10F".equals(MapUtils.getString(updateMap, "state", ""))) {
            sql.append(",finish_date = sysdate ");
        }
        sql.append("WHERE id = ? ");
        log.debug("更新活动实例脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "activityInstanceId", ""));
            log.debug("更新活动实例参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("更新活动实例异常", e);
        }
        return updateCount;
    }

    /**
     * 更新工作项
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateWorkitem(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_workitem ");
        sql.append("SET state = ? ");
        paramList.add(MapUtils.getString(updateMap, "state", ""));
        // 10F代表流程已经运行完成，需要更新finish_date字段
        if ("10F".equals(MapUtils.getString(updateMap, "state", ""))) {
            sql.append(",finish_date = sysdate ");
        }
        sql.append("WHERE id = ? ");
        log.debug("更新工作项脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "workitemId", ""));
            log.debug("更新工作项参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("更新工作项异常", e);
        }
        return updateCount;
    }
}
