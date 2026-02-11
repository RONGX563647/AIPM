package com.ai.dev.platform.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 系统用户实体类
 * 
 * <p>映射数据库中的sys_user表，表示系统用户账户信息：
 * <ul>
 *   <li>用户基本身份信息（用户名、昵称）</li>
 *   <li>用户安全信息（密码哈希值）</li>
 *   <li>用户账户状态和创建时间</li>
 *   <li>用户权限和角色信息</li>
 * </ul>
 * 
 * <p>该实体类通过MyBatis-Plus注解配置数据库映射关系，
 * 用于用户认证、授权和账户管理等核心功能。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@TableName("sys_user")
public class SysUser {
    
    /**
     * 用户唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每个用户账户，支持精确查询和关联操作。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户登录名
     * 
     * <p>用户登录系统的唯一标识符，必须唯一且不为空。
     * 通常使用邮箱地址或自定义用户名格式。
     * 
     * @since 1.0.0
     */
    private String username;
    
    /**
     * 用户密码哈希值
     * 
     * <p>用户密码的安全存储形式，使用BCrypt等强哈希算法加密。
     * <b>安全注意事项：</b>
     * <ul>
     *   <li>数据库中永远不存储明文密码</li>
     *   <li>使用安全的哈希算法和盐值</li>
     *   <li>定期更新哈希算法以应对安全威胁</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    private String password;
    
    /**
     * 用户昵称/显示名称
     * 
     * <p>用户在系统中的显示名称，用于界面展示。
     * 可以与用户名相同，也可以是用户自定义的友好名称。
     * 
     * @since 1.0.0
     */
    private String nickname;

    /**
     * 账户创建时间
     * 
     * <p>用户账户的创建时间戳，使用LocalDateTime类型存储。
     * 用于账户生命周期管理、活跃度统计和安全审计。
     * 
     * @since 1.0.0
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 获取用户ID
     * 
     * @return Long 用户唯一标识
     * @since 1.0.0
     */
    public Long getId() { return id; }
    
    /**
     * 设置用户ID
     * 
     * @param id 用户唯一标识
     * @since 1.0.0
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * 获取用户名
     * 
     * @return String 用户登录名
     * @since 1.0.0
     */
    public String getUsername() { return username; }
    
    /**
     * 设置用户名
     * 
     * @param username 用户登录名
     * @since 1.0.0
     */
    public void setUsername(String username) { this.username = username; }
    
    /**
     * 获取密码哈希值
     * 
     * @return String 密码的哈希存储形式
     * @since 1.0.0
     */
    public String getPassword() { return password; }
    
    /**
     * 设置密码哈希值
     * 
     * @param password 密码的哈希存储形式
     * @since 1.0.0
     */
    public void setPassword(String password) { this.password = password; }
    
    /**
     * 获取用户昵称
     * 
     * @return String 用户显示名称
     * @since 1.0.0
     */
    public String getNickname() { return nickname; }
    
    /**
     * 设置用户昵称
     * 
     * @param nickname 用户显示名称
     * @since 1.0.0
     */
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    /**
     * 获取账户创建时间
     * 
     * @return LocalDateTime 账户创建时间
     * @since 1.0.0
     */
    public LocalDateTime getCreateTime() { return createTime; }
    
    /**
     * 设置账户创建时间
     * 
     * @param createTime 账户创建时间
     * @since 1.0.0
     */
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
