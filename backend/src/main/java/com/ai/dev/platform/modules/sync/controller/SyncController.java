package com.ai.dev.platform.modules.sync.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sync.entity.GitHubRepo;
import com.ai.dev.platform.modules.sync.entity.GiteeRepo;
import com.ai.dev.platform.modules.sync.entity.SyncRecord;
import com.ai.dev.platform.modules.sync.service.DataSyncService;
import com.ai.dev.platform.modules.sync.service.GitHubRepoService;
import com.ai.dev.platform.modules.sync.service.GiteeRepoService;
import com.ai.dev.platform.modules.sync.service.SyncRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据同步管理", description = "数据同步相关接口")
@RestController
@RequestMapping("/sync")
public class SyncController {

    @Autowired
    private DataSyncService dataSyncService;

    @Autowired
    private GitHubRepoService gitHubRepoService;

    @Autowired
    private GiteeRepoService giteeRepoService;

    @Autowired
    private SyncRecordService syncRecordService;

    @Operation(summary = "手动触发GitHub同步")
    @PostMapping("/github")
    public Result<Void> syncGitHub(
            @Parameter(description = "GitHub用户名") @RequestParam String username,
            @Parameter(description = "GitHub访问令牌") @RequestParam(required = false) String accessToken) {
        try {
            dataSyncService.syncGitHubRepos(username, accessToken);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.error("GitHub同步失败：" + e.getMessage());
        }
    }

    @Operation(summary = "手动触发Gitee同步")
    @PostMapping("/gitee")
    public Result<Void> syncGitee(
            @Parameter(description = "Gitee用户名") @RequestParam String username,
            @Parameter(description = "Gitee访问令牌") @RequestParam(required = false) String accessToken) {
        try {
            dataSyncService.syncGiteeRepos(username, accessToken);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.error("Gitee同步失败：" + e.getMessage());
        }
    }

    @Operation(summary = "手动触发全量同步")
    @PostMapping("/all")
    public Result<Void> syncAll() {
        try {
            dataSyncService.syncAll();
            return Result.ok(null);
        } catch (Exception e) {
            return Result.error("全量同步失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取GitHub仓库列表")
    @GetMapping("/github/repos")
    public Result<List<GitHubRepo>> getGitHubRepos(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<GitHubRepo> page = new Page<>(pageNum, pageSize);
        return Result.ok(gitHubRepoService.page(page).getRecords());
    }

    @Operation(summary = "获取Gitee仓库列表")
    @GetMapping("/gitee/repos")
    public Result<List<GiteeRepo>> getGiteeRepos(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<GiteeRepo> page = new Page<>(pageNum, pageSize);
        return Result.ok(giteeRepoService.page(page).getRecords());
    }

    @Operation(summary = "获取同步记录")
    @GetMapping("/records")
    public Result<List<SyncRecord>> getSyncRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "数据源") @RequestParam(required = false) String source) {
        Page<SyncRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SyncRecord> wrapper = new LambdaQueryWrapper<>();
        if (source != null && !source.isEmpty()) {
            wrapper.eq(SyncRecord::getSource, source);
        }
        wrapper.orderByDesc(SyncRecord::getSyncTime);
        return Result.ok(syncRecordService.page(page, wrapper).getRecords());
    }

    @Operation(summary = "删除GitHub仓库")
    @DeleteMapping("/github/{id}")
    public Result<Void> deleteGitHubRepo(@Parameter(description = "仓库ID") @PathVariable Long id) {
        gitHubRepoService.removeById(id);
        return Result.ok(null);
    }

    @Operation(summary = "删除Gitee仓库")
    @DeleteMapping("/gitee/{id}")
    public Result<Void> deleteGiteeRepo(@Parameter(description = "仓库ID") @PathVariable Long id) {
        giteeRepoService.removeById(id);
        return Result.ok(null);
    }
}
