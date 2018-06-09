package com.ztesoft.iom.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Description: ftp处理类
 * @author: huang.jing
 * @Date: 2018/4/2 0002 - 16:53
 */
public class FTPUtil {

    private static Logger log = LogManager.getLogger(FTPUtil.class);
    private ChannelSftp sftp;
    private Session sshSession;

    /**
     * 连接FTP
     *
     * @param username
     * @param password
     * @param ip
     */
    public boolean connectFTP(String username, String password, String ip) {
        log.info("connectFTP入口……[" + username + "][" + ip + "]");
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
        log.info("connectFTP出口……");
        return connectFlag;
    }

    /**
     * 读取文件内容
     * @param ftpFileName
     * @return
     */
    public JSONObject loadFile(String ftpFileName) {
        log.info("loadFile入口……");
        JSONObject returnJson = new JSONObject();
        boolean loadFlag = false;

        try {
            List fileContentList = new ArrayList();
            log.info("即将读取[" + ftpFileName + "]……");
            InputStream is = sftp.get(ftpFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = in.readLine()) != null){
                fileContentList.add(line);
            }
            returnJson.put("fileContentList", fileContentList);
            loadFlag = true;
            log.info("读取文件内容结束……");
        } catch (Exception e) {
            log.error("读取文件异常", e);
        } finally {
            returnJson.put("loadFlag", loadFlag);
        }
        log.info("loadFile出口……");
        return returnJson;
    }

    public void disconnectFTP() {
        log.info("disconnectFTP入口……");
        // 断开连接
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
        log.info("disconnectFTP出口……");
    }

}
