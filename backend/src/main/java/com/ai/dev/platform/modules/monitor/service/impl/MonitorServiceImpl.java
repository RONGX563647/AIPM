package com.ai.dev.platform.modules.monitor.service.impl;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.ai.dev.platform.modules.monitor.mapper.MonitorMapper;
import com.ai.dev.platform.modules.monitor.service.MonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        
        wrapper.orderByDesc(Monitor::getCreateTime);
        
        return page(page, wrapper);
    }

    @Override
    public Monitor addMonitor(Monitor monitor) {
        monitor.setCreateTime(LocalDateTime.now());
        
        BigDecimal metricValue = monitor.getMetricValue();
        BigDecimal threshold = monitor.getThreshold();
        
        if (metricValue != null && threshold != null) {
            int compare = metricValue.compareTo(threshold);
            if (compare > 0) {
                monitor.setStatus("critical");
                monitor.setAlertMessage(monitor.getMetricName() + "指标值" + metricValue + "超过阈值" + threshold);
            } else if (compare == 0) {
                monitor.setStatus("warning");
                monitor.setAlertMessage(monitor.getMetricName() + "指标值" + metricValue + "达到阈值" + threshold);
            } else {
                monitor.setStatus("normal");
                monitor.setAlertMessage(null);
            }
        } else {
            monitor.setStatus("normal");
            monitor.setAlertMessage(null);
        }
        
        save(monitor);
        
        return monitor;
    }
}
