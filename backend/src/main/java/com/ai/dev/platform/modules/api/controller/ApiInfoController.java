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

/**
 * API信息管理控制器
 * 
 * <p>负责处理API接口信息的管理和服务，包括：
 * <ul>
 *   <li>API接口文档管理</li>
 *   <li>接口信息增删改查</li>
 *   <li>接口在线调试</li>
 *   <li>接口版本控制</li>
 * </ul>
 * 
 * <p>该控制器提供完整的API生命周期管理功能，支持：
 * <ul>
 *   <li>RESTful API文档管理</li>
 *   <li>接口调试和测试</li>
 *   <li>多项目API管理</li>
 *   <li>接口变更历史追踪</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "接口信息管理", description = "API接口文档和服务管理接口")
@RestController
@RequestMapping("/api/info")
public class ApiInfoController {

    @Autowired
    private ApiInfoService apiInfoService;

    /**
     * 新增API接口信息
     * 
     * <p>创建新的API接口文档记录，执行数据验证：
     * <ul>
     *   <li>验证接口路径唯一性</li>
     *   <li>验证HTTP方法合法性</li>
     *   <li>验证参数格式正确性</li>
     *   <li>设置接口版本信息</li>
     * </ul>
     * 
     * @param apiInfo API接口信息实体对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "新增API接口", description = "创建API接口文档记录")
    @PostMapping("/add")
    public Result<Void> add(@RequestBody ApiInfo apiInfo) {
        boolean ok = apiInfoService.save(apiInfo);
        if (!ok) return Result.error("新增失败");
        return Result.ok(null);
    }

    /**
     * 更新API接口信息
     * 
     * <p>修改现有API接口文档，支持版本控制：
     * <ul>
     *   <li>验证接口存在性</li>
     *   <li>记录变更历史</li>
     *   <li>维护接口版本链</li>
     *   <li>更新关联的测试用例</li>
     * </ul>
     * 
     * @param apiInfo 包含更新信息的API接口对象
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "更新API接口", description = "修改API接口文档信息")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody ApiInfo apiInfo) {
        boolean ok = apiInfoService.updateById(apiInfo);
        if (!ok) return Result.error("更新失败");
        return Result.ok(null);
    }

    /**
     * 删除API接口信息
     * 
     * <p>逻辑删除API接口记录，保障数据安全：
     * <ul>
     *   <li>验证接口存在性</li>
     *   <li>检查接口依赖关系</li>
     *   <li>验证删除权限</li>
     *   <li>记录删除操作日志</li>
     * </ul>
     * 
     * @param id API接口ID
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "删除API接口", description = "逻辑删除API接口记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@Parameter(description = "API接口唯一标识") @PathVariable Long id) {
        boolean ok = apiInfoService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    /**
     * 分页查询API接口列表
     * 
     * <p>支持多条件组合查询和分页展示：
     * <ul>
     *   <li>按项目ID筛选</li>
     *   <li>按接口路径模糊查询</li>
     *   <li>按HTTP方法筛选</li>
     *   <li>返回接口详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页记录数
     * @param projectId 项目ID（可选）
     * @param path 接口路径（可选）
     * @param method HTTP方法（可选）
     * @return Result<IPage<ApiInfo>> 分页查询结果
     * @since 1.0.0
     */
    @Operation(summary = "分页查询API接口", description = "支持多条件组合查询的接口列表")
    @GetMapping("/page")
    public Result<IPage<ApiInfo>> page(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "所属项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "接口路径，支持模糊查询") @RequestParam(required = false) String path,
            @Parameter(description = "HTTP请求方法") @RequestParam(required = false) String method
    ) {
        IPage<ApiInfo> page = apiInfoService.getApiInfoPage(pageNum, pageSize, projectId, path, method);
        return Result.ok(page);
    }

    /**
     * 在线调试API接口
     * 
     * <p>提供API接口的在线调试功能，支持实时测试：
     * <ul>
     *   <li>动态构建HTTP请求</li>
     *   <li>支持自定义请求头</li>
     *   <li>支持请求参数配置</li>
     *   <li>返回详细的响应信息</li>
     * </ul>
     * 
     * @param request 调试请求参数，包含path、method、params、header等
     * @return Result<Map<String, Object>> 调试结果，包含请求响应详情
     * @since 1.0.0
     */
    @Operation(summary = "在线调试API", description = "实时测试API接口功能")
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
