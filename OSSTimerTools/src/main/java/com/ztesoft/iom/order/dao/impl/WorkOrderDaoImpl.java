package com.ztesoft.iom.order.dao.impl;

import com.ztesoft.iom.order.dao.WorkOrderDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/3/15 0015 - 11:56
 */
public class WorkOrderDaoImpl extends JdbcDaoSupport implements WorkOrderDao {

    private static Logger log = LogManager.getLogger(WorkOrderDaoImpl.class);

    /**
     * 获取待合并签名图片的定单号列表
     *
     * @param param
     * @return
     */
    @Override
    public List getNotMergeWorkOrderConfirmList(Map param) {
        List notMergeWorkOrderConfirmList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM (SELECT wccp.order_id, ");
        sql.append("(SELECT COUNT(1) FROM work_confirm_construction_pic wccp1 WHERE wccp.order_id = wccp1.order_id) order_count ");
        sql.append("FROM work_confirm_construction_pic wccp ");
        sql.append("WHERE NOT EXISTS(SELECT 1 FROM work_confirm_pic_merge wcpm WHERE wccp.order_id = wcpm.order_id) ");
        sql.append("GROUP BY wccp.order_id) ");
        sql.append("WHERE order_count = 3 ");
        sql.append("AND rownum <= 100 ");

        log.debug("获取待合并签名图片的定单号列表脚本：" + sql.toString());
        try {
            notMergeWorkOrderConfirmList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取待合并签名图片的定单号列表异常", e);
        }
        return notMergeWorkOrderConfirmList;
    }

    /**
     * 获取待合并的外线施工签名图片列表
     *
     * @param param
     * @return
     */
    @Override
    public List getWorkOrderConfirmList(Map param) {
        List notMergeWorkOrderConfirmList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT wccp.order_code, wccp.file_name, wccp.file_name_path ");
        sql.append("FROM work_confirm_construction_pic wccp ");
        sql.append("WHERE wccp.order_id = ? ");

        log.debug("获取待合并的外线施工签名图片列表脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "orderId"));
            log.debug("获取待合并的外线施工签名图片列表参数：" + paramList);
            notMergeWorkOrderConfirmList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取待合并的外线施工签名图片列表异常", e);
        }
        return notMergeWorkOrderConfirmList;
    }

    /**
     * 插入合并后的签名图片
     *
     * @param param
     * @return
     */
    @Override
    public int insertWorkOrderConfirmMergePicInfo(Map param) {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO work_confirm_pic_merge(id, order_id, order_code, file_name, file_name_path, in_time) ");
        sql.append("VALUES(w_confirm_construction_pic_seq.nextval, ?, ?, ?, ?, sysdate) ");

        log.debug("插入合并后的签名图片脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(param, "orderId"));
            paramList.add(MapUtils.getString(param, "orderCode"));
            paramList.add(MapUtils.getString(param, "fileName"));
            paramList.add(MapUtils.getString(param, "fileNamePath"));
            log.debug("插入合并后的签名图片参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入合并后的签名图片异常", e);
        }
        return insertCount;
    }

}
