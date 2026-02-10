package com.ai.dev.platform.modules.ai.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.ai.entity.CodeAiReview;
import com.ai.dev.platform.modules.ai.service.CodeAiReviewService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI代码评审管理")
@RestController
@RequestMapping("/ai/review")
public class CodeAiReviewController {

    @Autowired
    private CodeAiReviewService codeAiReviewService;

    @Operation(summary = "提交代码评审")
    @PostMapping("/submit")
    public Result<CodeAiReview> submitReview(@RequestBody CodeAiReview review) {
        CodeAiReview result = codeAiReviewService.submitReview(review.getProjectId(), review.getCodeContent());
        if (result == null) return Result.error("评审失败");
        return Result.ok(result);
    }

    @Operation(summary = "查询评审记录")
    @GetMapping("/page")
    public Result<IPage<CodeAiReview>> getReviewPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long projectId) {
        IPage<CodeAiReview> page = codeAiReviewService.getReviewPage(pageNum, pageSize, projectId);
        return Result.ok(page);
    }

    @Operation(summary = "删除评审记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        boolean ok = codeAiReviewService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }
}
