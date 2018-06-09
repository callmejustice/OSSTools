package com.ztesoft.iom.manage.svn.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.ParamConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @Description: SVN日志业务类
 * @author: huang.jing
 * @Date: 2018/1/1 0001 - 17:00
 */
public class SVNLogService {

    private static Logger log = LogManager.getLogger(SVNLogService.class);

    public boolean doSvnLogin(JSONObject logonJson) {
        log.info("doSvnLog入口，即将使用[" + logonJson.getString("userName") + "]进行登录svn服务器");
        boolean loginFlag = false;
        // 安装版本库
        DAVRepositoryFactory.setup();
        // 定义svn版本库的URL。
        SVNURL repositoryURL = null;
        // 定义版本库。
        SVNRepository repository = null;

        // 实例化版本库类
        try {
            //获取SVN的URL。
            repositoryURL = SVNURL.parseURIEncoded(ParamConfig.getInstance().getParamValue("svnUrl"));
            //根据URL实例化SVN版本库。
            repository = SVNRepositoryFactory.create(repositoryURL);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(logonJson.getString("userName"), logonJson.getString("password").toCharArray());
            repository.setAuthenticationManager(authManager);
            long latestRevision = repository.getLatestRevision();
            log.info("获取svn最新版本号成功，最新版本号为：" + latestRevision);
            loginFlag = true;
        } catch (Exception e) {
            log.error("获取svn最新版本库异常", e);
        }
        return loginFlag;
    }

    /**
     * 初始化库
     */
    private static void setupLibrary() {
        /*
         * For using over http:// and https://
         */
        DAVRepositoryFactory.setup();
        /*
         * For using over svn:// and svn+xxx://
         */
        SVNRepositoryFactoryImpl.setup();
        /*
         * For using over file:///
         */
        FSRepositoryFactory.setup();
    }

}
