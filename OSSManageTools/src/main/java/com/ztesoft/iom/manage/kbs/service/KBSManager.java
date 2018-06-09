package com.ztesoft.iom.manage.kbs.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.WordUtils;
import com.ztesoft.iom.manage.develop.dao.DevelopOrderDao;
import com.ztesoft.iom.manage.kbs.dao.KBSDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/3/20 0020 - 20:07
 */
public class KBSManager {

    private static Logger log = LogManager.getLogger(KBSManager.class);
    private DevelopOrderDao developOrderDao;
    private KBSDao kbsDao;

    /**
     * 插入知识点
     *
     * @param createJson
     * @return
     */
    public boolean createKnowledge(JSONObject createJson) {
        log.info("createKnowledge入口……");
        boolean createFlag = false;
        try {
            developOrderDao = (DevelopOrderDao) BeanFactory.getApplicationContext().getBean("developOrderDaoImpl");
            Map qryMap = new HashMap();
            qryMap.put("seqName", "ot_knowledge_info_seq");
            String seq = developOrderDao.getDevelopModuleSeq(qryMap);
            kbsDao = (KBSDao) BeanFactory.getApplicationContext().getBean("kbsDaoImpl");

            String knowledgeFileName = createJson.getString("knowledgeFileName");
            String knowledgeTitle = createJson.getString("knowledgeTitle");
            String serverPath = createJson.getString("serverPath");
            WordUtils wordUtils = (WordUtils) BeanFactory.getApplicationContext().getBean("wordUtils");
            // 将用户填写的标题设置为html文件的标题
            wordUtils.autoReplace(serverPath + knowledgeFileName, "<head>", "<head><title>" + knowledgeTitle + "</title>");

            Map insertMap = new HashMap();
            insertMap.put("knowledgeId", seq);
            insertMap.put("knowledgeTtile", knowledgeTitle);
            insertMap.put("knowledgeDesc", createJson.getString("knowledgeDesc"));
            insertMap.put("knowledgeType", createJson.getString("knowledgeType"));
            insertMap.put("knowledgeDetail", createJson.getString("knowledgeDetail"));
            insertMap.put("knowledgeFileName", knowledgeFileName);
            insertMap.put("author", createJson.getString("author"));
            createFlag = kbsDao.insertKnowledge(insertMap) > 0;
        } catch (Exception e) {
            log.error("插入知识点异常", e);
        }
        log.info("createKnowledge出口……");
        return createFlag;
    }

    /**
     * 查询知识点
     *
     * @param qryJson
     * @return
     */
    public List getKnowledge(JSONObject qryJson) {
        log.info("getKnowledge入口……");
        List knowledgeList = new ArrayList();
        try {
            kbsDao = (KBSDao) BeanFactory.getApplicationContext().getBean("kbsDaoImpl");

            Map qryMap = new HashMap();
            qryMap.put("knowledgeId", qryJson.getString("knowledgeId"));
            qryMap.put("keyWord", qryJson.getString("keyWord"));
            qryMap.put("knowledgeType", qryJson.getString("knowledgeType"));
            qryMap.put("author", qryJson.getString("author"));
            knowledgeList = kbsDao.getKnowledge(qryMap);
        } catch (Exception e) {
            log.error("查询知识点异常", e);
        }
        log.info("getKnowledge出口……");
        return knowledgeList;
    }

    /**
     * 删除知识点
     *
     * @param deleteJson
     * @return
     */
    public boolean deleteKnowledge(JSONObject deleteJson) {
        log.info("deleteKnowledge入口……");
        boolean deleteFlag = false;
        try {
            kbsDao = (KBSDao) BeanFactory.getApplicationContext().getBean("kbsDaoImpl");

            Map deleteMap = new HashMap();
            deleteMap.put("knowledgeId", deleteJson.getString("knowledgeId"));
            deleteMap.put("author", deleteJson.getString("author"));
            // 先校验要删除的知识点的作者是否和登录人相同
            if (kbsDao.getKnowledge(deleteMap).size() > 0) {
                deleteFlag = kbsDao.deleteKnowledge(deleteMap) > 0;
            } else {
                log.error("删除知识点校验失败……");
            }

        } catch (Exception e) {
            log.error("删除知识点异常", e);
        }
        log.info("deleteKnowledge出口……");
        return deleteFlag;
    }

    /**
     * 更新知识点
     *
     * @param updateJson
     * @return
     */
    public boolean updateKnowledge(JSONObject updateJson) {
        log.info("updateKnowledge入口……");
        boolean updateFlag = false;
        try {
            String knowledgeFileName = updateJson.getString("knowledgeFileName");
            String knowledgeTitle = updateJson.getString("knowledgeTitle");
            String serverPath = updateJson.getString("serverPath");
            WordUtils wordUtils = (WordUtils) BeanFactory.getApplicationContext().getBean("wordUtils");

            String content = wordUtils.getFileContent(serverPath + knowledgeFileName);
            if (content.indexOf("<head><title>") < 0) {
                // 将用户填写的标题设置为html文件的标题
                wordUtils.autoReplace(serverPath + knowledgeFileName, "<head>", "<head><title>" + knowledgeTitle + "</title>");
            } else {
                // 替换已经存在的标题
                wordUtils.autoReplace(serverPath + knowledgeFileName, "<head><title>.+</title>", "<head><title>" + knowledgeTitle + "</title>");
            }
            kbsDao = (KBSDao) BeanFactory.getApplicationContext().getBean("kbsDaoImpl");

            Map qryMap = new HashMap();
            qryMap.put("knowledgeId", updateJson.getString("knowledgeId"));
            qryMap.put("author", updateJson.getString("author"));
            // 先校验要修改的知识点的作者是否和登录人相同
            if (kbsDao.getKnowledge(qryMap).size() > 0) {
                Map updateMap = new HashMap();
                updateMap.put("knowledgeId", updateJson.getString("knowledgeId"));
                updateMap.put("knowledgeTtile", knowledgeTitle);
                updateMap.put("knowledgeDesc", updateJson.getString("knowledgeDesc"));
                updateMap.put("knowledgeType", updateJson.getString("knowledgeType"));
                updateMap.put("knowledgeDetail", updateJson.getString("knowledgeDetail"));
                updateMap.put("knowledgeFileName", knowledgeFileName);
                updateFlag = kbsDao.updateKnowledge(updateMap) > 0;
            } else {
                log.error("修改知识点校验失败……");
            }
        } catch (Exception e) {
            log.error("更新知识点异常", e);
        }
        log.info("updateKnowledge出口……");
        return updateFlag;
    }
}
