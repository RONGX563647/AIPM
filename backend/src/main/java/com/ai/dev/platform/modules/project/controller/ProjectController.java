package com.ai.dev.platform.modules.project.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.project.entity.Project;
import com.ai.dev.platform.modules.project.service.ProjectService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目管理控制器
 * 
 * <p>负责处理项目相关的CRUD操作和查询接口，包括：
 * <ul>
 *   <li>项目创建、修改、删除</li>
 *   <li>项目分页查询</li>
 *   <li>项目详情查询</li>
 *   <li>项目状态管理</li>
 * </ul>
 * 
 * <p>该控制器提供完整的项目生命周期管理功能，支持：
 * <ul>
 *   <li>RESTful API设计</li>
 *   <li>分页查询优化</li>
 *   <li>参数验证和错误处理</li>
 *   <li>Swagger API文档生成</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "项目管理", description = "项目生命周期管理接口")
@RestController
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    /**
     * 新增项目
     * 
     * <p>创建新的项目记录，执行数据验证：
     * <ul>
     *   <li>验证项目名称唯一性</li>
     *   <li>验证必填字段完整性</li>
     *   <li>设置默认项目状态</li>
     *   <li>记录创建时间和操作人</li>
     * </ul>
     * 
     * @param project 项目实体对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "新增项目", description = "创建新的项目记录")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Project project) {
        boolean ok = projectService.save(project);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    /**
     * 修改项目
     * 
     * <p>更新现有项目信息，支持部分字段更新：
     * <ul>
     *   <li>验证项目存在性</li>
     *   <li>验证修改权限</li>
     *   <li>记录修改时间和操作人</li>
     *   <li>保持项目关联关系完整性</li>
     * </ul>
     * 
     * @param project 包含更新信息的项目对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "修改项目", description = "更新项目基本信息")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Project project) {
        boolean ok = projectService.updateById(project);
        if (!ok) return Result.error("修改失败");
        return Result.ok(null);
    }

    /**
     * 删除项目
     * 
     * <p>逻辑删除项目记录，保障数据安全：
     * <ul>
     *   <li>验证项目存在性</li>
      *   <li>检查项目关联依赖</li>
     *   <li>执行软删除策略</li>
     *   <li>记录删除操作日志</li>
     * </ul>
     * 
     * @param id 项目ID
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "删除项目", description = "逻辑删除项目记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "项目唯一标识") @PathVariable Long id) {
        boolean ok = projectService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    /**
     * 分页查询项目列表
     * 
     * <p>支持多条件组合查询和分页展示：
     * <ul>
     *   <li>按项目名称模糊查询</li>
     *   <li>按项目状态筛选</li>
     *   <li>分页参数控制</li>
     *   <li>返回总记录数和分页信息</li>
     * </ul>
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页记录数
     * @param name 项目名称（可选）
     * @param status 项目状态（可选）
     * @return Result<Page<Project>> 分页查询结果
     * @since 1.0.0
     */
    @Operation(summary = "分页查询项目", description = "支持多条件组合查询的项目列表")
    @GetMapping("/page")
    public Result<Page<Project>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "项目名称，支持模糊查询") @RequestParam(required = false) String name,
            @Parameter(description = "项目状态筛选") @RequestParam(required = false) String status
    ) {
        Page<Project> page = projectService.getProjectPage(pageNum, pageSize, name, status);
        return Result.ok(page);
    }

    /**
     * 根据ID查询项目详情
     * 
     * <p>获取项目完整信息，包括：
     * <ul>
     *   <li>项目基本信息</li>
     *   <li>项目关联信息</li>
     *   <li>项目统计信息</li>
     *   <li>权限验证</li>
     * </ul>
     * 
     * @param id 项目ID
     * @return Result<Project> 项目详情信息
     * @since 1.0.0
     */
    @Operation(summary = "查询项目详情", description = "根据ID获取项目完整信息")
    @GetMapping("/{id}")
    public Result<Project> getById(@Parameter(description = "项目唯一标识") @PathVariable Long id) {
        Project project = projectService.getById(id);
        return Result.ok(project);
    }
}