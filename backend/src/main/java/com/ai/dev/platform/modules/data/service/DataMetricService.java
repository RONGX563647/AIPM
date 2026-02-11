package com.ai.dev.platform.modules.data.service;

import com.ai.dev.platform.modules.data.entity.DataMetric;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 数据指标服务接口
 * 
 * <p>定义数据指标管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>数据指标的增删改查操作</li>
 *   <li>指标数据的统计分析功能</li>
 *   <li>按类别和名称的筛选查询</li>
 *   <li>指标趋势和分布统计</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了数据统计和分析特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DataMetricService extends IService<DataMetric> {
    
    /**
     * 分页查询数据指标
     * 
     * <p>支持多条件组合筛选的数据指标分页查询功能：
     * <ul>
     *   <li>按指标类别精确匹配筛选</li>
     *   <li>按指标名称模糊匹配筛选</li>
     *   <li>按时间戳倒序排列</li>
     *   <li>返回指标详细信息</li>
     * </ul>
     * 
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数，建议范围1-100
     * @param category 指标类别，用于分类筛选（可选）
     * @param name 指标名称，支持模糊匹配查询（可选）
     * @return IPage<DataMetric> 分页查询结果，包含记录列表和分页信息
     * @since 1.0.0
     */
    IPage<DataMetric> getMetricPage(Integer pageNum, Integer pageSize, String category, String name);
    
    /**
     * 获取类别统计信息
     * 
     * <p>统计各指标类别的聚合信息，包括：
     * <ul>
     *   <li>每个类别的指标数量统计</li>
     *   <li>指标值的平均值计算</li>
     *   <li>指标值的最大值和最小值</li>
     *   <li>数据分布情况分析</li>
     * </ul>
     * 
     * <p><b>统计计算逻辑：</b>
     * <ol>
     *   <li>获取所有不重复的指标类别</li>
     *   <li>按类别分组查询所有指标数据</li>
     *   <li>计算每个类别的统计指标</li>
     *   <li>汇总结果并格式化返回</li>
     * </ol>
     * 
     * @return List<Map<String, Object>> 类别统计信息列表，每项包含：
     *         <ul>
     *           <li>category：类别名称</li>
     *           <li>count：该类别下的指标数量</li>
     *           <li>avgValue：平均值</li>
     *           <li>maxValue：最大值</li>
     *           <li>minValue：最小值</li>
     *         </ul>
     * @since 1.0.0
     */
    List<Map<String, Object>> getCategoryStats();
}
