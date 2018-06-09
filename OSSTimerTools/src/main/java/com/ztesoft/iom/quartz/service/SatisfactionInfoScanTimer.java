package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.*;
import com.ztesoft.iom.satisfaction.dao.SatisfactionDao;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 客服满意度问卷采集定时任务
 * @author: huang.jing
 * @Date: 2018/4/2 0002 - 16:25
 */
public class SatisfactionInfoScanTimer {

    private static Logger log = LogManager.getLogger(SatisfactionInfoScanTimer.class);
    private static int limitCount = 20;

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("SatisfactionInfoScanTimer入口……");
        FTPUtil ftpUtil = (FTPUtil) BeanFactory.getApplicationContext().getBean("ftpUtil");

        String resultCode = "OK";
        String errorInfo = "";
        boolean rollbackFlag = false;
        Map updateMap = new HashMap();
        Map deleteMap = new HashMap();
        Map qryMap = new HashMap();
        Map insertMap = new HashMap();
        SatisfactionDao satisfactionDaoImpl = (SatisfactionDao) BeanFactory.getApplicationContext().getBean("satisfactionDaoImpl");
        // 待入库文件名
        String avlFileName = "";
        // 待入库数据条数
        int avlFileLength = 0;
        // 实际入库数据条数
        int infoCount = 0;
        // 入库次数
        long dealCount = 0;
        String nowDate = "";

