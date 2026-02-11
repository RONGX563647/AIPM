package com.ai.dev.platform.modules.test.service;

import com.ai.dev.platform.modules.test.entity.TestReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 测试报告服务接口
 * 
 * <p>定义测试报告管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>测试报告的增删改查操作</li>
 *   <li>测试结果的统计和分析</li>
 *   <li>多维度测试报告筛选</li>
 *   <li>测试执行历史查询</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了测试管理特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TestReportService extends IService<TestReport> {

    /**
     * 分页查询测试报告
     * 
     * <p>支持多条件组合筛选的测试报告分页查询功能：
     * <ul>
     *   <li>按API接口ID精确匹配筛选</li>
     *   <li>按测试用例ID精确匹配筛选</li>
     *   <li>按测试状态精确匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>返回测试报告详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param apiId API接口ID，用于筛选特定接口的测试报告（可选）
     * @param caseId 测试用例ID，用于筛选特定用例的测试报告（可选）
     * @param status 测试状态，如pass/fail等（可选）
     * @return IPage<TestReport> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<TestReport> getTestReportPage(Integer pageNum, Integer pageSize, Long apiId, Long caseId, String status);
}
