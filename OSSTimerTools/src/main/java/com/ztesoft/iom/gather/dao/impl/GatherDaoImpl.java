package com.ztesoft.iom.gather.dao.impl;

import com.ztesoft.iom.gather.dao.GatherDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * @Description: 信息采集dao实现类
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 12:11
 */
public class GatherDaoImpl extends JdbcDaoSupport implements GatherDao {

    private static Logger log = LogManager.getLogger(GatherDaoImpl.class);

    /**
     * 批量插入采集信息
     *
     * @param paramList
     * @return
     */
    @Override
    public int insertGatherInfoBatch(List paramList) {
        int insertCount = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ot_gather_info(id, gather_type, gather_machine, gather_machine_desc, gather_name, gather_value, gather_time) ");
        sql.append("VALUES(to_char(sysdate, 'yyyymmddhh24miss')||trim(to_char(ot_gather_info_seq.nextval, '0000')), ?, ?, ?, ?, ?, sysdate) ");

        log.debug("批量插入采集信息脚本：" + sql.toString());
        try {
            log.debug("批量插入采集信息参数：" + paramList);
            int[] insertCountTmp = this.getJdbcTemplate().batchUpdate(sql.toString(), paramList);
            for (int i = 0; i < insertCountTmp.length; i++) {
                if (insertCountTmp[i] > 0) {
                    insertCount += insertCountTmp[i];
                } else if (insertCountTmp[i] == -2) {
                    insertCount++;
                }

            }
        } catch (Exception e) {
            log.error("批量插入采集信息异常", e);
        }
        return insertCount;
    }
}
