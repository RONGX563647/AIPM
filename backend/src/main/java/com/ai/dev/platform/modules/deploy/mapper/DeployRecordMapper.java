package com.ai.dev.platform.modules.deploy.mapper;

import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部署记录数据访问接口
 * 
 * <p>定义部署记录相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的CRUD操作和一些常用的查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新记录</li>
 *   <li>deleteById：根据ID删除记录</li>
 *   <li>updateById：根据ID更新记录</li>
 *   <li>selectById：根据ID查询记录</li>
 *   <li>selectList：查询记录列表</li>
 *   <li>selectPage：分页查询</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface DeployRecordMapper extends BaseMapper<DeployRecord> {
    // 继承自BaseMapper的所有方法都可直接使用
    // 如需自定义SQL查询，可在此接口中添加方法声明
}
