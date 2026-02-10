package com.ai.dev.platform.modules.test.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.test.entity.TestCase;
import com.ai.dev.platform.modules.test.entity.TestReport;
import com.ai.dev.platform.modules.test.service.TestCaseService;
import com.ai.dev.platform.modules.test.service.TestReportService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "测试用例管理")
@RestController
@RequestMapping("/test/case")
public class TestCaseController {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestReportService testReportService;

    @Operation(summary = "新增测试用例")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody TestCase testCase) {
        boolean ok = testCaseService.save(testCase);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    @Operation(summary = "更新测试用例")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody TestCase testCase) {
        boolean ok = testCaseService.updateById(testCase);
        if (!ok) return Result.error("更新失败");
        return Result.ok(null);
    }

    @Operation(summary = "删除测试用例")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "用例ID") @PathVariable Long id) {
        boolean ok = testCaseService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "分页查询测试用例")
    @GetMapping("/page")
    public Result<IPage<TestCase>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "接口ID") @RequestParam(required = false) Long apiId
    ) {
        IPage<TestCase> page = testCaseService.getTestCasePage(pageNum, pageSize, apiId);
        return Result.ok(page);
    }

    @Operation(summary = "执行测试用例")
    @PostMapping("/run/{caseId}")
    public Result<Map<String, Object>> run(@Parameter(description = "用例ID") @PathVariable Long caseId) {
        Map<String, Object> result = testCaseService.runTestCase(caseId);
        return Result.ok(result);
    }
}
