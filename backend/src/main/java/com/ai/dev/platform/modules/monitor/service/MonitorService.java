package com.ai.dev.platform.modules.monitor.service;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 系统监控服务接口
 * 
 * <p>定义系统监控管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>监控任务的增删改查操作</li>
 *   <li>系统可用性监控和告警</li>
 *   <li>性能指标收集和分析</li>
 *   <li>服务状态实时检测</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了系统监控特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MonitorService extends IService<Monitor> {
    
    /**
     * 分页查询监控记录
     * 
     * <p>支持多条件组合筛选的监控记录分页查询功能：
     * <ul>
     *   <li>按项目ID精确匹配筛选</li>
     *   <li>按监控状态精确匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>返回监控详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param projectId 项目ID，用于筛选特定项目的监控记录（可选）
     * @param status 监控状态，如active/inactive等（可选）
     * @return IPage<Monitor> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<Monitor> getMonitorPage(Integer pageNum, Integer pageSize, Long projectId, String status);

    /**
     * 新增监控任务
     * 
     * <p>创建新的系统监控任务，执行以下验证和初始化：
     * <ul>
     *   <li>验证监控目标的有效性</li>
     *   <li>设置默认监控参数</li>
     *   <li>初始化监控状态为active</li>
     *   <li>记录创建时间和操作人</li>
     * </ul>
     * 
     * @param monitor 监控任务实体对象
     * @return Monitor 创建成功的监控任务对象
     * @since 1.0.0
     */
    Monitor addMonitor(Monitor monitor);

    /**
     * 获取项目系统可用率
     * 
     * <p>计算指定项目的系统可用时间百分比：
     * <ul>
     *   <li>统计项目监控任务的正常运行时间</li>
     *   <li>计算相对于总监控时间的比例</li>
     *   <li>返回0-100之间的可用率数值</li>
     * </ul>
     * 
     * @param projectId 项目ID
     * @return Double 系统可用率百分比(0-100)
     * @since 1.0.0
     */
    Double getUptime(Long projectId);

    /**
     * 检查监控状态并更新
     * 
     * <p>根据监控检测结果更新监控任务状态：
     * <ul>
     *   <li>分析响应时间是否超过阈值</li>
     *   <li>判断服务是否处于慢响应状态</li>
     *   <li>更新监控任务的当前状态</li>
     *   <li>记录状态变更历史</li>
     * </ul>
     * 
     * @param monitor 监控任务对象
     * @param responseTime 实际响应时间(毫秒)
     * @param slowThreshold 慢响应阈值(毫秒)
     * @since 1.0.0
     */
    void checkMonitorStatus(Monitor monitor, Integer responseTime, Integer slowThreshold);
}
