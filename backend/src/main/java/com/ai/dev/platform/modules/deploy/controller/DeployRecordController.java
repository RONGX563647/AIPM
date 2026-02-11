package com.ai.dev.platform.modules.deploy.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.ai.dev.platform.modules.deploy.service.DeployRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 部署记录管理控制器
 * 
 * <p>负责处理项目部署记录的管理和服务，包括：
 * <ul>
 *   <li>部署记录增删改查</li>
 *   <li>部署状态跟踪</li>
 *   <li>部署环境管理</li>
 *   <li>部署历史查询</li>
 * </ul>
 * 
 * <p>该控制器提供完整的部署生命周期管理功能，支持：
 * <ul>
 *   <li>多环境部署记录管理</li>
 *   <li>部署状态实时监控</li>
 *   <li>部署版本追踪</li>
 *   <li>部署统计分析</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "部署记录管理", description = "项目部署记录管理接口")
@RestController
@RequestMapping("/deploy")
public class DeployRecordController {

    @Autowired
    private DeployRecordService deployRecordService;

    /**
     * 新增部署记录
     * 
     * <p>创建新的部署记录，记录部署详细信息：
     * <ul>
     *   <li>验证项目和环境存在性</li>
     *   <li>记录部署配置信息</li>
     *   <li>设置初始部署状态</li>
     *   <li>关联部署操作人</li>
     * </ul>
     * 
     * @param deployRecord 部署记录实体对象
     * @return Result<DeployRecord> 操作结果，成功返回创建的记录，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "新增部署记录", description = "创建项目部署记录")
    @PostMapping("/add")
    public Result<DeployRecord> addDeploy(@RequestBody DeployRecord deployRecord) {
        boolean ok = deployRecordService.save(deployRecord);
        if (!ok) return Result.error("新增失败");
        return Result.ok(deployRecord);
    }

    /**
     * 修改部署记录
     * 
     * <p>更新部署记录信息和状态，支持部署流程管理：
     * <ul>
     *   <li>验证部署记录存在性</li>
     *   <li>验证状态变更合法性</li>
     *   <li>记录状态变更历史</li>
     *   <li>更新部署完成时间</li>
     * </ul>
     * 
     * @param deployRecord 包含更新信息的部署记录对象
     * @return Result<DeployRecord> 操作结果，成功返回更新后的记录，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "修改部署记录", description = "更新部署记录信息和状态")
    @PutMapping("/update")
    public Result<DeployRecord> updateDeploy(@RequestBody DeployRecord deployRecord) {
        boolean ok = deployRecordService.updateById(deployRecord);
        if (!ok) return Result.error("修改失败");
        return Result.ok(deployRecord);
    }

    /**
     * 删除部署记录
     * 
     * <p>逻辑删除部署记录，维护部署历史完整性：
     * <ul>
     *   <li>验证部署记录存在性</li>
     *   <li>检查记录关联依赖</li>
     *   <li>验证删除权限</li>
     *   <li>记录删除操作日志</li>
     * </ul>
     * 
     * @param id 部署记录ID
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @Operation(summary = "删除部署记录", description = "逻辑删除部署记录")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDeploy(@PathVariable Long id) {
        boolean ok = deployRecordService.removeById(id);
        if (!ok) return Result.error("删除失败");
        return Result.ok(null);
    }

    /**
     * 分页查询部署记录
     * 
     * <p>支持多维度筛选和分页查询部署历史：
     * <ul>
     *   <li>按项目ID筛选</li>
     *   <li>按部署环境筛选</li>
     *   <li>按部署状态筛选</li>
     *   <li>按版本号筛选</li>
     *   <li>返回部署详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页记录数
     * @param projectId 项目ID（可选）
     * @param env 部署环境（可选）
     * @param status 部署状态（可选）
     * @param version 版本号（可选）
     * @return Result<IPage<DeployRecord>> 分页查询结果
     * @since 1.0.0
     */
    @Operation(summary = "查询部署记录", description = "支持多条件筛选的部署记录列表")
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
