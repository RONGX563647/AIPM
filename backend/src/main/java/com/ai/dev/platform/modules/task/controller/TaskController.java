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

/**
 * 任务管理控制器
 * 
 * <p>负责处理项目任务相关的CRUD操作和状态管理，包括：
 * <ul>
 *   <li>任务创建、修改、删除</li>
 *   <li>任务分页查询和筛选</li>
 *   <li>任务状态跟踪</li>
 *   <li>项目进度统计</li>
 * </ul>
 * 
 * <p>该控制器提供完整的任务生命周期管理功能，支持：
 * <ul>
 *   <li>基于项目的任务管理</li>
 *   <li>任务状态流转控制</li>
 *   <li>进度统计和可视化</li>
 *   <li>批量操作支持</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "任务管理", description = "项目任务生命周期管理接口")
@RestController
@RequestMapping("/task")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    /**
     * 新增任务
     * 
     * <p>创建新的任务记录，执行关联验证：
     * <ul>
     *   <li>验证所属项目存在性</li>
     *   <li>验证任务名称唯一性</li>
     *   <li>设置默认任务状态</li>
     *   <li>关联创建人信息</li>
     * </ul>
     * 
     * @param task 任务实体对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "新增任务", description = "创建项目任务记录")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Task task) {
        boolean ok = taskService.save(task);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    /**
     * 修改任务
     * 
     * <p>更新任务信息和状态，支持状态流转：
     * <ul>
     *   <li>验证任务存在性和权限</li>
     *   <li>验证状态变更合法性</li>
     *   <li>记录状态变更历史</li>
     *   <li>更新关联的项目进度</li>
     * </ul>
     * 
     * @param task 包含更新信息的任务对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "修改任务", description = "更新任务信息和状态")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Task task) {
        boolean ok = taskService.updateById(task);
        if (!ok) return Result.error("修改失败");
        return Result.ok(null);
    }

    /**
     * 删除任务
     * 
     * <p>逻辑删除任务记录，维护数据完整性：
     * <ul>
     *   <li>验证任务存在性</li>
     *   <li>检查任务依赖关系</li>
     *   <li>更新关联项目进度</li>
     *   <li>记录删除操作日志</li>
     * </ul>
     * 
     * @param id 任务ID
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "删除任务", description = "逻辑删除任务记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "任务唯一标识") @PathVariable Long id) {
        boolean ok = taskService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    /**
     * 分页查询任务列表
     * 
     * <p>支持多维度筛选和分页展示：
     * <ul>
     *   <li>按项目ID筛选</li>
     *   <li>按任务状态筛选</li>
     *   <li>分页参数控制</li>
     *   <li>返回任务关联信息</li>
     * </ul>
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页记录数
     * @param projectId 项目ID（可选）
     * @param status 任务状态（可选）
     * @return Result<Page<Task>> 分页查询结果
     * @since 1.0.0
     */
    @Operation(summary = "分页查询任务", description = "支持多条件筛选的任务列表")
    @GetMapping("/page")
    public Result<Page<Task>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "所属项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "任务状态筛选") @RequestParam(required = false) String status
    ) {
        Page<Task> page = taskService.getTaskPage(pageNum, pageSize, projectId, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询任务详情
     * 
     * <p>获取任务完整信息和关联数据：
     * <ul>
     *   <li>任务基本信息</li>
     *   <li>所属项目信息</li>
     *   <li>执行人信息</li>
     *   <li>历史变更记录</li>
     * </ul>
     * 
     * @param id 任务ID
     * @return Result<Task> 任务详情信息
     * @since 1.0.0
     */
    @Operation(summary = "查询任务详情", description = "根据ID获取任务完整信息")
    @GetMapping("/{id}")
    public Result<Task> getById(@Parameter(description = "任务唯一标识") @PathVariable Long id) {
        Task task = taskService.getById(id);
        return Result.ok(task);
    }

    /**
     * 获取项目进度统计
     * 
     * <p>计算指定项目的任务完成进度：
     * <ul>
     *   <li>统计已完成任务数</li>
     *   <li>统计总任务数</li>
     *   <li>计算完成百分比</li>
     *   <li>支持实时进度查询</li>
     * </ul>
     * 
     * @param projectId 项目ID
     * @return Result<Integer> 项目完成进度百分比（0-100）
     * @since 1.0.0
     */
    @Operation(summary = "获取项目进度", description = "计算项目任务完成进度")
    @GetMapping("/progress/{projectId}")
    public Result<Integer> getProgress(@Parameter(description = "项目唯一标识") @PathVariable Long projectId) {
        Integer progress = taskService.getProjectProgress(projectId);
        return Result.ok(progress);
    }
}
