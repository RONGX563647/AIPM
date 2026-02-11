package com.ai.dev.platform.modules.setting.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.setting.entity.UserSetting;
import com.ai.dev.platform.modules.setting.service.UserSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户设置管理控制器
 * 
 * <p>负责处理用户个性化设置相关的HTTP请求，包括：
 * <ul>
 *   <li>用户设置的查询和管理</li>
 *   <li>设置项的增删改操作</li>
 *   <li>默认设置的初始化和重置</li>
 *   <li>设置分类的管理</li>
 * </ul>
 * 
 * <p>该控制器提供RESTful风格的API接口，支持用户个性化配置的完整管理功能。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "用户设置管理", description = "用户个性化设置管理接口")
@RestController
@RequestMapping("/setting/user")
public class UserSettingController {

    @Autowired
    private UserSettingService userSettingService;

    /**
     * 获取用户所有设置
     * 
     * <p>查询当前登录用户的所有个性化设置配置。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<List<UserSetting>> 用户设置列表
     * @since 1.0.0
     */
    @Operation(summary = "获取用户所有设置", description = "查询用户的所有个性化配置")
    @GetMapping("/list")
    public Result<List<UserSetting>> getUserSettings(@RequestHeader("userId") Long userId) {
        List<UserSetting> settings = userSettingService.getUserSettings(userId);
        return Result.ok(settings);
    }

    /**
     * 获取用户设置（Map格式）
     * 
     * <p>查询当前登录用户的所有设置，并以键值对形式返回。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<Map<String, Object>> 用户设置键值对
     * @since 1.0.0
     */
    @Operation(summary = "获取用户设置Map", description = "查询用户设置并以键值对形式返回")
    @GetMapping("/map")
    public Result<Map<String, Object>> getUserSettingsMap(@RequestHeader("userId") Long userId) {
        Map<String, Object> settings = userSettingService.getUserSettingsMap(userId);
        return Result.ok(settings);
    }

    /**
     * 获取用户指定分类的设置
     * 
     * <p>查询当前登录用户指定分类下的所有设置项。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param category 设置分类
     * @return Result<List<UserSetting>> 指定分类的设置列表
     * @since 1.0.0
     */
    @Operation(summary = "获取分类设置", description = "查询用户指定分类的设置项")
    @GetMapping("/category/{category}")
    public Result<List<UserSetting>> getUserSettingsByCategory(
            @RequestHeader("userId") Long userId,
            @PathVariable String category) {
        List<UserSetting> settings = userSettingService.getUserSettingsByCategory(userId, category);
        return Result.ok(settings);
    }

    /**
     * 获取单个设置值
     * 
     * <p>获取当前登录用户特定设置项的值。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param settingKey 设置键名
     * @return Result<String> 设置值
     * @since 1.0.0
     */
    @Operation(summary = "获取设置值", description = "获取用户特定设置项的值")
    @GetMapping("/value/{settingKey}")
    public Result<String> getUserSettingValue(
            @RequestHeader("userId") Long userId,
            @PathVariable String settingKey) {
        String value = userSettingService.getUserSettingValue(userId, settingKey);
        return Result.ok(value);
    }

    /**
     * 保存用户设置
     * 
     * <p>保存或更新当前登录用户的设置配置。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param settingKey 设置键名
     * @param settingValue 设置值
     * @param settingType 设置类型
     * @param category 设置分类
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "保存用户设置", description = "保存或更新用户设置配置")
    @PostMapping("/save")
    public Result<String> saveUserSetting(
            @RequestHeader("userId") Long userId,
            @RequestParam String settingKey,
            @RequestParam String settingValue,
            @RequestParam(defaultValue = "string") String settingType,
            @RequestParam(defaultValue = "general") String category) {
        
        boolean success = userSettingService.saveUserSetting(userId, settingKey, settingValue, settingType, category);
        if (success) {
            return Result.ok("设置保存成功");
        } else {
            return Result.error("设置保存失败");
        }
    }

    /**
     * 批量保存用户设置
     * 
     * <p>批量保存当前登录用户的多个设置配置项。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param settings 设置项映射
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "批量保存设置", description = "批量保存用户设置配置")
    @PostMapping("/batch-save")
    public Result<String> saveUserSettings(
            @RequestHeader("userId") Long userId,
            @RequestBody Map<String, String> settings) {
        
        boolean success = userSettingService.saveUserSettings(userId, settings);
        if (success) {
            return Result.ok("批量设置保存成功");
        } else {
            return Result.error("批量设置保存失败");
        }
    }

    /**
     * 删除用户设置
     * 
     * <p>删除当前登录用户的特定设置项。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param settingKey 设置键名
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "删除用户设置", description = "删除用户特定设置项")
    @DeleteMapping("/delete/{settingKey}")
    public Result<String> deleteUserSetting(
            @RequestHeader("userId") Long userId,
            @PathVariable String settingKey) {
        
        boolean success = userSettingService.deleteUserSetting(userId, settingKey);
        if (success) {
            return Result.ok("设置删除成功");
        } else {
            return Result.error("设置删除失败");
        }
    }

    /**
     * 重置用户默认设置
     * 
     * <p>为当前登录用户初始化或重置默认设置配置。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "重置默认设置", description = "初始化或重置用户默认设置")
    @PostMapping("/reset-defaults")
    public Result<String> resetUserDefaultSettings(@RequestHeader("userId") Long userId) {
        boolean success = userSettingService.resetUserDefaultSettings(userId);
        if (success) {
            return Result.ok("默认设置重置成功");
        } else {
            return Result.error("默认设置重置失败");
        }
    }

    /**
     * 获取系统默认设置
     * 
     * <p>获取系统预定义的默认设置配置模板。
     * 
     * @return Result<Map<String, Object>> 系统默认设置
     * @since 1.0.0
     */
    @Operation(summary = "获取系统默认设置", description = "获取系统预定义的默认配置")
    @GetMapping("/system-defaults")
    public Result<Map<String, Object>> getSystemDefaultSettings() {
        Map<String, Object> defaults = userSettingService.getSystemDefaultSettings();
        return Result.ok(defaults);
    }
}