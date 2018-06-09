package com.ztesoft.iom.manage.rest.controller.view;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 系统监控模块视图控制器
 * @author: huang.jing
 * @Date: 2018/2/11 0011 - 17:55
 */
@RestController
@RequestMapping("/controller/views/monitor")
public class MonitorViewController {

    /**
     * 接口监控
     *
     * @return
     */
    @RequestMapping("/interfaceMonitor.do")
    public ModelAndView interfaceMonitor() {
        return new ModelAndView("monitor/interfaceMonitor");
    }

    /**
     * 应用连接数监控
     *
     * @return
     */
    @RequestMapping("/portMonitor.do")
    public ModelAndView portMonitor() {
        return new ModelAndView("monitor/portMonitor");
    }
}
