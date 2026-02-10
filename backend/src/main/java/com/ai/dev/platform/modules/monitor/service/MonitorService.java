package com.ai.dev.platform.modules.monitor.service;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MonitorService extends IService<Monitor> {
    IPage<Monitor> getMonitorPage(Integer pageNum, Integer pageSize, Long projectId, String status);

    Monitor addMonitor(Monitor monitor);
}
