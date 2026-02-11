package com.ai.dev.platform.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 密码重置实体类
 * 
 * <p>映射数据库中的password_reset表，用于管理用户密码重置流程：
 * <ul>
 *   <li>重置令牌的生成和存储</li>
 *   <li>令牌的有效期管理</li>
 *   <li>重置状态跟踪</li>
 *   <li>安全审计信息</li>
 * </ul>
 * 
 * <p>该实体类支持安全的密码重置机制，包含：
 * <ul>
 *   <li>一次性使用的重置令牌</li>
 *   <li>时间限制的令牌有效期</li>
 *   <li>使用状态跟踪防止重复使用</li>
 *   <li>关联用户身份验证</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@TableName("password_reset")
public class PasswordReset {
    
    /**
     * 重置记录唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每条密码重置记录。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 关联用户ID
     * 
     * <p>外键关联到sys_user表的主键，标识该重置请求属于哪个用户。
     * 用于用户身份验证和重置权限控制。
     * 
     * @since 1.0.0
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 重置令牌
     * 
     * <p>用于密码重置的安全令牌，具有以下特征：
     * <ul>
     *   <li>随机生成的高熵值字符串</li>
     *   <li>一次性使用，使用后立即失效</li>
     *   <li>通过URL参数传递给用户</li>
     *   <li>数据库中加密存储</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    private String token;
    
    /**
     * 令牌过期时间
     * 
     * <p>重置令牌的有效截止时间，通常设置为15-30分钟。
     * 超过此时间的令牌将被视为无效，防止安全风险。
     * 
     * @since 1.0.0
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;
    
    /**
     * 令牌使用状态
     * 
     * <p>标记重置令牌是否已被使用：
     * <ul>
     *   <li>true：令牌已使用，不可再次使用</li>
     *   <li>false：令牌未使用，可正常使用</li>
     * </ul>
     * 用于防止令牌重复使用造成安全问题。
     * 
     * @since 1.0.0
     */
    private Boolean used;

    /**
     * 获取重置记录ID
     * 
     * @return Long 重置记录唯一标识
     * @since 1.0.0
     */
    public Long getId() { return id; }
    
    /**
     * 设置重置记录ID
     * 
     * @param id 重置记录唯一标识
     * @since 1.0.0
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取关联用户ID
     * 
     * @return Long 关联用户唯一标识
     * @since 1.0.0
     */
    public Long getUserId() { return userId; }
    
    /**
     * 设置关联用户ID
     * 
     * @param userId 关联用户唯一标识
     * @since 1.0.0
     */
    public void setUserId(Long userId) { this.userId = userId; }
    
    /**
     * 获取重置令牌
     * 
     * @return String 重置令牌字符串
     * @since 1.0.0
     */
    public String getToken() { return token; }
    
    /**
     * 设置重置令牌
     * 
     * @param token 重置令牌字符串
     * @since 1.0.0
     */
    public void setToken(String token) { this.token = token; }
    
    /**
     * 获取令牌过期时间
     * 
     * @return LocalDateTime 令牌过期时间
     * @since 1.0.0
     */
    public LocalDateTime getExpireTime() { return expireTime; }
    
    /**
     * 设置令牌过期时间
     * 
     * @param expireTime 令牌过期时间
     * @since 1.0.0
     */
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    
    /**
     * 获取令牌使用状态
     * 
     * @return Boolean 令牌使用状态
     * @since 1.0.0
     */
    public Boolean getUsed() { return used; }
    
    /**
     * 设置令牌使用状态
     * 
     * @param used 令牌使用状态
     * @since 1.0.0
     */
    public void setUsed(Boolean used) { this.used = used; }
}
