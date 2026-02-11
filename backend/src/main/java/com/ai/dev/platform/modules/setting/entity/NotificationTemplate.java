package com.ai.dev.platform.modules.setting.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知模板实体类
 * 
 * <p>映射数据库中的notification_template表，存储系统通知模板：
 * <ul>
 *   <li>系统预定义通知模板</li>
 *   <li>可复用的通知消息格式</li>
 *   <li>支持参数化的内容模板</li>
 *   <li>模板启用状态管理</li>
 * </ul>
 * 
 * <p>该实体类通过MyBatis-Plus注解配置数据库映射关系，
 * 支持通知模板的存储、管理和复用。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("notification_template")
@Schema(description = "通知模板实体")
public class NotificationTemplate {

    /**
     * 模板记录唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每个通知模板。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "模板记录唯一标识")
    private Long id;

    /**
     * 模板编码
     * 
     * <p>模板的唯一标识符，用于程序引用。
     * 格式通常为：模块_操作_类型，如：project_create_success
     * 
     * @since 1.0.0
     */
    @TableField("template_code")
    @Schema(description = "模板编码")
    private String templateCode;

    /**
     * 模板名称
     * 
     * <p>模板的显示名称，用于管理界面展示。
     * 应该简洁明了，描述模板用途。
     * 
     * @since 1.0.0
     */
    @TableField("template_name")
    @Schema(description = "模板名称")
    private String templateName;

    /**
     * 标题模板
     * 
     * <p>通知标题的模板字符串，支持参数占位符。
     * 例如："{projectName}项目创建成功"
     * 
     * @since 1.0.0
     */
    @TableField("title_template")
    @Schema(description = "标题模板")
    private String titleTemplate;

    /**
     * 内容模板
     * 
     * <p>通知内容的模板字符串，支持HTML和参数占位符。
     * 可以包含丰富的格式和参数替换。
     * 
     * @since 1.0.0
     */
    @TableField("content_template")
    @Schema(description = "内容模板")
    private String contentTemplate;

    /**
     * 模板类型
     * 
     * <p>标识模板的分类，可能的值包括：
     * <ul>
     *   <li>system：系统模板</li>
     *   <li>project：项目模板</li>
     *   <li>task：任务模板</li>
     *   <li>api：接口模板</li>
     *   <li>deploy：部署模板</li>
     *   <li>custom：自定义模板</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("type")
    @Schema(description = "模板类型")
    private String type;

    /**
     * 是否启用
     * 
     * <p>标识模板是否处于启用状态：
     * <ul>
     *   <li>true：启用，可被使用</li>
     *   <li>false：禁用，不可使用</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("enabled")
    @Schema(description = "是否启用")
    private Boolean enabled;

    /**
     * 创建时间
     * 
     * <p>模板记录的创建时间戳，使用LocalDateTime类型存储。
     * 
     * @since 1.0.0
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 
     * <p>模板记录的最后更新时间戳，使用LocalDateTime类型存储。
     * 
     * @since 1.0.0
     */
    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // 构造函数
    public NotificationTemplate() {}

    public NotificationTemplate(String templateCode, String templateName, String titleTemplate, String contentTemplate) {
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.titleTemplate = titleTemplate;
        this.contentTemplate = contentTemplate;
        this.type = "system";
        this.enabled = true;
    }

    // Getter和Setter方法由Lombok的@Data注解自动生成
    
    // 手动添加getter/setter方法以确保编译通过
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public String getTitleTemplate() { return titleTemplate; }
    public void setTitleTemplate(String titleTemplate) { this.titleTemplate = titleTemplate; }
    
    public String getContentTemplate() { return contentTemplate; }
    public void setContentTemplate(String contentTemplate) { this.contentTemplate = contentTemplate; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}