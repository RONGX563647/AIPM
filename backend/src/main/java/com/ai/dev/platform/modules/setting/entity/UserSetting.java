package com.ai.dev.platform.modules.setting.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 用户设置实体类
 * 
 * <p>映射数据库中的user_setting表，存储用户的个性化配置信息：
 * <ul>
 *   <li>用户界面偏好设置</li>
 *   <li>通知接收偏好</li>
 *   <li>系统功能开关配置</li>
 *   <li>个人工作习惯配置</li>
 * </ul>
 * 
 * <p>该实体类通过MyBatis-Plus注解配置数据库映射关系，
 * 支持用户个性化配置的存储和管理。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@TableName("user_setting")
@Schema(description = "用户设置实体")
public class UserSetting {

    /**
     * 设置记录唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每个设置记录。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "设置记录唯一标识")
    private Long id;

    /**
     * 关联用户ID
     * 
     * <p>外键关联到sys_user表的主键，标识该设置属于哪个用户。
     * 用于用户维度的设置管理和查询。
     * 
     * @since 1.0.0
     */
    @TableField("user_id")
    @Schema(description = "关联用户ID")
    private Long userId;

    /**
     * 设置键名
     * 
     * <p>设置项的唯一标识符，用于区分不同的配置项。
     * 常见的键名包括：theme、language、notifications、auto_save等。
     * 
     * @since 1.0.0
     */
    @TableField("setting_key")
    @Schema(description = "设置键名")
    private String settingKey;

    /**
     * 设置值
     * 
     * <p>配置项的具体值，可以是字符串、JSON或其他格式。
     * 根据setting_type字段决定具体的解析方式。
     * 
     * @since 1.0.0
     */
    @TableField("setting_value")
    @Schema(description = "设置值")
    private String settingValue;

    /**
     * 设置值类型
     * 
     * <p>标识设置值的数据类型，可能的值包括：
     * <ul>
     *   <li>string：字符串类型</li>
     *   <li>number：数字类型</li>
     *   <li>boolean：布尔类型</li>
     *   <li>json：JSON对象类型</li>
     *   <li>array：数组类型</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("setting_type")
    @Schema(description = "设置值类型")
    private String settingType;

    /**
     * 设置分类
     * 
     * <p>对设置项进行分类管理，常见的分类包括：
     * <ul>
     *   <li>general：通用设置</li>
     *   <li>appearance：外观设置</li>
     *   <li>notification：通知设置</li>
     *   <li>security：安全设置</li>
     *   <li>privacy：隐私设置</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("category")
    @Schema(description = "设置分类")
    private String category;

    /**
     * 是否为默认设置
     * 
     * <p>标识该设置是否为系统默认配置：
     * <ul>
     *   <li>true：系统默认设置</li>
     *   <li>false：用户自定义设置</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("is_default")
    @Schema(description = "是否为默认设置")
    private Boolean isDefault;

    /**
     * 创建时间
     * 
     * <p>设置记录的创建时间戳，使用LocalDateTime类型存储。
     * 
     * @since 1.0.0
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 
     * <p>设置记录的最后更新时间戳，使用LocalDateTime类型存储。
     * 
     * @since 1.0.0
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // 构造函数
    public UserSetting() {}

    public UserSetting(Long userId, String settingKey, String settingValue) {
        this.userId = userId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.settingType = "string";
        this.category = "general";
        this.isDefault = false;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}