package com.ztesoft.iom.workOrder.material.dao.impl;

import com.ztesoft.iom.workOrder.material.dao.MaterialDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 耗材信息dao实现类
 * @author: huang.jing
 * @Date: 2018/4/25 0025 - 16:10
 */
public class MaterialDaoImpl extends JdbcDaoSupport implements MaterialDao {

    private static Logger log = LogManager.getLogger(MaterialDaoImpl.class);

    /**
     * 插入耗材统计报表日志记录
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertReportWorkOrderMaterialLog(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO report_work_order_material_log(order_id, work_order_id, order_code, type_id, product_type, md5, type_name, use_num, unit, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate) ");
        log.debug("插入耗材统计报表日志记录脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "orderId"));
            paramList.add(MapUtils.getString(insertMap, "workOrderId"));
            paramList.add(MapUtils.getString(insertMap, "orderCode"));
            paramList.add(MapUtils.getString(insertMap, "typeId"));
            paramList.add(MapUtils.getString(insertMap, "productType"));
            paramList.add(MapUtils.getString(insertMap, "md5"));
            paramList.add(MapUtils.getString(insertMap, "typeName"));
            paramList.add(MapUtils.getString(insertMap, "useNum"));
            paramList.add(MapUtils.getString(insertMap, "unit"));
            log.debug("插入耗材统计报表日志记录参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入耗材统计报表日志记录异常", e);
        }
        return insertCount;
    }

    /**
     * 获取耗材统计报表基本信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getReportWorkOrderMaterialBaseInfo(Map qryMap) throws Exception {
        List reportWorkOrderMaterialBaseInfoList = new ArrayList();
        List paramList = new ArrayList();

        // 取宽表中前一天竣工的数据
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT to_char(oofw.finish_date, 'yyyy/mm/dd') finish_date, oofw.user_area_name, nvl(oofw.county, '其它') county, nvl(oofw.exch_name, '其它') exch_name, nvl(oofw.gird, '其它') grid, oofw.service_id, nvl(decode(oofw.construct_type, '0', '冷接', '1', '热熔', '2', '定长尾纤', oofw.construct_type), '其它') as construct_type, oofw.new_access_type access_type, wwomn.order_id, oofw.order_code, pp.name product_type, wwomn.work_order_id, wwomn.type_id, wwomn.type_name, nvl(wwomn.small_species_name, '其它') material_name, wwomn.use_num, wwomn.unit ");
        sql.append("FROM om_order_finish_wid oofw, wo_work_order_material_n wwomn, pm_product pp ");
        sql.append("WHERE oofw.order_id = wwomn.order_id ");
        sql.append("AND oofw.finish_date >= TRUNC(sysdate) - 1 ");
        sql.append("AND oofw.finish_date < TRUNC(sysdate) ");
        sql.append("AND oofw.indep_prod_id = pp.id ");
        sql.append("AND NOT EXISTS(SELECT 1 FROM report_work_order_material_log rwoml WHERE wwomn.work_order_id = rwoml.work_order_id AND wwomn.type_id = rwoml.type_id) ");
        sql.append("AND ROWNUM <= 5000 ");

        log.debug("获取耗材统计报表基本信息脚本：" + sql.toString());
        try {
            log.debug("获取耗材统计报表基本信息参数：" + paramList);
            reportWorkOrderMaterialBaseInfoList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取耗材统计报表基本信息异常", e);
        }
        return reportWorkOrderMaterialBaseInfoList;
    }

    /**
     * 获取耗材统计报表信息
     *
     * @param qryMap
     * @return
     * @throws Exception
     */
    @Override
    public List getReportWorkOrderMaterial(Map qryMap) throws Exception {
        List reportWorkOrderMaterialList = new ArrayList();
        List paramList = new ArrayList();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT rwom.md5, rwom.area_name, rwom.county, rwom.grid, rwom.exch_name, rwom.service_id, rwom.service_id, rwom.const_type, to_char(rwom.finish_date, 'yyyy/mm/dd') finish_date, rwom.material_name, rwom.access_type rwom.order_num, rwom.use_num, rwom.unit ");
        sql.append("FROM report_work_order_material rwom ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND rwom.md5 = ? ");
        paramList.add(MapUtils.getString(qryMap, "md5"));

        log.debug("获取耗材统计报表信息脚本：" + sql.toString());
        try {
            log.debug("获取耗材统计报表信息参数：" + paramList);
            reportWorkOrderMaterialList = this.getJdbcTemplate().queryForList(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("获取耗材统计报表信息异常", e);
        }
        return reportWorkOrderMaterialList;
    }

    /**
     * 插入耗材统计报表
     *
     * @param insertMap
     * @return
     */
    @Override
    public int insertReportWorkOrderMaterial(Map insertMap) throws Exception {
        int insertCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO report_work_order_material(md5, area_name, county, grid, exch_name, service_id, const_type, finish_date, material_name, access_type, order_num, use_num, unit, create_date) ");
        sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyy/mm/dd'), ?, ?, ?, ?, ?, sysdate) ");
        log.debug("插入耗材统计报表脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(insertMap, "md5"));
            paramList.add(MapUtils.getString(insertMap, "areaName"));
            paramList.add(MapUtils.getString(insertMap, "county"));
            paramList.add(MapUtils.getString(insertMap, "grid"));
            paramList.add(MapUtils.getString(insertMap, "exchName"));
            paramList.add(MapUtils.getString(insertMap, "serviceId"));
            paramList.add(MapUtils.getString(insertMap, "constType"));
            paramList.add(MapUtils.getString(insertMap, "finishDate"));
            paramList.add(MapUtils.getString(insertMap, "materialName"));
            paramList.add(MapUtils.getString(insertMap, "accessType"));
            paramList.add(MapUtils.getString(insertMap, "orderNum"));
            paramList.add(MapUtils.getString(insertMap, "useNum"));
            paramList.add(MapUtils.getString(insertMap, "unit"));
            log.debug("插入耗材统计报表参数：" + paramList);
            insertCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("插入耗材统计报表异常", e);
        }
        return insertCount;
    }

    /**
     * 更新耗材统计报表
     *
     * @param updateMap
     * @return
     */
    @Override
    public int updateReportWorkOrderMaterial(Map updateMap) throws Exception {
        int updateCount = 0;
        List paramList = new ArrayList();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE report_work_order_material rwom ");
        sql.append("SET rwom.order_num = rwom.order_num + ?, rwom.use_num = rwom.use_num + ?, rwom.update_date = sysdate ");
        sql.append("WHERE rwom.md5 = ? ");
        log.debug("更新耗材统计报表脚本：" + sql.toString());
        try {
            paramList.add(MapUtils.getString(updateMap, "orderNum"));
            paramList.add(MapUtils.getString(updateMap, "useNum"));
            paramList.add(MapUtils.getString(updateMap, "md5"));
            log.debug("更新耗材统计报表参数：" + paramList);
            updateCount = this.getJdbcTemplate().update(sql.toString(), paramList.toArray());
        } catch (Exception e) {
            log.error("更新耗材统计报表异常", e);
        }
        return updateCount;
    }
}
