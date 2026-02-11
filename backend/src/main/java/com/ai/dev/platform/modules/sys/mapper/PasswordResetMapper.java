package com.ai.dev.platform.modules.sys.mapper;

import com.ai.dev.platform.modules.sys.entity.PasswordReset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 密码重置数据访问接口
 * 
 * <p>定义密码重置记录相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的密码重置数据CRUD操作和安全相关的查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的重置记录</li>
 *   <li>deleteById：根据ID删除重置记录</li>
 *   <li>updateById：根据ID更新重置状态</li>
 *   <li>selectById：根据ID查询重置记录</li>
 *   <li>selectList：查询重置记录列表</li>
 *   <li>selectPage：分页查询重置记录</li>
 * </ul>
 * 
 * <p><b>安全关键操作：</b>
 * <ul>
 *   <li>令牌验证查询需要高效索引支持</li>
 *   <li>过期令牌的清理需要定时任务</li>
 *   <li>敏感令牌数据的存储需要加密处理</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface PasswordResetMapper extends BaseMapper<PasswordReset> {
    // 继承自BaseMapper的所有方法都可直接使用
    // 如需自定义SQL查询，可在此接口中添加方法声明
}
