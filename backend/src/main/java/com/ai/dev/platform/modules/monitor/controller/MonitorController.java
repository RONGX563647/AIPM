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

    @Operation(summary = "新增监控配置")
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
    public Result<String> deleteMonitor(@PathVariable Long id) {
        boolean ok = monitorService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok("删除成功");
    }

    @Operation(summary = "获取可用性统计")
    @GetMapping("/uptime/{project_id}")
    public Result<Double> getUptime(@PathVariable("project_id") Long projectId) {
        Double uptime = monitorService.getUptime(projectId);
        return Result.ok(uptime);
    }
}
