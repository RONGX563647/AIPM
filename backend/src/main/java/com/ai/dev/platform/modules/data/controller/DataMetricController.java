package com.ai.dev.platform.modules.data.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.data.entity.DataMetric;
import com.ai.dev.platform.modules.data.service.DataMetricService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "数据中心")
@RestController
@RequestMapping("/data")
public class DataMetricController {

    @Autowired
    private DataMetricService dataMetricService;

    @Operation(summary = "分页查询指标")
    @GetMapping("/metrics")
    public Result<IPage<DataMetric>> getMetrics(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name) {
        IPage<DataMetric> page = dataMetricService.getMetricPage(pageNum, pageSize, category, name);
        return Result.ok(page);
    }

    @Operation(summary = "获取类别统计")
    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        List<Map<String, Object>> stats = dataMetricService.getCategoryStats();
        return Result.ok(stats);
    }

    @Operation(summary = "根据ID查询指标")
    @GetMapping("/metric/{id}")
    public Result<DataMetric> getMetricById(@PathVariable Long id) {
        DataMetric metric = dataMetricService.getById(id);
        if (metric == null) {
            return Result.error("指标不存在");
        }
        return Result.ok(metric);
    }

    @Operation(summary = "新增指标")
    @PostMapping("/metric")
    public Result<DataMetric> addMetric(@RequestBody DataMetric metric) {
        if (metric.getName() == null || metric.getName().isEmpty()) {
            return Result.error("指标名称不能为空");
        }
        if (metric.getValue() == null) {
            return Result.error("指标值不能为空");
        }
        boolean success = dataMetricService.save(metric);
        if (!success) {
            return Result.error("新增失败");
        }
        return Result.ok(metric);
    }

    @Operation(summary = "修改指标")
    @PutMapping("/metric")
    public Result<DataMetric> updateMetric(@RequestBody DataMetric metric) {
        if (metric.getId() == null) {
            return Result.error("指标ID不能为空");
        }
        boolean success = dataMetricService.updateById(metric);
        if (!success) {
            return Result.error("修改失败");
        }
        return Result.ok(metric);
    }

    @Operation(summary = "删除指标")
    @DeleteMapping("/metric/{id}")
    public Result<String> deleteMetric(@PathVariable Long id) {
        boolean success = dataMetricService.removeById(id);
        if (!success) {
            return Result.error("删除失败");
        }
        return Result.ok("删除成功");
    }
}
