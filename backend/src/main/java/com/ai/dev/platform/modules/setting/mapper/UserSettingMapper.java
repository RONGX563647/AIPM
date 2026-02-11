package com.ai.dev.platform.modules.setting.mapper;

import com.ai.dev.platform.modules.setting.entity.UserSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户设置数据访问接口
 * 
 * <p>定义用户设置相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的用户设置数据CRUD操作和查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的设置记录</li>
 *   <li>deleteById：根据ID删除设置记录</li>
 *   <li>updateById：根据ID更新设置信息</li>
 *   <li>selectById：根据ID查询设置记录</li>
 *   <li>selectList：查询设置记录列表</li>
 *   <li>selectPage：分页查询设置记录</li>
 * </ul>
 * 
 * <p><b>自定义查询方法：</b>
 * <ul>
 *   <li>findByUserId：根据用户ID查询所有设置</li>
 *   <li>findByUserAndKey：根据用户ID和键名查询特定设置</li>
 *   <li>findByCategory：根据分类查询设置项</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface UserSettingMapper extends BaseMapper<UserSetting> {
    
    /**
     * 根据用户ID查询所有设置
     * 
     * <p>查询指定用户的所有个性化设置配置。
     * 
     * @param userId 用户ID
     * @return List<UserSetting> 用户的所有设置记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM user_setting WHERE user_id = #{userId}")
    List<UserSetting> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和键名查询特定设置
     * 
     * <p>查询指定用户的特定配置项，用于获取单个设置值。
     * 
     * @param userId 用户ID
     * @param settingKey 设置键名
     * @return UserSetting 对应的设置记录，未找到返回null
     * @since 1.0.0
     */
    @Select("SELECT * FROM user_setting WHERE user_id = #{userId} AND setting_key = #{settingKey}")
    UserSetting findByUserAndKey(@Param("userId") Long userId, @Param("settingKey") String settingKey);
    
    /**
     * 根据分类查询设置项
     * 
     * <p>查询指定分类下的所有设置项，用于分类管理界面。
     * 
     * @param userId 用户ID
     * @param category 设置分类
     * @return List<UserSetting> 指定分类的设置记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM user_setting WHERE user_id = #{userId} AND category = #{category}")
    List<UserSetting> findByCategory(@Param("userId") Long userId, @Param("category") String category);
    
    /**
     * 查询用户的默认设置
     * 
     * <p>查询指定用户的所有默认设置项。
     * 
     * @param userId 用户ID
     * @return List<UserSetting> 用户的默认设置记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM user_setting WHERE user_id = #{userId} AND is_default = true")
    List<UserSetting> findDefaultsByUserId(@Param("userId") Long userId);
}