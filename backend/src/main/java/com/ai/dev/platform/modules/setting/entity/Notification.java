package com.ai.dev.platform.modules.setting.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 通知消息实体类
 * 
 * <p>映射数据库中的notification表，存储系统通知和消息信息：
 * <ul>
 *   <li>系统公告和通知</li>
 *   <li>任务状态变更提醒</li>
 *   <li>项目更新通知</li>
 *   <li>系统安全警告</li>
 *   <li>用户交互提醒</li>
 * </ul>
 * 
 * <p>该实体类通过MyBatis-Plus注解配置数据库映射关系，
 * 支持通知消息的存储、查询和状态管理。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@TableName("notification")
@Schema(description = "通知消息实体")
public class Notification {

    /**
     * 通知记录唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每条通知消息。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "通知记录唯一标识")
    private Long id;

    /**
     * 关联用户ID
     * 
     * <p>外键关联到sys_user表的主键，标识该通知发送给哪个用户。
     * 用于用户维度的通知管理和查询。
     * 
     * @since 1.0.0
     */
    @TableField("user_id")
    @Schema(description = "关联用户ID")
    private Long userId;

    /**
     * 通知标题
     * 
     * <p>通知消息的简要标题，用于通知列表展示。
     * 应该简洁明了，能够概括通知内容。
     * 
     * @since 1.0.0
     */
    @TableField("title")
    @Schema(description = "通知标题")
    private String title;

    /**
     * 通知内容
     * 
     * <p>通知消息的详细内容，支持HTML格式。
     * 包含具体的通知信息和相关说明。
     * 
     * @since 1.0.0
     */
    @TableField("content")
    @Schema(description = "通知内容")
    private String content;

    /**
     * 通知类型
     * 
     * <p>标识通知的分类，可能的值包括：
     * <ul>
     *   <li>system：系统通知</li>
     *   <li>project：项目相关通知</li>
     *   <li>task：任务相关通知</li>
     *   <li>alert：警告通知</li>
     *   <li>info：信息通知</li>
     *   <li>success：成功通知</li>
     *   <li>error：错误通知</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("type")
    @Schema(description = "通知类型")
    private String type;

    /**
     * 通知状态
     * 
     * <p>标识通知的处理状态，可能的值包括：
     * <ul>
     *   <li>unread：未读</li>
     *   <li>read：已读</li>
     *   <li>archived：已归档</li>
     *   <li>deleted：已删除</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("status")
    @Schema(description = "通知状态")
    private String status;

    /**
     * 通知优先级
     * 
     * <p>标识通知的重要程度，可能的值包括：
     * <ul>
     *   <li>low：低优先级</li>
     *   <li>medium：中优先级</li>
     *   <li>high：高优先级</li>
     *   <li>urgent：紧急</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("priority")
    @Schema(description = "通知优先级")
    private String priority;

    /**
     * 关联业务类型
     * 
     * <p>标识通知关联的业务模块，如：
     * project、task、api、deploy等。
     * 
     * @since 1.0.0
     */
    @TableField("related_type")
    @Schema(description = "关联业务类型")
    private String relatedType;

    /**
     * 关联业务ID
     * 
     * <p>关联的具体业务记录ID，用于跳转到相关页面。
     * 
     * @since 1.0.0
     */
    @TableField("related_id")
    @Schema(description = "关联业务ID")
    private Long relatedId;

    /**
     * 是否已读
     * 
     * <p>标识通知是否已被用户阅读：
     * <ul>
     *   <li>true：已读</li>
     *   <li>false：未读</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @TableField("is_read")
    @Schema(description = "是否已读")
    private Boolean isRead;

    /**
     * 阅读时间
     * 
     * <p>用户阅读通知的时间戳，未读时为null。
     * 
     * @since 1.0.0
     */
    @TableField("read_time")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    /**
     * 创建时间
     * 
     * <p>通知记录的创建时间戳，使用LocalDateTime类型存储。
     * 
     * @since 1.0.0
     */
    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // 构造函数
    public Notification() {}

    public Notification(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = "info";
        this.status = "unread";
        this.priority = "medium";
        this.isRead = false;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}