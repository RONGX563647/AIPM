package com.ai.dev.platform.modules.setting.service.impl;

import com.ai.dev.platform.modules.setting.entity.UserSetting;
import com.ai.dev.platform.modules.setting.mapper.UserSettingMapper;
import com.ai.dev.platform.modules.setting.service.UserSettingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户设置服务实现类
 * 
 * <p>用户设置管理业务逻辑的具体实现，负责处理：
 * <ul>
 *   <li>用户个性化配置的存储和查询</li>
 *   <li>设置值的类型转换和验证</li>
 *   <li>默认设置的初始化和管理</li>
 *   <li>设置项的批量操作处理</li>
 * </ul>
 * 
 * <p>该实现类继承自MyBatis-Plus的ServiceImpl，利用其提供的
 * 基础CRUD操作，专注于设置管理的业务逻辑实现。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements UserSettingService {

    @Override
    public List<UserSetting> getUserSettings(Long userId) {
        return this.baseMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> getUserSettingsMap(Long userId) {
        List<UserSetting> settings = getUserSettings(userId);
        Map<String, Object> settingMap = new HashMap<>();
        
        for (UserSetting setting : settings) {
            Object value = parseSettingValue(setting.getSettingValue(), setting.getSettingType());
            settingMap.put(setting.getSettingKey(), value);
        }
        
        return settingMap;
    }

    @Override
    public List<UserSetting> getUserSettingsByCategory(Long userId, String category) {
        return this.baseMapper.findByCategory(userId, category);
    }

    @Override
    public String getUserSettingValue(Long userId, String settingKey) {
        UserSetting setting = this.baseMapper.findByUserAndKey(userId, settingKey);
        return setting != null ? setting.getSettingValue() : null;
    }

    @Override
    public boolean saveUserSetting(Long userId, String settingKey, String settingValue, String settingType, String category) {
        try {
            // 验证设置值
            if (!validateSettingValue(settingValue, settingType)) {
                return false;
            }
            
            // 检查设置是否已存在
            UserSetting existingSetting = this.baseMapper.findByUserAndKey(userId, settingKey);
            
            if (existingSetting != null) {
                // 更新现有设置
                existingSetting.setSettingValue(settingValue);
                existingSetting.setSettingType(settingType);
                existingSetting.setCategory(category);
                existingSetting.setIsDefault(false);
                return this.updateById(existingSetting);
            } else {
                // 创建新设置
                UserSetting newSetting = new UserSetting(userId, settingKey, settingValue);
                newSetting.setSettingType(settingType);
                newSetting.setCategory(category);
                newSetting.setIsDefault(false);
                return this.save(newSetting);
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean saveUserSettings(Long userId, Map<String, String> settings) {
        try {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                // 使用默认的string类型和general分类
                if (!saveUserSetting(userId, key, value, "string", "general")) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteUserSetting(Long userId, String settingKey) {
        try {
            QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            wrapper.eq("setting_key", settingKey);
            return this.remove(wrapper);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean resetUserDefaultSettings(Long userId) {
        try {
            // 删除用户现有设置
            QueryWrapper<UserSetting> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId);
            deleteWrapper.eq("is_default", false);
            this.remove(deleteWrapper);
            
            // 初始化默认设置
            Map<String, Object> defaultSettings = getSystemDefaultSettings();
            for (Map.Entry<String, Object> entry : defaultSettings.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                UserSetting setting = new UserSetting(userId, key, value.toString());
                setting.setSettingType("string");
                setting.setCategory(getDefaultCategory(key));
                setting.setIsDefault(true);
                this.save(setting);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getSystemDefaultSettings() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("theme", "light");
        defaults.put("language", "zh-CN");
        defaults.put("notifications.enabled", true);
        defaults.put("notifications.email", true);
        defaults.put("auto_save", true);
        defaults.put("sidebar_collapsed", false);
        defaults.put("items_per_page", 10);
        defaults.put("timezone", "Asia/Shanghai");
        return defaults;
    }

    @Override
    public boolean validateSettingValue(String settingValue, String settingType) {
        if (settingValue == null) {
            return false;
        }
        
        try {
            switch (settingType.toLowerCase()) {
                case "boolean":
                    return "true".equals(settingValue) || "false".equals(settingValue);
                case "number":
                    Double.parseDouble(settingValue);
                    return true;
                case "string":
                    return true; // 字符串类型基本都有效
                case "json":
                    // 简单的JSON格式验证
                    return settingValue.startsWith("{") && settingValue.endsWith("}") ||
                           settingValue.startsWith("[") && settingValue.endsWith("]");
                default:
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据设置键名确定默认分类
     */
    private String getDefaultCategory(String settingKey) {
        if (settingKey.startsWith("theme") || settingKey.startsWith("language") || settingKey.startsWith("sidebar")) {
            return "appearance";
        } else if (settingKey.startsWith("notifications") || settingKey.startsWith("email")) {
            return "notification";
        } else if (settingKey.startsWith("auto_save") || settingKey.startsWith("items_per_page")) {
            return "general";
        } else {
            return "general";
        }
    }

    /**
     * 解析设置值为对应的Java对象类型
     */
    private Object parseSettingValue(String value, String type) {
        if (value == null) {
            return null;
        }
        
        try {
            switch (type.toLowerCase()) {
                case "boolean":
                    return Boolean.parseBoolean(value);
                case "number":
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        return Double.parseDouble(value);
                    }
                case "json":
                    // 简单返回字符串，实际应用中可使用JSON库解析
                    return value;
                default:
                    return value;
            }
        } catch (Exception e) {
            return value;
        }
    }
}