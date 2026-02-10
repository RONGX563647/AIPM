package com.ai.dev.platform.modules.monitor.service.impl;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.ai.dev.platform.modules.monitor.mapper.MonitorMapper;
import com.ai.dev.platform.modules.monitor.service.MonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonitorServiceImpl extends ServiceImpl<MonitorMapper, Monitor> implements MonitorService {

    @Override
    public IPage<Monitor> getMonitorPage(Integer pageNum, Integer pageSize, Long projectId, String status) {
        Page<Monitor> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Monitor> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(Monitor::getProjectId, projectId);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Monitor::getStatus, status);
        }
        
        wrapper.orderByDesc(Monitor::getUpdateTime);
        
        return page(page, wrapper);
    }

    @Override
    public Monitor addMonitor(Monitor monitor) {
        monitor.setCreateTime(LocalDateTime.now());
        monitor.setUpdateTime(LocalDateTime.now());
        monitor.setResponseTime(0);
        monitor.setStatus("normal");
        
        save(monitor);
        
        return monitor;
    }

    @Override
    public Double getUptime(Long projectId) {
        LambdaQueryWrapper<Monitor> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(Monitor::getProjectId, projectId);
        }
        
        List<Monitor> monitors = list(wrapper);
        
        if (monitors.isEmpty()) {
            return 0.0;
        }
        
        long normalCount = monitors.stream()
            .filter(m -> "normal".equals(m.getStatus()))
            .count();
        
        return (double) normalCount / monitors.size() * 100;
    }

    public void checkMonitorStatus(Monitor monitor, Integer responseTime, Integer slowThreshold) {
        monitor.setResponseTime(responseTime);
        monitor.setUpdateTime(LocalDateTime.now());
        
        if (responseTime == null || responseTime < 0) {
            monitor.setStatus("exception");
        } else if (responseTime > slowThreshold) {
            monitor.setStatus("slow");
        } else {
            monitor.setStatus("normal");
        }
        
        updateById(monitor);
    }
}
