package com.ai.dev.platform.modules.deploy.service.impl;

import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.ai.dev.platform.modules.deploy.mapper.DeployRecordMapper;
import com.ai.dev.platform.modules.deploy.service.DeployRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 部署记录服务实现类
 * 
 * <p>部署记录管理业务逻辑的具体实现，负责处理：
 * <ul>
 *   <li>部署记录的业务规则验证</li>
 *   <li>部署状态变更的逻辑处理</li>
 *   <li>部署历史数据的查询和统计</li>
 *   <li>部署相关业务异常处理</li>
 * </ul>
 * 
 * <p>该实现类继承自MyBatis-Plus的ServiceImpl，利用其提供的
 * 基础CRUD操作，专注于部署管理的业务逻辑实现。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class DeployRecordServiceImpl extends ServiceImpl<DeployRecordMapper, DeployRecord> implements DeployRecordService {

    /**
     * 分页查询部署记录实现
     * 
     * <p>实现多条件组合筛选的部署记录分页查询功能，具体处理逻辑：
     * <ol>
     *   <li><b>参数验证</b>：检查分页参数的有效性</li>
     *   <li><b>构建分页对象</b>：创建MyBatis-Plus分页对象</li>
     *   <li><b>构建查询条件</b>：使用LambdaQueryWrapper构建动态查询条件</li>
     *   <li><b>条件筛选</b>：根据传入参数动态添加WHERE条件</li>
     *   <li><b>排序设置</b>：按创建时间倒序排列，最新记录在前</li>
     *   <li><b>执行查询</b>：调用父类方法执行分页查询</li>
     * </ol>
     * 
     * <p><b>查询条件说明：</b>
     * <ul>
     *   <li>projectId：项目ID精确匹配，用于筛选特定项目</li>
     *   <li>env：部署环境精确匹配，如dev/test/prod</li>
     *   <li>status：部署状态精确匹配，如pending/success/failed</li>
     *   <li>version：版本号模糊匹配，支持部分版本号查询</li>
     * </ul>
     * 
     * <p><b>性能优化考虑：</b>
     * <ul>
     *   <li>使用LambdaQueryWrapper避免字符串拼接错误</li>
     *   <li>动态条件构建避免不必要的查询条件</li>
     *   <li>数据库索引优化建议：projectId, env, status, createTime</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数，必须大于0
     * @param pageSize 每页记录数，建议范围1-100，过大会影响性能
     * @param projectId 项目ID，null时表示不限制项目筛选
     * @param env 部署环境，null或空字符串时表示不限制环境筛选
     * @param status 部署状态，null或空字符串时表示不限制状态筛选
     * @param version 版本号，null或空字符串时表示不限制版本筛选
     * @return IPage<DeployRecord> 分页查询结果，包含：
     *         <ul>
     *           <li>records：当前页的部署记录列表</li>
     *           <li>total：总记录数</li>
     *           <li>size：每页大小</li>
     *           <li>current：当前页码</li>
     *           <li>pages：总页数</li>
     *         </ul>
     * @throws IllegalArgumentException 当pageNum≤0或pageSize≤0时抛出
     * @throws RuntimeException 数据库查询异常时抛出
     * @since 1.0.0
     */
    @Override
    public IPage<DeployRecord> getDeployPage(Integer pageNum, Integer pageSize, Long projectId, String env, String status, String version) {
        // 1. 参数验证 - 确保分页参数合法性
        if (pageNum <= 0) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }
        
        // 2. 构建分页对象 - 设置页码和每页大小
        Page<DeployRecord> page = new Page<>(pageNum, pageSize);
        
        // 3. 构建查询条件包装器 - 使用LambdaQueryWrapper避免字段名错误
        LambdaQueryWrapper<DeployRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 4. 动态添加查询条件 - 根据参数值决定是否添加条件
        
        // 项目ID筛选条件：只在projectId不为null时添加
        if (projectId != null) {
            wrapper.eq(DeployRecord::getProjectId, projectId);
        }
        
        // 部署环境筛选条件：只在env不为null且非空时添加
        if (env != null && !env.isEmpty()) {
            wrapper.eq(DeployRecord::getEnv, env);
        }
        
        // 部署状态筛选条件：只在status不为null且非空时添加
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DeployRecord::getStatus, status);
        }
        
        // 版本号筛选条件：使用模糊匹配，只在version不为null且非空时添加
        if (version != null && !version.isEmpty()) {
            wrapper.like(DeployRecord::getVersion, version);
        }
        
        // 5. 设置排序规则 - 按创建时间倒序，确保最新部署记录在前
        wrapper.orderByDesc(DeployRecord::getCreateTime);
        
        // 6. 执行分页查询 - 调用父类方法执行实际的数据库查询
        return page(page, wrapper);
    }
}
