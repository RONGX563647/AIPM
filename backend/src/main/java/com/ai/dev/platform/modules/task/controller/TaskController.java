package com.ai.dev.platform.modules.task.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.task.entity.Task;
import com.ai.dev.platform.modules.task.service.TaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务管理")
@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "新增任务")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Task task) {
        boolean ok = taskService.save(task);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    @Operation(summary = "修改任务")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Task task) {
        boolean ok = taskService.updateById(task);
        if (!ok) return Result.error("修改失败");
        return Result.ok(null);
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "任务ID") @PathVariable Long id) {
        boolean ok = taskService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "分页查询任务")
    @GetMapping("/page")
    public Result<Page<Task>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status
    ) {
        Page<Task> page = taskService.getTaskPage(pageNum, pageSize, projectId, status);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询任务")
    @GetMapping("/{id}")
    public Result<Task> getById(@Parameter(description = "任务ID") @PathVariable Long id) {
        Task task = taskService.getById(id);
        return Result.ok(task);
    }

    @Operation(summary = "获取项目进度")
    @GetMapping("/progress/{projectId}")
    public Result<Integer> getProgress(@Parameter(description = "项目ID") @PathVariable Long projectId) {
        Integer progress = taskService.getProjectProgress(projectId);
        return Result.ok(progress);
    }
}
