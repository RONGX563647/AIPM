package com.ai.dev.platform.modules.deploy.service;

import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 部署记录服务接口
 * 
 * <p>定义部署记录管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>部署记录的增删改查操作</li>
 *   <li>部署状态管理和跟踪</li>
 *   <li>部署历史查询和统计</li>
 *   <li>多维度部署记录筛选</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了部署管理特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DeployRecordService extends IService<DeployRecord> {
    
    /**
     * 分页查询部署记录
     * 
     * <p>支持多条件组合筛选的部署记录分页查询功能：
     * <ul>
     *   <li>按项目ID精确匹配筛选</li>
     *   <li>按部署环境精确匹配筛选</li>
     *   <li>按部署状态精确匹配筛选</li>
     *   <li>按版本号模糊匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     * </ul>
     * 
     * <p>查询逻辑说明：
     * <ol>
     *   <li>构建分页对象，设置页码和每页大小</li>
     *   <li>创建查询条件包装器</li>
     *   <li>根据传入参数动态添加查询条件</li>
     *   <li>设置按创建时间倒序排序</li>
     *   <li>执行分页查询并返回结果</li>
     * </ol>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param projectId 项目ID，用于筛选特定项目的部署记录（可选）
     * @param env 部署环境，如dev/test/prod等（可选）
     * @param status 部署状态，如pending/success/failed等（可选）
     * @param version 版本号，支持模糊匹配查询（可选）
     * @return IPage<DeployRecord> 分页查询结果，包含记录列表和分页信息
     * @throws IllegalArgumentException 当pageNum或pageSize参数不合法时抛出
     * @since 1.0.0
     */
    IPage<DeployRecord> getDeployPage(Integer pageNum, Integer pageSize, Long projectId, String env, String status, String version);
}
