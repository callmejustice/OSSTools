package com.ztesoft.iom.quartz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.gather.dao.impl.GatherDaoImpl;
import com.ztesoft.iom.shell.service.RemoteShellExecutor;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: linux主机信息采集定时任务
 * @author: huang.jing
 * @Date: 2018/2/12 0012 - 10:29
 */
public class LinuxInfoGatherTimer {

    private static Logger log = LogManager.getLogger(LinuxInfoGatherTimer.class);

    /**
     * 定时任务入口，配置在spring-quartz.xml中
     */
    public void execute() {
        log.info("LinuxInfoGatherTimer入口……");
        gatherInfoByName("portMonitorInfo");
        log.info("LinuxInfoGatherTimer出口……");
    }

    private void gatherInfoByName(String monitorInfo) {
        log.info("gatherInfoByName入口……");
        // 从缓存中获取需要监控的主机信息
        JSONArray gatherInfoList = JSONArray.parseArray(ParamConfig.getInstance().getParamValue(monitorInfo));
        GatherDaoImpl gatherDaoImpl = (GatherDaoImpl) BeanFactory.getApplicationContext().getBean("gatherDaoImpl");
        for (int i = 0; i < gatherInfoList.size(); i++) {
            try {
                JSONObject gatherInfo = gatherInfoList.getJSONObject(i);
                // 创建远程连接
                RemoteShellExecutor executor = new RemoteShellExecutor(gatherInfo.getString("ip"), gatherInfo.getString("account"), gatherInfo.getString("password"));
                JSONArray listenList = gatherInfo.getJSONArray("listenList");
                List paramList = new ArrayList();
                for (int j = 0; j < listenList.size(); j++) {
                    JSONObject listenInfo = listenList.getJSONObject(j);
                    if("port".equals(listenInfo.getString("listenType"))) {
                        // 执行命令
                        String listenCmd = ParamConfig.getInstance().getParamValue("listenCmd");
                        listenCmd = listenCmd.replaceAll("\\[port\\]", listenInfo.getString("port"));
                        HashMap listenCmdInfoMap = executor.exec(listenCmd);
                        String listenCmdInfo = MapUtils.getString(listenCmdInfoMap, "stdout");
                        String listenCmdError = MapUtils.getString(listenCmdInfoMap, "stderr");
                        if(!"".equals(listenCmdError)) {
                            log.error("主机[" + gatherInfo.getString("ip") + "]采集端口[" + listenInfo.getString("port") + "]连接信息失败：" + listenCmdInfoMap);
                        } else {
                            // 根据换行符进行字符串截断
                            String[] listenCmdInfoArr = listenCmdInfo.split("\n");
                            for (int k = 0; k < listenCmdInfoArr.length; k++) {
                                // 根据空格符进行字符串截断
                                String[] poerListenInfo = listenCmdInfoArr[k].split("\\s+");
                                if(poerListenInfo.length > 1) {
                                    List paramListChild = new ArrayList();
                                    paramListChild.add("portListen");
                                    paramListChild.add(gatherInfo.getString("ip") + ":" + listenInfo.getString("port"));
                                    paramListChild.add(listenInfo.getString("desc"));
                                    paramListChild.add(poerListenInfo[0]);
                                    paramListChild.add(poerListenInfo[1]);
                                    paramList.add(paramListChild.toArray());
                                }
                            }
                        }
                    }
                }
                // 关闭连接
                executor.closeConnection();
                // 批量插入
                gatherDaoImpl.insertGatherInfoBatch(paramList);
            } catch (Exception e) {
                log.error("登录远程机执行脚本异常", e);
            }
        }
        log.info("gatherInfoByName出口……");
    }

}
