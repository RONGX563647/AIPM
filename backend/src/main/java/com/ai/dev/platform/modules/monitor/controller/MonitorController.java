package com.ai.dev.platform.modules.monitor.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.ai.dev.platform.modules.monitor.service.MonitorService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "监控管理")
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @Operation(summary = "新增监控记录")
    @PostMapping("/add")
    public Result<Monitor> addMonitor(@RequestBody Monitor monitor) {
        Monitor result = monitorService.addMonitor(monitor);
        if (result == null) return Result.error("新增失败");
        return Result.ok(result);
    }

    @Operation(summary = "查询监控记录")
    @GetMapping("/page")
    public Result<IPage<Monitor>> getMonitorPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status) {
        IPage<Monitor> page = monitorService.getMonitorPage(pageNum, pageSize, projectId, status);
        return Result.ok(page);
    }

    @Operation(summary = "删除监控记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteMonitor(@PathVariable Long id) {
        boolean ok = monitorService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "获取告警记录")
    @GetMapping("/alerts")
    public Result<IPage<Monitor>> getAlerts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long projectId) {
        IPage<Monitor> page = monitorService.getMonitorPage(pageNum, pageSize, projectId, "critical");
        return Result.ok(page);
    }
}
