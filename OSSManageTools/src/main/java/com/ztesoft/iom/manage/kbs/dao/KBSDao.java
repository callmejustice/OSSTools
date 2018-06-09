package com.ztesoft.iom.manage.kbs.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description: 知识库操作dao
 * @author: huang.jing
 * @Date: 2018/3/20 0020 - 19:55
 */
public interface KBSDao {

    /**
     * 查询知识点
     * @param qryMap
     * @return
     * @throws Exception
     */
    public List getKnowledge(Map qryMap) throws Exception;

    /**
     * 插入知识点
     * @param insertMap
     * @return
     * @throws Exception
     */
    public int insertKnowledge(Map insertMap) throws Exception;

    /**
     * 删除知识点（逻辑删除）
     * @param deleteMap
     * @return
     * @throws Exception
     */
    public int deleteKnowledge(Map deleteMap) throws Exception;

    /**
     * 修改知识点
     * @param updateMap
     * @return
     * @throws Exception
     */
    public int updateKnowledge(Map updateMap) throws Exception;
}
