package com.ai.dev.platform.modules.sys.mapper;

import com.ai.dev.platform.modules.sys.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户数据访问接口
 * 
 * <p>定义系统用户相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的用户数据CRUD操作和常用的查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新用户</li>
 *   <li>deleteById：根据ID删除用户</li>
 *   <li>updateById：根据ID更新用户信息</li>
 *   <li>selectById：根据ID查询用户</li>
 *   <li>selectList：查询用户列表</li>
 *   <li>selectPage：分页查询用户</li>
 * </ul>
 * 
 * <p><b>安全注意事项：</b>
 * <ul>
 *   <li>密码字段的处理需要特别注意安全性</li>
 *   <li>用户敏感信息的查询需要权限控制</li>
 *   <li>批量操作需要考虑性能和安全影响</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承自BaseMapper的所有方法都可直接使用
    // 如需自定义SQL查询，可在此接口中添加方法声明
}
