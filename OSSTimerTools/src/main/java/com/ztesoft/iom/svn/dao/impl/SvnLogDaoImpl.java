package com.ztesoft.iom.svn.dao.impl;

import com.ztesoft.iom.svn.dao.SvnLogDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: svn日志操作dao实现类
 * @author: huang.jing
 * @Date: 2018/1/18 0018 - 15:04
 */
public class SvnLogDaoImpl extends JdbcDaoSupport implements SvnLogDao {

    private static Logger log = LogManager.getLogger(SvnLogDaoImpl.class);

    /**
     * 获取svn库表相关序列
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public String getSvnLogSeq(Map qryMap) throws Exception {
        String seq = "";
        StringBuffer sql = new StringBuffer();

        try {
            // 在序列前加上yyyymmddhh24miss，每秒最多可以生成10000个不重复的序列，序列长度为固定18位
            sql.append("SELECT to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(" + MapUtils.getString(qryMap, "seqName") + ".nextval, '0000')) seq FROM dual ");
            log.debug("获取svn库表相关序列脚本：" + sql.toString());
            List seqList = this.getJdbcTemplate().queryForList(sql.toString());
            for (int i = 0; i < seqList.size(); i++) {
                Map seqMap = (Map) seqList.get(i);
                seq = MapUtils.getString(seqMap, "SEQ", "");
            }
        } catch (Exception e) {
            throw new Exception("获取svn库表相关序列异常", e);
        }
        return seq;
    }

    /**
     * 查询svn版本是数量
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public int getRevisionCount(Map qryMap) throws Exception {
        int logCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT COUNT(1) log_count ");
        sql.append("FROM ot_svn_log osl ");
        sql.append("WHERE 1 = 1 ");
        if (!"".equals(MapUtils.getString(qryMap, "revision", ""))) {
            sql.append("AND osl.revision = ? ");
            paramList.add(MapUtils.getString(qryMap, "revision", ""));
        }
        if (!"".equals(MapUtils.getString(qryMap, "repository", ""))) {
            sql.append("AND osl.repository = ? ");
            paramList.add(MapUtils.getString(qryMap, "repository", ""));
        }
        log.debug("查询svn版本号数量脚本：" + sql.toString());

        try {
            log.debug("查询svn版本号数量参数：" + paramList);
            List svnLogListList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            if (svnLogListList != null && svnLogListList.size() > 0) {
                Map svnLogListMap = (Map) svnLogListList.get(0);
                logCount = MapUtils.getIntValue(svnLogListMap, "LOG_COUNT", 0);
            }
        } catch (Exception e) {
            throw new Exception("查询svn版本号数量异常", e);
        }
        return logCount;
    }

    /**
     * 插入svn日志记录
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    @Override
    public int insertSvnLog(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ot_svn_log(svn_log_id, revision, repository, author, message, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss')) ");

        log.debug("插入svn日志记录脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "svnLogId"));
            paramList.add(MapUtils.getString(insertMap, "revision"));
            paramList.add(MapUtils.getString(insertMap, "repository"));
            paramList.add(MapUtils.getString(insertMap, "author"));
            paramList.add(MapUtils.getString(insertMap, "message"));
            paramList.add(MapUtils.getString(insertMap, "createDate"));
            log.debug("插入svn日志记录参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入svn日志记录异常", e);
        }
        return insertCount;
    }

    /**
     * 插入svn文件变更记录
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    @Override
    public int insertSvnChangeList(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ot_svn_changelist(id, svn_log_id, project, path, type) ");
        sql.append("VALUES(?, ?, ?, ?, ?) ");

        log.debug("插入svn文件变更记录脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "changelistId"));
            paramList.add(MapUtils.getString(insertMap, "svnLogId"));
            paramList.add(MapUtils.getString(insertMap, "project"));
            paramList.add(MapUtils.getString(insertMap, "path"));
            paramList.add(MapUtils.getString(insertMap, "type"));
            log.debug("插入svn文件变更记录参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入svn文件变更记录异常", e);
        }
        return insertCount;
    }

    /**
     * 获取动态参数值
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public String getParamConfigValue(Map qryMap) throws Exception {
        String paramConfigValue = "";
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT otc.param_value ");
        sql.append("FROM ot_param_config otc ");
        sql.append("WHERE otc.param_name = ? ");
        log.debug("获取动态参数值：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(qryMap, "paramConfigName"));
            log.debug("获取动态参数值参数：" + paramList);
            List paramConfigList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
            for (int i = 0; i < paramConfigList.size(); i++) {
                Map paramConfigMap = (Map) paramConfigList.get(i);
                paramConfigValue = MapUtils.getString(paramConfigMap, "PARAM_VALUE", "");
            }
        } catch (Exception e) {
            throw new Exception("获取动态参数值异常", e);
        }
        return paramConfigValue;
    }

    /**
     * 更新动态参数值
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateParamConfigValue(Map updateMap) throws Exception {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ot_param_config otc ");
        sql.append("SET otc.param_value = ? ");
        sql.append("WHERE otc.param_name = ? ");
        log.debug("更新动态参数值脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(updateMap, "paramConfigValue"));
            paramList.add(MapUtils.getString(updateMap, "paramConfigName"));
            log.debug("更新动态参数值参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新动态参数值异常", e);
        }
        return updateCount;
    }
}
