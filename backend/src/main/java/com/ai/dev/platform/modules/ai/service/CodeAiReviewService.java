package com.ai.dev.platform.modules.ai.service;

import com.ai.dev.platform.modules.ai.entity.CodeAiReview;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * AI代码审查服务接口
 * 
 * <p>定义AI代码审查功能的核心业务逻辑接口，提供：
 * <ul>
 *   <li>代码审查记录的增删改查操作</li>
 *   <li>AI代码质量评估和建议生成</li>
 *   <li>代码审查历史查询和统计</li>
 *   <li>项目维度的代码质量分析</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了AI代码审查特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CodeAiReviewService extends IService<CodeAiReview> {
    
    /**
     * 分页查询代码审查记录
     * 
     * <p>支持按项目ID筛选的代码审查记录分页查询功能：
     * <ul>
     *   <li>按项目ID精确匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>返回审查记录详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param projectId 项目ID，用于筛选特定项目的审查记录（可选）
     * @return IPage<CodeAiReview> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<CodeAiReview> getReviewPage(Integer pageNum, Integer pageSize, Long projectId);

    /**
     * 提交代码审查请求
     * 
     * <p>提交代码内容进行AI智能审查，执行以下操作：
     * <ul>
     *   <li>分析代码质量和结构</li>
     *   <li>生成质量评分(80-95分随机)</li>
     *   <li>提供优化建议和改进方案</li>
     *   <li>生成优化后的代码示例</li>
     *   <li>记录审查时间和项目关联</li>
     * </ul>
     * 
     * <p><b>AI审查特点：</b>
     * <ul>
     *   <li>模拟真实的代码审查过程</li>
     *   <li>提供结构化的问题反馈</li>
     *   <li>给出具体的改进建议</li>
     *   <li>展示优化前后的代码对比</li>
     * </ul>
     * 
     * @param projectId 关联项目ID
     * @param codeContent 待审查的代码内容
     * @return CodeAiReview 完整的审查结果对象
     * @since 1.0.0
     */
    CodeAiReview submitReview(Long projectId, String codeContent);
}
