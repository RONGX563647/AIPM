package com.ai.dev.platform.modules.deploy.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.ai.dev.platform.modules.deploy.service.DeployRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "部署记录管理")
@RestController
@RequestMapping("/deploy")
public class DeployRecordController {

    @Autowired
    private DeployRecordService deployRecordService;

    @Operation(summary = "新增部署记录")
    @PostMapping("/add")
    public Result<DeployRecord> addDeploy(@RequestBody DeployRecord deployRecord) {
        boolean ok = deployRecordService.save(deployRecord);
        if (!ok) return Result.error("新增失败");
        return Result.ok(deployRecord);
    }

    @Operation(summary = "修改部署记录")
    @PutMapping("/update")
    public Result<DeployRecord> updateDeploy(@RequestBody DeployRecord deployRecord) {
        boolean ok = deployRecordService.updateById(deployRecord);
        if (!ok) return Result.error("修改失败");
        return Result.ok(deployRecord);
    }

    @Operation(summary = "删除部署记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDeploy(@PathVariable Long id) {
        boolean ok = deployRecordService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "查询部署记录")
    @GetMapping("/page")
    public Result<IPage<DeployRecord>> getDeployPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String env,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String version) {
        IPage<DeployRecord> page = deployRecordService.getDeployPage(pageNum, pageSize, projectId, env, status, version);
        return Result.ok(page);
    }
}
