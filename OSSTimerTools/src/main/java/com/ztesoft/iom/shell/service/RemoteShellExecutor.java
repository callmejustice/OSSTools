package com.ztesoft.iom.shell.service;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @Description: shell脚本执行类
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 9:45
 */
public class RemoteShellExecutor {

    private static Logger log = LogManager.getLogger(RemoteShellExecutor.class);

    private Connection conn;
    /**
     * 远程机器IP
     */
    private String ip;
    /**
     * 用户名
     */
    private String osUsername;
    /**
     * 密码
     */
    private String password;
    private String charset = Charset.defaultCharset().toString();
    // 等待命令执行时间
    private static final int TIME_OUT = 1000 * 1 * 10;

    /**
     * 构造函数
     *
     * @param ip
     * @param usr
     * @param pasword
     */
    public RemoteShellExecutor(String ip, String usr, String pasword) throws Exception {
        this.ip = ip;
        this.osUsername = usr;
        this.password = pasword;

        // 登录远程机
        try {
            if (login()) {
                log.info("登录[" + ip + "]成功……");
            } else {
                throw new Exception("登录[" + ip + "]失败……"); // 自定义异常类 实现略
            }
        } catch (Exception e) {
            throw new Exception("登录[" + ip + "]异常……", e);
        }
    }


    /**
     * 登录
     *
     * @return
     * @throws IOException
     */
    private boolean login() throws IOException {
        conn = new Connection(ip);
        conn.connect();
        return conn.authenticateWithPassword(osUsername, password);
    }

    /**
     * 执行脚本
     * 每个session打开后只能执行一条命令，如果需要执行多条命令可以多次调用该方法
     *
     * @param cmds
     * @return
     * @throws Exception
     */
    public HashMap exec(String cmds) {
        InputStream stdOut = null;
        InputStream stdErr = null;
        HashMap returnMap = new HashMap();
        String outStr = "";
        String outErr = "";
        int ret = -1;
        Session session = null;
        try {
            log.info("即将执行命令：" + cmds);
            // Open a new {@link Session} on this connection
            session = conn.openSession();
            log.info("获取session成功，即将执行命令……");
            // Execute a command on the remote machine.
            session.execCommand(cmds);
            log.info("执行命令成功……");
            // 脚本执行结果
            stdOut = new StreamGobbler(session.getStdout());
            outStr = processStream(stdOut, charset);
            // 脚本执行错误信息
            stdErr = new StreamGobbler(session.getStderr());
            outErr = processStream(stdErr, charset);

            // 避免执行命令后卡住，需要设置进程超时时间
            session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
            log.info("执行结果获取成功……");
            ret = session.getExitStatus();
            log.info("session退出状态获取成功……");
        } catch (Exception e) {
            log.error("执行脚本异常", e);
        } finally {
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);

            returnMap.put("returnCode", ret + "");
            returnMap.put("stdout", outStr);
            returnMap.put("stderr", outErr);

            // 关闭通道
            if (session != null) {
                session.close();
            }
        }
        return returnMap;
    }

    /**
     * 关闭当前登录连接
     */
    public void closeConnection() {
        if (conn != null) {
            conn.close();
        }
        log.info("关闭[" + ip + "]连接成功……");
    }

    /**
     * @param in
     * @param charset
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }
}
