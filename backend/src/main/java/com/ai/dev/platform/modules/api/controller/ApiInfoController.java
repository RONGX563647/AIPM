package com.ai.dev.platform.modules.api.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.api.entity.ApiInfo;
import com.ai.dev.platform.modules.api.service.ApiInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "接口信息管理")
@RestController
@RequestMapping("/api/info")
public class ApiInfoController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Operation(summary = "新增接口")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody ApiInfo apiInfo) {
        boolean ok = apiInfoService.save(apiInfo);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    @Operation(summary = "更新接口")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody ApiInfo apiInfo) {
        boolean ok = apiInfoService.updateById(apiInfo);
        if (!ok) return Result.error("更新失败");
        return Result.ok(null);
    }

    @Operation(summary = "删除接口")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "接口ID") @PathVariable Long id) {
        boolean ok = apiInfoService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    @Operation(summary = "分页查询接口")
    @GetMapping("/page")
    public Result<IPage<ApiInfo>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "接口路径") @RequestParam(required = false) String path,
            @Parameter(description = "请求方法") @RequestParam(required = false) String method
    ) {
        IPage<ApiInfo> page = apiInfoService.getApiInfoPage(pageNum, pageSize, projectId, path, method);
        return Result.ok(page);
    }

    @Operation(summary = "在线调试接口")
    @PostMapping("/debug")
    public Result<Map<String, Object>> debug(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        String method = request.get("method");
        String params = request.get("params");
        String header = request.get("header");
        
        Map<String, Object> result = apiInfoService.debugApi(path, method, params, header);
        return Result.ok(result);
    }
}
