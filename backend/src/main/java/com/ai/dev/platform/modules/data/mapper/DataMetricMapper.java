package com.ai.dev.platform.modules.data.mapper;

import com.ai.dev.platform.modules.data.entity.DataMetric;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据指标数据访问接口
 * 
 * <p>定义数据指标相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的数据指标CRUD操作和统计查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的数据指标记录</li>
 *   <li>deleteById：根据ID删除指标记录</li>
 *   <li>updateById：根据ID更新指标信息</li>
 *   <li>selectById：根据ID查询指标记录</li>
 *   <li>selectList：查询指标记录列表</li>
 *   <li>selectPage：分页查询指标记录</li>
 * </ul>
 * 
 * <p><b>性能优化建议：</b>
 * <ul>
 *   <li>为category、timestamp字段建立索引</li>
 *   <li>对于统计查询考虑使用数据库视图</li>
 *   <li>大数据量时考虑分区表设计</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface DataMetricMapper extends BaseMapper<DataMetric> {
    // 继承自BaseMapper的所有方法都可直接使用
    // 如需自定义SQL查询，可在此接口中添加方法声明
}
