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

@Tag(name = "项目管理")
@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Operation(summary = "新增项目")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Project project) {
        boolean ok = projectService.save(project);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    @Operation(summary = "修改项目")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Project project) {
        boolean ok = projectService.updateById(project);
        if (!ok) return Result.error("修改失败");
        return Result.ok(null);
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "项目ID") @PathVariable Long id) {
        boolean ok = projectService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "分页查询项目")
    @GetMapping("/page")
    public Result<Page<Project>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "项目名称") @RequestParam(required = false) String name,
            @Parameter(description = "项目状态") @RequestParam(required = false) String status
    ) {
        Page<Project> page = projectService.getProjectPage(pageNum, pageSize, name, status);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询项目")
    @GetMapping("/{id}")
    public Result<Project> getById(@Parameter(description = "项目ID") @PathVariable Long id) {
        Project project = projectService.getById(id);
        return Result.ok(project);
    }
}