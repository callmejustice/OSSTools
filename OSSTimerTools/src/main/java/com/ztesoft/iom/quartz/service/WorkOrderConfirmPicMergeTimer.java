package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.order.dao.WorkOrderDao;
import com.ztesoft.iom.order.service.CreateBigImage;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 外线施工签名图片合并定时任务
 * @author: huang.jing
 * @Date: 2018/3/15 0015 - 15:45
 */
public class WorkOrderConfirmPicMergeTimer {

    private static Logger log = LogManager.getLogger(WorkOrderConfirmPicMergeTimer.class);
    private ChannelSftp sftp;
    private Session sshSession;

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("WorkOrderConfirmPicMergeTimer入口……");

        try {
            HashMap param = new HashMap();
            WorkOrderDao WorkOrderDao = (WorkOrderDao) BeanFactory.getApplicationContext().getBean("workOrderDaoImpl");
            List notMergeWorkOrderConfirmList = WorkOrderDao.getNotMergeWorkOrderConfirmList(param);
            log.info("外线施工待合并签名图片的定单数：" + notMergeWorkOrderConfirmList.size());

            CreateBigImage cbi = new CreateBigImage();
            JSONObject workOrderConfirmPicFtpInfo = JSONObject.parseObject(ParamConfig.getInstance().getParamValue("workOrderConfirmPicFtpInfo"));
            String localPath = workOrderConfirmPicFtpInfo.getString("localPath");
            // 正则表达式，用于匹配文件路径中是否有非法字符
            String pattern = "[*]";
            Pattern r = Pattern.compile(pattern);
            if (connectFTP(workOrderConfirmPicFtpInfo.getString("userName"), workOrderConfirmPicFtpInfo.getString("password"), workOrderConfirmPicFtpInfo.getString("ip"))) {
                for (int i = 0; i < notMergeWorkOrderConfirmList.size(); i++) {
                    String orderId = "";
                    String orderCode = "";
                    try {
                        Map notMergeWorkOrderConfirmMap = (Map) notMergeWorkOrderConfirmList.get(i);
                        orderId = MapUtils.getString(notMergeWorkOrderConfirmMap, "ORDER_ID");
                        orderCode = "";
                        param.put("orderId", orderId);
                        List workOrderConfirmList = WorkOrderDao.getWorkOrderConfirmList(param);
                        if (workOrderConfirmList.size() == 3) {
                            ArrayList widthMergePicList = new ArrayList();
                            ArrayList heightMergePicList = new ArrayList();
                            String widthMergeFileName = "";
                            String heightMergeFileName = "";
                            String ftpFilePath = "";

                            // 客户签名在左边，先把客户签名放入拼接列表
                            for (int j = 0; j < workOrderConfirmList.size(); j++) {
                                Map workOrderConfirmMap = (Map) workOrderConfirmList.get(j);
                                orderCode = MapUtils.getString(workOrderConfirmMap, "ORDER_CODE");

                                String ftpFileName = MapUtils.getString(workOrderConfirmMap, "FILE_NAME_PATH");
                                ftpFilePath = ftpFileName.substring(0, ftpFileName.lastIndexOf("/") + 1);
                                log.debug("即将获取文件：" + ftpFileName);
                                InputStream is = sftp.get(ftpFileName);
                                String fileName = MapUtils.getString(workOrderConfirmMap, "FILE_NAME");
                                // 客户签名压缩成指定尺寸，其它图片不变
                                if (fileName.indexOf("_custSign_") > -1) {
                                    // 注意校验localPath是否存在，如果不存在需要先新建目录
                                    cbi.changeImage(is, localPath + fileName, 500, 466);
                                    widthMergePicList.add(localPath + fileName);
                                    // 移除出列表
                                    workOrderConfirmList.remove(j);
                                    break;
                                }
                            }
                            // 将装维人员签名和工单信息图片放入列表
                            for (int j = 0; j < workOrderConfirmList.size(); j++) {
                                Map workOrderConfirmMap = (Map) workOrderConfirmList.get(j);
                                orderCode = MapUtils.getString(workOrderConfirmMap, "ORDER_CODE");

                                String ftpFileName = MapUtils.getString(workOrderConfirmMap, "FILE_NAME_PATH");
                                ftpFilePath = ftpFileName.substring(0, ftpFileName.lastIndexOf("/") + 1);
                                InputStream is = sftp.get(ftpFileName);
                                String fileName = MapUtils.getString(workOrderConfirmMap, "FILE_NAME");
                                // 装维人员签名压缩成指定尺寸，其它图片不变
                                if (fileName.indexOf("_constructType_") > -1) {
                                    cbi.changeImage(is, localPath + fileName, 500, 466);
                                    widthMergePicList.add(localPath + fileName);

                                    // 设置合并后的图片名
                                    widthMergeFileName = fileName.substring(0, fileName.indexOf("_constructType_")) + "_widthMerge.jpg";
                                    heightMergeFileName = fileName.substring(0, fileName.indexOf("_constructType_")) + "_heightMerge.jpg";
                                } else {
                                    cbi.changeImage(is, localPath + fileName, 1000, 930);
                                    heightMergePicList.add(localPath + fileName);
                                }
                            }

                            // 横向合并图片
                            cbi.mergeWidth(widthMergePicList, "jpg", localPath + widthMergeFileName);
                            // 纵向合并
                            heightMergePicList.add(localPath + widthMergeFileName);
                            cbi.mergeHeight(heightMergePicList, "jpg", localPath + heightMergeFileName);

                            // 使用输入流读取文件
                            File heightMergeFile = new File(localPath + heightMergeFileName);
                            InputStream isMerge = new FileInputStream(heightMergeFile);
                            // 上传至指定目录
                            log.info("即将转移至FTP目录：" + ftpFilePath);
                            sftp.cd(ftpFilePath);
                            Matcher m = r.matcher(heightMergeFileName);
                            if (m.find()) {
                                log.error("文件名[" + heightMergeFileName + "]中包含非法字符，即将进行替换……");
                                heightMergeFileName = heightMergeFileName.replaceAll("\\*", "@");
                            }

                            sftp.put(isMerge, heightMergeFileName);

                            // 记录合并图片信息
                            HashMap insertParam = new HashMap();
                            insertParam.put("orderId", orderId);
                            insertParam.put("orderCode", orderCode);
                            insertParam.put("fileName", heightMergeFileName);
                            insertParam.put("fileNamePath", ftpFilePath + heightMergeFileName);
                            WorkOrderDao.insertWorkOrderConfirmMergePicInfo(insertParam);


                            // 删除文件
                            File deleteFile = new File(localPath + heightMergeFileName);
                            deleteFile.delete();

                            for (int j = 0; j < widthMergePicList.size(); j++) {
                                String fileName = (String) widthMergePicList.get(j);
                                deleteFile = new File(fileName);
                                deleteFile.delete();
                            }

                            for (int j = 0; j < heightMergePicList.size(); j++) {
                                String fileName = (String) heightMergePicList.get(j);
                                deleteFile = new File(fileName);
                                deleteFile.delete();
                            }
                        }
                    } catch (Exception e) {
                        log.error("合并图片异常", e);

                        // 获取图片失败时也记录合并信息，避免重复扫描
                        if(!"".equals(orderId)) {
                            // 记录合并图片信息
                            HashMap insertParam = new HashMap();
                            insertParam.put("orderId", orderId);
                            insertParam.put("orderCode", orderCode);
                            insertParam.put("fileName", "error");
                            insertParam.put("fileNamePath", "error");
                            WorkOrderDao.insertWorkOrderConfirmMergePicInfo(insertParam);
                        }

                        // 断开连接
                        if (sftp != null && sftp.isConnected()) {
                            sftp.disconnect();
                        }
                        if (sshSession != null && sshSession.isConnected()) {
                            sshSession.disconnect();
                        }
                        // 重登陆
                        connectFTP(workOrderConfirmPicFtpInfo.getString("userName"), workOrderConfirmPicFtpInfo.getString("password"), workOrderConfirmPicFtpInfo.getString("ip"));
                    }
                }
            } else {
                log.info("连接FTP失败，不对定单进行合并");
            }

        } catch (Exception e) {
            log.error("外线施工签名图片合并定时任务异常", e);
        } finally {
            // 断开连接
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
            if (sshSession != null && sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }

        log.info("WorkOrderConfirmPicMergeTimer出口……");
    }

    /**
     * 连接FTP
     *
     * @param username
     * @param password
     * @param ip
     */
    public boolean connectFTP(String username, String password, String ip) {
        log.info("initConnectFTP入口……[" + username + "][" + ip + "]");
        boolean connectFlag = false;
        try {
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, ip, 22);
            log.debug("创建SSH会话");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            log.debug("SSH会话连接成功");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            if (sftp.isConnected()) {
                log.debug("[" + ip + "]连接成功");
                connectFlag = true;
            } else {
                log.debug("[" + ip + "]连接失败");
            }
        } catch (Exception e) {
            log.error("连接FTP异常", e);
        }
        log.info("initConnectFTP出口……");
        return connectFlag;
    }
}
