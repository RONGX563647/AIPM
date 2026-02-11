package com.ai.dev.platform.modules.api.service;

import com.ai.dev.platform.modules.api.entity.ApiInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * API信息服务接口
 * 
 * <p>定义API信息管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>API接口信息的增删改查操作</li>
 *   <li>API文档的维护和管理</li>
 *   <li>接口调试和测试功能</li>
 *   <li>项目维度的API统计分析</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了API管理特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ApiInfoService extends IService<ApiInfo> {

    /**
     * 分页查询API信息
     * 
     * <p>支持多条件组合筛选的API信息分页查询功能：
     * <ul>
     *   <li>按项目ID精确匹配筛选</li>
     *   <li>按接口路径模糊匹配筛选</li>
     *   <li>按请求方法精确匹配筛选</li>
     *   <li>按创建时间倒序排列</li>
     *   <li>返回API详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param projectId 项目ID，用于筛选特定项目的API（可选）
     * @param path 接口路径，支持模糊匹配查询（可选）
     * @param method 请求方法，如GET/POST/PUT/DELETE等（可选）
     * @return IPage<ApiInfo> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<ApiInfo> getApiInfoPage(Integer pageNum, Integer pageSize, Long projectId, String path, String method);

    /**
     * 调试API接口
     * 
     * <p>实时调试和测试API接口的功能：
     * <ul>
     *   <li>构造完整的HTTP请求</li>
     *   <li>发送请求到目标接口</li>
     *   <li>收集响应结果和状态码</li>
     *   <li>处理请求头和参数</li>
     *   <li>返回调试结果信息</li>
     * </ul>
     * 
     * <p><b>调试流程：</b>
     * <ol>
     *   <li>解析传入的请求参数和头信息</li>
     *   <li>构建完整的HTTP请求对象</li>
     *   <li>发送请求到本地API服务</li>
     *   <li>捕获响应结果和异常信息</li>
     *   <li>格式化返回调试结果</li>
     * </ol>
     * 
     * @param path API接口路径
     * @param method HTTP请求方法
     * @param params 请求参数(JSON格式字符串)
     * @param header 请求头信息(JSON格式字符串)
     * @return Map<String, Object> 调试结果，包含：
     *         <ul>
     *           <li>success：执行是否成功</li>
     *           <li>statusCode：HTTP状态码</li>
     *           <li>body：响应体内容</li>
     *           <li>error：错误信息(失败时)</li>
     *         </ul>
     * @since 1.0.0
     */
    Map<String, Object> debugApi(String path, String method, String params, String header);
}