        try {
            // 取前一天的数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
            Calendar cal = Calendar.getInstance();

            if (cal.get(Calendar.HOUR_OF_DAY) <= 5) {
                log.info("当前时间小于5点，不进行问卷入库……");
                return;
            }
            nowDate = sdf1.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
            String chkFileName = sdf.format(cal.getTime()) + ".CHK";

            JSONObject kfSatisfactionFtpInfo = JSONObject.parseObject(ParamConfig.getInstance().getParamValue("kfSatisfactionFtpInfo"));
            String ftpPath = kfSatisfactionFtpInfo.getString("ftpPath");

            // 连接FTP
            if (ftpUtil.connectFTP(kfSatisfactionFtpInfo.getString("userName"), kfSatisfactionFtpInfo.getString("password"), kfSatisfactionFtpInfo.getString("ip"))) {
                JSONObject chkContentJson = ftpUtil.loadFile(ftpPath + chkFileName);
                if (chkContentJson.getBoolean("loadFlag")) {
                    List chkContentList = chkContentJson.getObject("fileContentList", ArrayList.class);
                    if (chkContentList.size() >= 1) {
                        String[] chkContentArr = ((String) chkContentList.get(0)).split("\\|");
                        avlFileName = chkContentArr[0];
                        avlFileLength = Integer.parseInt(chkContentArr[1]);

                        qryMap.put("fileName", avlFileName);
                        // 查询文件名是否已经入库
                        List satisfactionLogList = satisfactionDaoImpl.getSatisfactionLog(qryMap);
                        if (satisfactionLogList.size() == 0) {
                            // 记录入库文件名、数据行数和开始时间
                            insertMap.put("fileName", avlFileName);
                            insertMap.put("avlFileLength", avlFileLength);
                            satisfactionDaoImpl.insertSatisfactionLog(insertMap);
                        } else {
                            Map satisfactionLogMap = (Map) satisfactionLogList.get(0);
                            dealCount = MapUtils.getLongValue(satisfactionLogMap, "DEAL_COUNT", 0);
                            if ("OK".equals(MapUtils.getString(satisfactionLogMap, "RESULT_CODE", ""))) {
                                log.info("问卷数据已经入库成功，跳过本次入库……");
                                return;
                            } else if (dealCount >= limitCount) {
                                log.info("问卷数据已经尝试入库超过" + limitCount + "次，跳过本次入库……");
                                return;
                            }
                        }

                        JSONObject avlContentJson = ftpUtil.loadFile(ftpPath + avlFileName);

                        List avlContentList = avlContentJson.getObject("fileContentList", ArrayList.class);
                        if (avlContentList.size() == avlFileLength) {
                            log.info("CHK文件中文件行数与AVL文件内容行数匹配，校验通过，即将记录问卷信息……");
                            qryMap.clear();
                            qryMap.put("fileName", avlFileName);
                            if (satisfactionDaoImpl.getSatisfactionInfoCount(qryMap) == 0) {
                                // 获取满意答案列表
                                qryMap.clear();
                                qryMap.put("state", "1");
                                List answerList = satisfactionDaoImpl.getSatisfactionAnswer(qryMap);

                                for (int i = 0; i < avlContentList.size(); i++) {
                                    String avlContent = (String) avlContentList.get(i);

                                    String[] avlContentArr = avlContent.split("\\|");
                                    String isSatisfied = "0";

                                    if("".equals(avlContentArr[12])) {
                                        // 答案为空时，不统计满意度
                                        isSatisfied = "";
                                    } else {
                                        for (int j = 0; j < answerList.size(); j++) {
                                            Map answerMap = (Map) answerList.get(j);
                                            if (avlContentArr[12].equals(MapUtils.getString(answerMap, "ANSWER")) && "1".equals(MapUtils.getString(answerMap, "IS_SATISFIED"))) {
                                                isSatisfied = "1";
                                            }
                                        }
                                    }
                                    // 获取序列
                                    qryMap.clear();
                                    qryMap.put("seqName", "om_kf_satisfaction_seq");
                                    String seq = satisfactionDaoImpl.getSatisfactionSeq(qryMap);
                                    // 入库
                                    insertMap.clear();
                                    insertMap.put("id", seq);
                                    insertMap.put("accnbr", avlContentArr[0]);
                                    insertMap.put("summaryDate", avlContentArr[1]);
                                    insertMap.put("questionnaireName", avlContentArr[2]);
                                    insertMap.put("staffId", avlContentArr[3]);
                                    insertMap.put("org", avlContentArr[4]);
                                    insertMap.put("fillDate", avlContentArr[5]);
                                    insertMap.put("isConnnected", avlContentArr[6]);
                                    insertMap.put("isNewAndTimeout", avlContentArr[7]);
                                    insertMap.put("isPaied", avlContentArr[8]);
                                    insertMap.put("operDate", avlContentArr[9]);
                                    insertMap.put("operId", avlContentArr[10]);
                                    insertMap.put("mainQuestion", avlContentArr[11]);
                                    insertMap.put("mainAnswer", avlContentArr[12]);
                                    insertMap.put("questionnaireContent", avlContentArr[13]);
                                    insertMap.put("fileName", avlFileName);
                                    insertMap.put("isSatisfied", isSatisfied);

                                    String relaOrderId = "";
                                    // 查询关联定单
                                    qryMap.clear();
                                    qryMap.put("orderState", "10F");
                                    qryMap.put("accnbr", avlContentArr[0]);
                                    qryMap.put("fillDate", avlContentArr[5]);
                                    qryMap.put("limitDay", "30");
                                    List orderInfoList = satisfactionDaoImpl.getOrderInfo(qryMap);
                                    if (orderInfoList.size() > 0) {
                                        Map orderInfoMap = (Map) orderInfoList.get(0);
                                        relaOrderId = MapUtils.getString(orderInfoMap, "ID");
                                    }
                                    insertMap.put("relaOrderId", relaOrderId);

                                    infoCount += satisfactionDaoImpl.insertSatisfactionInfo(insertMap);
                                }


                                if (infoCount > avlFileLength) {
                                    resultCode = "NOT_EQUAL";
                                    errorInfo = "实际插入数据条数大于应插入数据条数";
                                    rollbackFlag = true;
                                } else if (infoCount < avlFileLength) {
                                    resultCode = "NOT_EQUAL";
                                    errorInfo = "实际插入数据条数小于应插入数据条数";
                                    rollbackFlag = true;
                                }

                                // 记录入库结果
                                updateMap.clear();
                                updateMap.put("insertFileLength", infoCount);
                                updateMap.put("resultCode", resultCode);
                                updateMap.put("errorInfo", errorInfo);
                                updateMap.put("fileName", avlFileName);
                                satisfactionDaoImpl.updateSatisfactionLog(updateMap);

                                // 入库成功，将数据构造成宽表
                                if ("OK".equals(resultCode)) {
                                    setOrderSatisfactionReportInfo(satisfactionDaoImpl, avlFileName);
                                }
                            } else {
                                log.info("问卷内容[" + avlFileName + "]已录入系统，无需重复录入……");
                            }

                        } else {

                            if (avlContentList.size() > avlFileLength) {
                                resultCode = "NOT_EQUAL";
                                errorInfo = "文件数据条数大于应插入数据条数";
                                rollbackFlag = true;
                            } else if (avlContentList.size() < avlFileLength) {
                                resultCode = "NOT_EQUAL";
                                errorInfo = "文件数据条数小于应插入数据条数";
                                rollbackFlag = true;
                            }
                            // 记录入库结果
                            updateMap.clear();
                            updateMap.put("insertFileLength", "0");
                            updateMap.put("resultCode", resultCode);
                            updateMap.put("errorInfo", errorInfo);
                            updateMap.put("fileName", avlFileName);
                            satisfactionDaoImpl.updateSatisfactionLog(updateMap);

                            log.info("CHK文件中文件行数与AVL文件内容行数不匹配，校验不通过……");
                        }
                    } else {
                        log.info("CHK文件行数不足1行，不符合校验规则……");
                    }
                }
            }
        } catch (Exception e) {
            log.error("满意度问卷采集异常", e);

            try {
                resultCode = "ERROR";
                errorInfo = "满意度问卷采集异常：" + e.getMessage();
                rollbackFlag = true;
                // 记录入库结果
                updateMap.clear();
                updateMap.put("insertFileLength", "0");
                updateMap.put("resultCode", resultCode);
                updateMap.put("errorInfo", errorInfo);
                updateMap.put("fileName", avlFileName);
                satisfactionDaoImpl.updateSatisfactionLog(updateMap);
            } catch (Exception e1) {
                log.error("更新满意度采集日志异常", e1);
            }
        } finally {
            ftpUtil.disconnectFTP();

            try {
                // 回滚数据
                if (rollbackFlag) {
                    deleteMap.clear();
                    deleteMap.put("fileName", avlFileName);
                    int deleteCount = satisfactionDaoImpl.deleteSatisfactionInfo(deleteMap);

                    if (deleteCount > 0) {
                        resultCode = "ROLLBACK";
                        errorInfo = "成功回滚数据[" + deleteCount + "]条";

                        // 记录入库结果，不增加计数器
                        updateMap.clear();
                        updateMap.put("insertFileLength", "0");
                        updateMap.put("resultCode", resultCode);
                        updateMap.put("errorInfo", errorInfo);
                        updateMap.put("dealCount", 0);
                        updateMap.put("fileName", avlFileName);
                        satisfactionDaoImpl.updateSatisfactionLog(updateMap);
                    }

                    if(dealCount >= limitCount - 1) {
                        // 发送短信
                        qryMap.clear();
                        qryMap.put("warnId", "200");
                        List warnList = satisfactionDaoImpl.getWarnListById(qryMap);
                        for (int i = 0; i < warnList.size(); i++) {
                            Map warnMap = (Map) warnList.get(i);
                            insertMap.clear();
                            insertMap.put("areaCode", "1100");
                            insertMap.put("recvNum", warnMap.get("RECV_NUM"));
                            // 监控告警短信
                            insertMap.put("sendContent", nowDate + "家宽装机回访满意度调查问卷数据校验不一致，应接收" + avlFileLength + "条，实际接收" + infoCount + "条，系统已做回退处理，请联系IOM管理员核查！");
                            satisfactionDaoImpl.insertSmsLog(insertMap);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("回滚数据异常", e);
            }

        }
        log.info("SatisfactionInfoScanTimer出口……");
    }

    /**
     * 入库客服问卷满意度报表信息
     *
     * @param satisfactionDaoImpl
     * @param avlFileName
     * @throws Exception
     */
    private void setOrderSatisfactionReportInfo(SatisfactionDao satisfactionDaoImpl, String avlFileName) throws Exception {
        log.info("setOrderSatisfactionReportInfo入口……");
        JSONObject indexJson = new JSONObject();
        Map qryMap = new HashMap();

        qryMap.clear();
        qryMap.put("fileName", avlFileName);
        // 按网格维度获取客服问卷满意度报表信息
        List orderSatisfactionReportBaseInfoList = satisfactionDaoImpl.getOrderSatisfactionReportBaseInfo(qryMap);
        for (int i = 0; i < orderSatisfactionReportBaseInfoList.size(); i++) {
            Map orderSatisfactionReportBaseInfoMap = (Map) orderSatisfactionReportBaseInfoList.get(i);
            String summaryDate = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "SUMMARY_DATE");
            String userAreaName = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "USER_AREA_NAME");
            String county = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "COUNTY");
            String exchName = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "EXCH_NAME");
            String grid = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "GRID");
            String issatisfied = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "IS_SATISFIED", "");
            String servicelv = MapUtils.getString(orderSatisfactionReportBaseInfoMap, "SERVICE_LV", "");

            // 普通品质用户数，无论是否填写了满意度答案都统计
            int normalLevelCount = 0;
            // 普通品质用户回答数，只统计填写了满意度答案的
            int normalLevelAnswerCount = 0;
            // 普通品质用户满意数
            int normalLevelSatisfiedCount = 0;
            // 高品质用户数，无论是否填写了满意度答案都统计
            int highLevelCount = 0;
            // 高品质用户回答数，只统计填写了满意度答案的
            int highLevelAnswerCount = 0;
            // 高品质用户满意数
            int highLevelSatisfiedCount = 0;
            if("high".equals(servicelv)) {
                highLevelCount++;
                if(!"".equals(issatisfied)) {
                    if("1".equals(issatisfied)) {
                        highLevelSatisfiedCount++;
                    }
                    highLevelAnswerCount++;
                }

            } else {
                normalLevelCount++;
                if(!"".equals(issatisfied)) {
                    if("1".equals(issatisfied)) {
                        normalLevelSatisfiedCount++;
                    }
                    normalLevelAnswerCount++;
                }
            }

            // 将网格信息构造成md5，作为主键
            String md5 = MD5Util.MD5Encode(summaryDate + userAreaName + county + exchName + grid);
            JSONObject gridJson = new JSONObject();
            if (indexJson.containsKey(md5)) {
                // 索引json中已包含该网格，则更新满意度数据
                gridJson = indexJson.getJSONObject(md5);
                gridJson.put("highLevelCount", gridJson.getIntValue("highLevelCount") + highLevelCount);
                gridJson.put("highLevelAnswerCount", gridJson.getIntValue("highLevelAnswerCount") + highLevelAnswerCount);
                gridJson.put("highLevelSatisfiedCount", gridJson.getIntValue("highLevelSatisfiedCount") + highLevelSatisfiedCount);
                gridJson.put("normalLevelCount", gridJson.getIntValue("normalLevelCount") + normalLevelCount);
                gridJson.put("normalLevelAnswerCount", gridJson.getIntValue("normalLevelAnswerCount") + normalLevelAnswerCount);
                gridJson.put("normalLevelSatisfiedCount", gridJson.getIntValue("normalLevelSatisfiedCount") + normalLevelSatisfiedCount);
            } else {
                // 索引json中未包含该网格，则新建数据并放置如索引json中
                gridJson.put("md5", md5);
                gridJson.put("summaryDate", summaryDate);
                gridJson.put("areaName", userAreaName);
                gridJson.put("county", county);
                gridJson.put("exchName", exchName);
                gridJson.put("gridName", grid);
                gridJson.put("highLevelCount", highLevelCount);
                gridJson.put("highLevelAnswerCount", highLevelAnswerCount);
                gridJson.put("highLevelSatisfiedCount", highLevelSatisfiedCount);
                gridJson.put("normalLevelCount", normalLevelCount);
                gridJson.put("normalLevelAnswerCount", normalLevelAnswerCount);
                gridJson.put("normalLevelSatisfiedCount", normalLevelSatisfiedCount);
            }
            indexJson.put(md5, gridJson);
        }

        // 记录客服满意度宽表信息
        Iterator it = indexJson.keySet().iterator();
        while (it.hasNext()) {
            String keyName = (String) it.next();
            Map paramMap = indexJson.getObject(keyName, Map.class);
            if(satisfactionDaoImpl.getOrderSatisfactionReportInfo(paramMap).size() > 0) {
                satisfactionDaoImpl.updateSatisfactionReportInfo(paramMap);
            } else {
                satisfactionDaoImpl.insertSatisfactionReportInfo(paramMap);
            }
        }
        log.info("setOrderSatisfactionReportInfo出口……");
    }
}
