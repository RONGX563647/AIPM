package com.ai.dev.platform.modules.setting.service;

import com.ai.dev.platform.modules.setting.entity.UserSetting;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 用户设置服务接口
 * 
 * <p>定义用户设置管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>用户个性化设置的增删改查操作</li>
 *   <li>设置项的分类管理和批量操作</li>
 *   <li>默认设置的初始化和重置</li>
 *   <li>设置值的类型转换和验证</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了设置管理特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserSettingService extends IService<UserSetting> {
    
    /**
     * 获取用户所有设置
     * 
     * <p>查询指定用户的所有个性化设置配置，按分类组织返回。
     * 
     * @param userId 用户ID
     * @return List<UserSetting> 用户的所有设置记录列表
     * @since 1.0.0
     */
    List<UserSetting> getUserSettings(Long userId);
    
    /**
     * 获取用户设置Map格式
     * 
     * <p>查询指定用户的所有设置，并以键值对的形式返回，
     * 便于前端直接使用。
     * 
     * @param userId 用户ID
     * @return Map<String, Object> 设置键值对映射
     * @since 1.0.0
     */
    Map<String, Object> getUserSettingsMap(Long userId);
    
    /**
     * 获取用户指定分类的设置
     * 
     * <p>查询指定用户特定分类下的所有设置项。
     * 
     * @param userId 用户ID
     * @param category 设置分类
     * @return List<UserSetting> 指定分类的设置记录列表
     * @since 1.0.0
     */
    List<UserSetting> getUserSettingsByCategory(Long userId, String category);
    
    /**
     * 获取单个设置值
     * 
     * <p>获取指定用户的特定设置项的值。
     * 
     * @param userId 用户ID
     * @param settingKey 设置键名
     * @return String 设置值，未找到返回null
     * @since 1.0.0
     */
    String getUserSettingValue(Long userId, String settingKey);
    
    /**
     * 保存或更新用户设置
     * 
     * <p>保存用户的设置配置，如果已存在则更新，不存在则创建。
     * 
     * @param userId 用户ID
     * @param settingKey 设置键名
     * @param settingValue 设置值
     * @param settingType 设置值类型
     * @param category 设置分类
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean saveUserSetting(Long userId, String settingKey, String settingValue, String settingType, String category);
    
    /**
     * 批量保存用户设置
     * 
     * <p>批量保存用户的多个设置配置项。
     * 
     * @param userId 用户ID
     * @param settings 设置项映射(key-value对)
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean saveUserSettings(Long userId, Map<String, String> settings);
    
    /**
     * 删除用户设置
     * 
     * <p>删除指定用户的特定设置项。
     * 
     * @param userId 用户ID
     * @param settingKey 设置键名
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean deleteUserSetting(Long userId, String settingKey);
    
    /**
     * 重置用户默认设置
     * 
     * <p>为指定用户初始化或重置默认设置配置。
     * 
     * @param userId 用户ID
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean resetUserDefaultSettings(Long userId);
    
    /**
     * 获取系统默认设置
     * 
     * <p>获取系统预定义的默认设置配置模板。
     * 
     * @return Map<String, Object> 系统默认设置映射
     * @since 1.0.0
     */
    Map<String, Object> getSystemDefaultSettings();
    
    /**
     * 验证设置值
     * 
     * <p>验证设置值是否符合指定类型的要求。
     * 
     * @param settingValue 设置值
     * @param settingType 设置类型
     * @return boolean 验证是否通过
     * @since 1.0.0
     */
    boolean validateSettingValue(String settingValue, String settingType);
}