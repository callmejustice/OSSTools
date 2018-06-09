package com.ztesoft.iom.quartz.service;

import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.svn.dao.SvnLogDao;
import com.ztesoft.iom.svn.dao.impl.SvnLogDaoImpl;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: SVN日志信息定时任务
 * @author: huang.jing
 * @Date: 2018/1/18 0018 - 11:51
 */
public class SvnLogListTimer {
    private static Logger log = LogManager.getLogger(SvnLogListTimer.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SVNRevision pegRevision = SVNRevision.create(0l);
    private SVNRevision startRevision = SVNRevision.create(0l);
    private SVNRevision endRevision = SVNRevision.HEAD;
    private long checkEndRevision = 0l;
    private boolean stopOnCopy = false;
    private boolean discoverChangedPaths = true;
    private long limit = 9999l;
    private SvnLogDao svnLogDao;

    // Log event handler
    ISVNLogEntryHandler handler = new ISVNLogEntryHandler() {
        /**
         * This method will process when doLog() is done
         */
        public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
            log.info("handleLogEntry入口……");
            String repository = "IOM_LOCAL";
            long revision = logEntry.getRevision();
            String author = logEntry.getAuthor();
            String createDate = sdf.format(logEntry.getDate());
            String message = logEntry.getMessage();

            try {
                // 遍历列表，先判断变更记录中是否有需要插入的工程
                Map changeMap = logEntry.getChangedPaths();
                Iterator it = changeMap.keySet().iterator();
                boolean insertFlag = false;
                while (it.hasNext()) {
                    String keyName = (String) it.next();
                    SVNLogEntryPath svnLogEntryPath = (SVNLogEntryPath) MapUtils.getObject(changeMap, keyName);
                    String path = svnLogEntryPath.getPath();

                    if (path.indexOf("/IomInterface/") > -1) {
                        insertFlag = true;
                    } else if (path.indexOf("/IOM1/") > -1) {
                        insertFlag = true;
                    }
                }

                Map qryMap = new HashMap();
                qryMap.put("revision", revision);
                qryMap.put("repository", repository);
                int revisionCount = svnLogDao.getRevisionCount(qryMap);

                // 作者不是admin、且变更列表中包含指定的项目且版本号不存在库表中
                if (insertFlag && revisionCount == 0 && !"admin".equals(author)) {
                    qryMap.clear();
                    qryMap.put("seqName", "ot_svn_log_seq");
                    String seq = svnLogDao.getSvnLogSeq(qryMap);

                    Map insertMap = new HashMap();
                    insertMap.put("svnLogId", seq);
                    insertMap.put("revision", revision);
                    insertMap.put("repository", repository);
                    insertMap.put("author", author);
                    insertMap.put("message", message);
                    insertMap.put("createDate", createDate);

                    svnLogDao.insertSvnLog(insertMap);

                    it = changeMap.keySet().iterator();
                    while (it.hasNext()) {
                        String keyName = (String) it.next();
                        SVNLogEntryPath svnLogEntryPath = (SVNLogEntryPath) MapUtils.getObject(changeMap, keyName);
                        String path = svnLogEntryPath.getPath();
                        String project = "";

                        if (path.indexOf("/IomInterface/") > -1) {
                            project = "JK";
                            path = path.substring(path.indexOf("/IomInterface/") + 14);
                        } else if (path.indexOf("/IOM1/") > -1) {
                            project = "IOM";
                            path = path.substring(path.indexOf("/IOM1/") + 6);
                        }

                        if (!"".equals(project)) {
                            qryMap.clear();
                            qryMap.put("seqName", "ot_svn_changelist_seq");
                            String changelistSeq = svnLogDao.getSvnLogSeq(qryMap);

                            insertMap.clear();
                            insertMap.put("changelistId", changelistSeq);
                            insertMap.put("svnLogId", seq);
                            insertMap.put("project", project);
                            insertMap.put("path", path);
                            insertMap.put("type", svnLogEntryPath.getType());

                            svnLogDao.insertSvnChangeList(insertMap);
                        }
                    }
                } else {
                    log.info("版本号[" + revision + "]不符合插入条件，跳过……");
                }
            } catch (Exception e) {
                log.error("处理svn日志信息异常", e);
            }


            checkEndRevision = logEntry.getRevision();
            log.info("handleLogEntry出口……");
        }
    };

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("SvnLogListTimer入口……");
        setupLibrary();
        //定义svn版本库的URL。
        SVNURL repositoryURL = null;
        //定义版本库。
        SVNRepository repository = null;
        /*
         * 实例化版本库类
         * */
        try {
            repositoryURL = SVNURL.parseURIEncoded(ParamConfig.getInstance().getParamValue("svnUrl"));
            //根据URL实例化SVN版本库。
            repository = SVNRepositoryFactory.create(repositoryURL);
        } catch (Exception e) {
            log.error("创建版本库异常", e);
        }

        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
        SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, ParamConfig.getInstance().getParamValue("svnLoginName"), ParamConfig.getInstance().getParamValue("svnPassword"));
        SVNLogClient logClient = svnClientManager.getLogClient();
        try {
            String[] paths = {ParamConfig.getInstance().getParamValue("svnPaths")};

            svnLogDao = (SvnLogDaoImpl) BeanFactory.getApplicationContext().getBean("svnLogDaoImpl");
            Map qryMap = new HashMap();
            qryMap.put("paramConfigName", "START_REVISION");
            Long startRevisionConfig = Long.parseLong(svnLogDao.getParamConfigValue(qryMap));
            startRevision = SVNRevision.create(startRevisionConfig);

            logClient.doLog(repositoryURL, paths, pegRevision, startRevision, endRevision, stopOnCopy, discoverChangedPaths, limit, handler);

            log.info("本次获取的结束版本为：" + checkEndRevision);
            Map updateMap = new HashMap();
            updateMap.put("paramConfigName", "START_REVISION");
            updateMap.put("paramConfigValue", checkEndRevision + "");
            svnLogDao.updateParamConfigValue(updateMap);

        } catch (Exception e) {
            log.error("获取更新记录错误", e);
        }

        log.info("SvnLogListTimer出口……");
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
}
