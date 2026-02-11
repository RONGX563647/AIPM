package com.ai.dev.platform.modules.sys.service;

/**
 * 系统用户服务接口
 * 
 * <p>定义用户认证管理的核心业务逻辑，包括：
 * <ul>
 *   <li>用户身份验证</li>
 *   <li>用户账户管理</li>
 *   <li>密码安全策略</li>
 *   <li>用户信息查询</li>
 * </ul>
 * 
 * <p>该服务层负责实现业务逻辑与数据访问层的解耦
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SysUserService {
    
    /**
     * 用户登录认证
     * 
     * <p>验证用户凭据并返回JWT访问令牌，实现流程：
     * <ol>
     *   <li>查询用户信息</li>
     *   <li>验证密码匹配</li>
     *   <li>生成JWT令牌</li>
     *   <li>记录登录日志</li>
     * </ol>
     * 
     * @param username 用户名
     * @param password 用户密码（明文）
     * @return String JWT访问令牌，验证失败返回null
     * @since 1.0.0
     */
    String login(String username, String password);

    /**
     * 用户注册
     * 
     * <p>创建新用户账户，执行安全验证：
     * <ul>
     *   <li>检查用户名唯一性</li>
     *   <li>验证密码复杂度</li>
     *   <li>密码加密存储</li>
     *   <li>分配默认权限</li>
     * </ul>
     * 
     * @param username 用户名
     * @param password 用户密码（明文）
     * @param nickname 用户昵称
     * @return boolean 注册成功返回true，失败返回false
     * @since 1.0.0
     */
    boolean register(String username, String password, String nickname);

    /**
     * 发起密码重置流程
     * 
     * <p>生成密码重置令牌用于邮箱验证，实现安全控制：
     * <ul>
     *   <li>验证用户存在性</li>
     *   <li>生成安全的重置令牌</li>
     *   <li>设置令牌有效期</li>
     *   <li>通过邮箱发送重置链接</li>
     * </ul>
     * 
     * @param username 用户名
     * @return String 密码重置令牌，用户不存在返回null
     * @since 1.0.0
     */
    String forgotPassword(String username);

    /**
     * 通过令牌重置密码
     * 
     * <p>使用重置令牌验证用户身份并更新密码，保障安全：
     * <ul>
     *   <li>验证令牌有效性和时效性</li>
     *   <li>验证新密码强度要求</li>
     *   <li>安全更新密码哈希值</li>
     *   <li>作废使用的令牌</li>
     * </ul>
     * 
     * @param token 重置令牌
     * @param newPassword 新密码（明文）
     * @return boolean 密码重置成功返回true，失败返回false
     * @since 1.0.0
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * 根据用户名查找用户
     * 
     * <p>用于内部查询，不会暴露用户密码信息
     * 
     * @param username 用户名
     * @return SysUser 用户实体对象，未找到返回null
     * @since 1.0.0
     */
    com.ai.dev.platform.modules.sys.entity.SysUser findByUsername(String username);

}
