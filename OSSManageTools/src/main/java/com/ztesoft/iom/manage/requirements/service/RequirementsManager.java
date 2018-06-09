package com.ztesoft.iom.manage.requirements.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.requirements.dao.RequirementsOrderDao;
import com.ztesoft.iom.manage.requirements.dao.impl.RequirementsOrderDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/1/23 0023 - 16:02
 */
public class RequirementsManager {

    private static Logger log = LogManager.getLogger(RequirementsManager.class);
    private RequirementsOrderDao requirementsOrderDao;

    /**
     * 导入需求EOMS单
     *
     * @param importJson
     * @return
     */
    public JSONObject importEOMSOrder(JSONObject importJson) {
        log.info("importEOMSOrder入口……");
        JSONObject returnJson = new JSONObject();
        int successCount = 0;
        int failureCount = 0;
        boolean importFlag = false;
        StringBuffer errorInfo = new StringBuffer();

        try {
            requirementsOrderDao = (RequirementsOrderDaoImpl) BeanFactory.getApplicationContext().getBean("requirementsOrderDaoImpl");
            List excelList = importJson.getObject("excelList", List.class);
            for (Iterator iterator = excelList.iterator(); iterator.hasNext(); ) {
                String[] cells = (String[]) iterator.next();
                try {
                    Map qryMap = new HashMap();
                    qryMap.put("seqName", "ot_requirements_eoms_order_seq");
                    String seq = requirementsOrderDao.getRequirementsModuleSeq(qryMap);

                    // String[]转列表用必须使用new ArrayList进行初始化，否则没有add方法
                    ArrayList paramList = new ArrayList(Arrays.asList(cells));
                    paramList.add(seq);

                    qryMap.clear();
                    String requirementsCode = cells[0];
                    qryMap.put("requirementsCode", requirementsCode);
                    // 先根据需求单号查询是否存在需求单号，存在则更新，不存在则插入
                    if(requirementsOrderDao.getRequirementsEOMSOrderListCount(qryMap) > 0) {
                        // 将队列第一个参数移出来，放到队列最后一个
                        paramList.remove(0);
                        paramList.add(requirementsCode);
                        requirementsOrderDao.updatetRequirementsEOMSOrderAllCol(paramList);
                    } else {
                        requirementsOrderDao.insertRequirementsEOMSOrderAllCol(paramList);
                    }
                    successCount ++;
                } catch (Exception e) {
                    failureCount ++;
                    errorInfo.append("[" + cells[0] + "]导入异常：" + e.getMessage() + "\r\n");
                }
            }
            importFlag = true;
        } catch (Exception e) {
            log.error("导入需求EOMS单异常", e);
            errorInfo.append("导入需求EOMS单异常：" + e.getMessage() + "\r\n");
        } finally {
            returnJson.put("importFlag", importFlag);
            returnJson.put("successCount", successCount);
            returnJson.put("failureCount", failureCount);
            returnJson.put("errorInfo", errorInfo.toString());
        }

        log.info("importEOMSOrder入口……");
        return returnJson;
    }

}
