package com.ai.dev.platform.modules.test.service;

import com.ai.dev.platform.modules.test.entity.TestCase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ai.dev.platform.modules.test.entity.TestReport;

import java.util.Map;

/**
 * 测试用例服务接口
 * 
 * <p>定义测试用例管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>测试用例的增删改查操作</li>
 *   <li>测试用例的执行和调度</li>
 *   <li>测试结果的收集和报告生成</li>
 *   <li>API接口维度的测试用例管理</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了测试执行特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TestCaseService extends IService<TestCase> {

    /**
     * 分页查询测试用例
     * 
     * <p>支持按API接口ID筛选的测试用例分页查询功能：
     * <ul>
     *   <li>按API接口ID精确匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>返回测试用例详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param apiId API接口ID，用于筛选特定接口的测试用例（可选）
     * @return IPage<TestCase> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<TestCase> getTestCasePage(Integer pageNum, Integer pageSize, Long apiId);

    /**
     * 执行测试用例
     * 
     * <p>运行指定的测试用例并生成测试报告：
     * <ul>
     *   <li>获取测试用例的详细配置</li>
     *   <li>构造API请求参数和头信息</li>
     *   <li>执行API调用并收集响应结果</li>
     *   <li>分析测试结果并生成报告</li>
     *   <li>记录测试执行历史</li>
     * </ul>
     * 
     * <p><b>执行流程：</b>
     * <ol>
     *   <li>查询测试用例配置信息</li>
     *   <li>构建HTTP请求对象</li>
     *   <li>发送请求到目标API接口</li>
     *   <li>验证响应结果是否符合预期</li>
     *   <li>生成详细的测试报告</li>
     * </ol>
     * 
     * @param caseId 测试用例ID
     * @return Map<String, Object> 测试执行结果，包含：
     *         <ul>
     *           <li>success：执行是否成功</li>
     *           <li>response：API响应内容</li>
     *           <li>assertionResult：断言结果</li>
     *           <li>executionTime：执行耗时</li>
     *           <li>reportId：生成的测试报告ID</li>
     *         </ul>
     * @since 1.0.0
     */
    Map<String, Object> runTestCase(Long caseId);
}
