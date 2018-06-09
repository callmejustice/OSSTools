package com.ztesoft.iom.manage.kbs.dao.impl;

import com.ztesoft.iom.manage.kbs.dao.KBSDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 知识库操作dao实现类
 * @author: huang.jing
 * @Date: 2018/3/20 0020 - 19:57
 */
public class KBSDaoImpl extends JdbcDaoSupport implements KBSDao {

    private static Logger log = LogManager.getLogger(KBSDaoImpl.class);

    /**
     * 查询知识点
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getKnowledge(Map qryMap) throws Exception {
        List orderList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("SELECT oki.knowledge_id, oki.knowledge_title, oki.knowledge_desc, oki.knowledge_type, oki.knowledge_detail, oki.knowledge_file, oki.author, to_char(oki.create_date, 'yyyy-mm-dd hh24:mi:ss') create_date, to_char(oki.modify_date, 'yyyy-mm-dd hh24:mi:ss') modify_date ");
        sql.append("FROM ot_knowledge_info oki ");
        sql.append("WHERE oki.state = '1' ");

        // 知识点ID
        if (!"".equals(MapUtils.getString(qryMap, "knowledgeId", ""))) {
            sql.append("AND oki.knowledge_id = ? ");
            paramList.add(MapUtils.getString(qryMap, "knowledgeId", ""));
        }
        // 关键字
        if (!"".equals(MapUtils.getString(qryMap, "keyWord", ""))) {
            sql.append("AND (oki.knowledge_title LIKE '%'||?||'%' ");
            paramList.add(MapUtils.getString(qryMap, "keyWord", ""));

            sql.append("OR oki.knowledge_desc LIKE '%'||?||'%' ");
            paramList.add(MapUtils.getString(qryMap, "keyWord", ""));

            sql.append("OR UTL_RAW.CAST_TO_VARCHAR2(oki.knowledge_detail) LIKE '%'||?||'%') ");
            paramList.add(MapUtils.getString(qryMap, "keyWord", ""));
        }
        // 知识点类型
        if (!"".equals(MapUtils.getString(qryMap, "knowledgeType", ""))) {
            sql.append("AND oki.knowledge_type = ? ");
            paramList.add(MapUtils.getString(qryMap, "knowledgeType", ""));
        }
        // 作者
        if (!"".equals(MapUtils.getString(qryMap, "author", ""))) {
            sql.append("AND oki.author = ? ");
            paramList.add(MapUtils.getString(qryMap, "author", ""));
        }
        sql.append("ORDER BY oki.create_date DESC ");

        log.debug("查询知识点脚本：" + sql.toString());

        try {
            log.debug("查询知识点参数：" + paramList);
            orderList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("查询知识点异常", e);
        }
        return orderList;
    }

    /**
     * 插入知识点
     *
     * @param insertMap
     * @return
     * @throws Exception
     */
    @Override
    public int insertKnowledge(Map insertMap) throws Exception {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("INSERT INTO ot_knowledge_info(knowledge_id, knowledge_title, knowledge_desc, knowledge_type, knowledge_detail, knowledge_file, author, create_date, state, modify_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, sysdate, ?, sysdate) ");
        log.debug("插入知识点脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(insertMap, "knowledgeId", ""));
            paramList.add(MapUtils.getString(insertMap, "knowledgeTtile", ""));
            paramList.add(MapUtils.getString(insertMap, "knowledgeDesc", ""));
            paramList.add(MapUtils.getString(insertMap, "knowledgeType", ""));
            paramList.add(MapUtils.getString(insertMap, "knowledgeDetail", ""));
            paramList.add(MapUtils.getString(insertMap, "knowledgeFileName", ""));
            paramList.add(MapUtils.getString(insertMap, "author", ""));
            paramList.add(MapUtils.getString(insertMap, "state", "1"));
            log.debug("插入知识点参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("插入知识点异常", e);
        }
        return insertCount;
    }

    /**
     * 删除知识点（逻辑删除）
     *
     * @param deleteMap
     * @return
     * @throws Exception
     */
    @Override
    public int deleteKnowledge(Map deleteMap) throws Exception {
        int deleteCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_knowledge_info oki ");
        sql.append("SET oki.state = ?, oki.modify_date = sysdate ");
        sql.append("WHERE oki.knowledge_id = ? ");
        log.debug("删除知识点脚本：" + sql.toString());

        try {
            paramList.add("0");
            paramList.add(MapUtils.getString(deleteMap, "knowledgeId", ""));
            log.debug("删除知识点参数：" + paramList);
            deleteCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("删除知识点异常", e);
        }
        return deleteCount;
    }

    /**
     * 修改知识点
     *
     * @param updateMap
     * @return
     * @throws Exception
     */
    @Override
    public int updateKnowledge(Map updateMap) throws Exception {
        int updateCount = 0;
        StringBuffer sql = new StringBuffer();
        List paramList = new ArrayList();
        sql.append("UPDATE ot_knowledge_info oki ");
        sql.append("SET oki.knowledge_title = ?, oki.knowledge_desc = ?, oki.knowledge_type = ?, oki.knowledge_file = ?, oki.modify_date = sysdate ");
        sql.append("WHERE oki.knowledge_id = ? ");
        log.debug("修改知识点脚本：" + sql.toString());

        try {
            paramList.add(MapUtils.getString(updateMap, "knowledgeTtile", ""));
            paramList.add(MapUtils.getString(updateMap, "knowledgeDesc", ""));
            paramList.add(MapUtils.getString(updateMap, "knowledgeType", ""));
            paramList.add(MapUtils.getString(updateMap, "knowledgeFileName", ""));
            paramList.add(MapUtils.getString(updateMap, "knowledgeId", ""));
            log.debug("修改知识点参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            throw new Exception("修改知识点异常", e);
        }
        return updateCount;
    }
}
