package com.ztesoft.iom.manage.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2017/12/31 0031 - 21:04
 */
public class DirEntryHandler implements ISVNDirEntryHandler {

    private static Logger log = LogManager.getLogger(DirEntryHandler.class);

    public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
        log.info(dirEntry.getRelativePath() + "/" + dirEntry.getName());
    }

}
