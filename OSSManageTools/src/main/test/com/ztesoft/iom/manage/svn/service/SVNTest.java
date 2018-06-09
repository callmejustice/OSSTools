package com.ztesoft.iom.manage.svn.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2017/12/31 0031 - 22:12
 */
public class SVNTest {

    private static Logger log = LogManager.getLogger(SVNTest.class);

    // Parameters for doLog()
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    String[] paths = { "branches/programmer/IOM1" };
    SVNRevision pegRevision = SVNRevision.create( 0l );
    SVNRevision startRevision = SVNRevision.create( 0l );
    SVNRevision endRevision = SVNRevision.HEAD;
    boolean stopOnCopy = false;
    boolean discoverChangedPaths = true;
    long limit = 9999l;

    // Log event handler
    ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
        /**
         * This method will process when doLog() is done
         */
        public void handleLogEntry( SVNLogEntry logEntry ) throws SVNException {
            log.info("------------处理日志记录开始----------------");
            log.info( "Author: " + logEntry.getAuthor() );
            log.info( "Date: " + sdf.format(logEntry.getDate()));
            log.info( "Message: " + logEntry.getMessage() );
            log.info( "Revision: " + logEntry.getRevision() );
            Map changeMap = logEntry.getChangedPaths();
            Iterator it = changeMap.keySet().iterator();
            while (it.hasNext()) {
                String keyName = (String) it.next();
                SVNLogEntryPath svnLogEntryPath = (SVNLogEntryPath) MapUtils.getObject(changeMap, keyName);

                log.info("变动文件:" + keyName + "；变动类型：" + svnLogEntryPath.getType() + "；文件路径：" + svnLogEntryPath.getPath());
            }
//            log.info( "ChangedPaths: " + logEntry.getChangedPaths() );
            log.info("------------处理日志记录结束----------------");
        }
    };

    public void test(JSONObject logonJson) throws SVNException {
        setupLibrary();
        //定义svn版本库的URL。
        SVNURL repositoryURL = null;
        //定义版本库。
        SVNRepository repository = null;
        /*
         * 实例化版本库类
         * */
        try {
            //获取SVN的URL。
//            repositoryURL = SVNURL.parseURIEncoded("https://Saitor-PC/svn/testSvnServer/testProject1/trunk");
            repositoryURL = SVNURL.parseURIEncoded("http://10.185.48.230:8095/svn/gx_iom/");
            //根据URL实例化SVN版本库。
            repository = SVNRepositoryFactory.create(repositoryURL);
        } catch (Exception e) {
            log.error("创建版本库异常", e);
        }
//        /*
//         * 对版本库设置认证信息。
//         */
//        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(logonJson.getString("userName"), logonJson.getString("password"));


        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(logonJson.getString("userName"), logonJson.getString("password").toCharArray());
        repository.setAuthenticationManager(authManager);

        /*
         * 获得版本库的最新版本树
         */
        long latestRevision = -1;
        try {
            latestRevision = repository.getLatestRevision();
        } catch (Exception e) {
            log.error("获取最新版本号异常", e);
        }

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, logonJson.getString("userName"), logonJson.getString("password"));
        SVNLogClient logClient = svnClientManager.getLogClient();
        try {
            startRevision = SVNRevision.create( 800l );

            logClient.doLog(repositoryURL, paths, pegRevision, startRevision, endRevision, stopOnCopy, discoverChangedPaths, limit, handler );
        } catch ( Exception e ) {
            log.error("获取更新记录错误", e);
        }

//        SVNClientManager svnClientManager = SVNClientManager.createDefaultAuthenticationManager((logonJson.getString("userName"), logonJson.getString("password"));
//        SVNURL repositoryOptUrl = SVNURL.parseURIEncoded();
////        svnClientManager.createRepository(repositoryOptUrl, true);
//        SVNLogClient logClient = svnClientManager.getLogClient();
////        DirEntryHandler handler = new DirEntryHandler();
//        // 将svn检出到本地的目录
////            File to = new File("f:\\test"); // 本地目录
////            logClient.doList(to, SVNRevision.HEAD, SVNRevision.HEAD, false,  SVNDepth.INFINITY, 1, handler);
////        logClient.doList(repositoryOptUrl, SVNRevision.HEAD, SVNRevision.HEAD, false, true, handler); // 列出当前svn地址的目录，对每个文件进行处理
////            doLog()	'svn log'
////            doList()	'svn list'
////            doAnnotate()	'svn blame'
//
//        // Do log
//        try {
//            logClient.doLog(repositoryOptUrl, paths, pegRevision, startRevision, endRevision, stopOnCopy, discoverChangedPaths, limit, handler );
//        } catch ( Exception e ) {
//            log.error("获取更新记录错误", e);
//        }

        // 对比差异
//            SVNURL diffFileUrl = SVNURL.parseURIEncoded(ParamConfig.getInstance().getParamValue("svnUrl") + "/test.txt");
//            BufferedOutputStream result =new BufferedOutputStream(new FileOutputStream("F:/test2.txt"));
//            svnClientManager.getDiffClient().doDiff(diffFileUrl, SVNRevision.HEAD, SVNRevision.BASE, SVNRevision.HEAD, SVNDepth.INFINITY, true, result);
    }

    /*
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


    public static void main(String args[]) {
        SVNTest svnTest = new SVNTest();
        JSONObject logonJson = new JSONObject();
        logonJson.put("userName", "huangjing");
        logonJson.put("password", "1234");
        try {
            svnTest.test(logonJson);
        } catch (Exception e) {
            log.error("SVN操作执行异常", e);
        }

    }
}
