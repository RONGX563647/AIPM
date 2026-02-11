package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 系统认证控制器
 * 
 * <p>负责处理用户身份认证相关的核心功能，包括：
 * <ul>
 *   <li>用户账户注册</li>
 *   <li>密码找回和重置</li>
 *   <li>第三方OAuth认证</li>
 *   <li>用户信息管理</li>
 * </ul>
 * 
 * <p>该控制器提供完整的用户认证管理功能，支持：
 * <ul>
 *   <li>本地用户认证</li>
 *   <li>社交账号登录</li>
 *   <li>密码安全策略</li>
 *   <li>多因素认证扩展</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sys/user")
@Tag(name = "用户认证", description = "用户注册/密码找回/第三方登录")
public class AuthController {
    
    private final SysUserService sysUserService;
    private final com.ai.dev.platform.modules.sys.service.OAuthStateStore oauthStateStore;

    @Value("${github.client-id:}")
    private String githubClientId;

    @Value("${github.redirect-uri:http://localhost:8080/api/oauth/github/callback}")
    private String githubRedirectUri;

    /**
     * 构造函数注入依赖服务
     * 
     * @param sysUserService 系统用户服务实例
     * @param oauthStateStore OAuth状态存储服务实例
     * @since 1.0.0
     */
    public AuthController(SysUserService sysUserService, com.ai.dev.platform.modules.sys.service.OAuthStateStore oauthStateStore) {
        this.sysUserService = sysUserService;
        this.oauthStateStore = oauthStateStore;
    }

    /**
     * 用户注册接口
     * 
     * <p>创建新用户账户，执行安全验证流程：
     * <ul>
     *   <li>验证用户名唯一性</li>
     *   <li>验证密码复杂度要求</li>
     *   <li>密码加密存储</li>
     *   <li>分配默认用户权限</li>
     *   <li>发送欢迎邮件（可选）</li>
     * </ul>
     * 
     * @param request 注册请求对象，包含用户名、密码、昵称
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        boolean ok = sysUserService.register(request.getUsername(), request.getPassword(), request.getNickname());
        if (!ok) return Result.error("注册失败，用户名已存在");
        return Result.ok(null);
    }

    /**
     * 忘记密码接口
     * 
     * <p>发起密码重置流程，生成安全的重置令牌：
     * <ul>
     *   <li>验证用户账户存在性</li>
     *   <li>生成加密的重置令牌</li>
     *   <li>设置令牌有效期（通常24小时）</li>
     *   <li>通过邮件发送重置链接</li>
     * </ul>
     * 
     * @param request 忘记密码请求对象，包含用户名
     * @return Result<String> 操作结果，成功返回重置令牌，失败返回错误信息
     * @since 1.0.0
     */
    @PostMapping("/forgot")
    @Operation(summary = "忘记密码", description = "生成密码重置链接（会在服务端日志打印）")
    public Result<String> forgot(@RequestBody ForgotRequest request) {
        String token = sysUserService.forgotPassword(request.getUsername());
        if (token == null) return Result.error("用户不存在");
        return Result.ok(token);
    }

    /**
     * 重置密码接口
     * 
     * <p>使用重置令牌更新用户密码，保障安全：
     * <ul>
     *   <li>验证重置令牌有效性和时效性</li>
     *   <li>验证新密码强度要求</li>
     *   <li>安全更新密码哈希值</li>
     *   <li>作废已使用的令牌</li>
     *   <li>记录密码变更日志</li>
     * </ul>
     * 
     * @param request 重置密码请求对象，包含令牌和新密码
     * @return Result<Void> 操作结果，成功返回空数据，失败返回错误信息
     * @since 1.0.0
     */
    @PostMapping("/reset")
    @Operation(summary = "重置密码", description = "使用令牌更新用户密码")
    public Result<Void> reset(@RequestBody ResetRequest request) {
        boolean ok = sysUserService.resetPassword(request.getToken(), request.getNewPassword());
        if (!ok) return Result.error("重置失败，token无效或已过期");
        return Result.ok(null);
    }

    /**
     * 用户注册请求数据传输对象
     * 
     * <p>用于封装用户注册时提交的数据，包含：
     * <ul>
     *   <li>用户名（唯一标识）</li>
     *   <li>密码（明文，将在服务端加密）</li>
     *   <li>用户昵称（显示名称）</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String nickname;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    /**
     * 忘记密码请求数据传输对象
     * 
     * <p>用于封装忘记密码时提交的用户名信息
     * 
     * @since 1.0.0
     */
    public static class ForgotRequest {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    /**
     * 重置密码请求数据传输对象
     * 
     * <p>用于封装密码重置时的令牌和新密码信息，包含：
     * <ul>
     *   <li>重置令牌（由系统生成的安全令牌）</li>
     *   <li>新密码（明文，将在服务端加密）</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    public static class ResetRequest {
        private String token;
        private String newPassword;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    /**
     * 获取GitHub授权重定向URL
     * 
     * <p>生成GitHub OAuth授权链接，实现第三方登录：
     * <ul>
     *   <li>验证GitHub OAuth配置完整性</li>
     *   <li>生成安全的状态参数防止CSRF攻击</li>
     *   <li>构建标准的OAuth授权URL</li>
     *   <li>包含必要的授权范围（scope）</li>
     * </ul>
     * 
     * @return Result<String> GitHub授权链接，客户端需要重定向到此URL
     * @since 1.0.0
     */
    @GetMapping("/oauth/github/authorize")
    @Operation(summary = "获取GitHub授权链接", description = "生成GitHub OAuth授权重定向URL")
    public Result<String> getGithubAuthorizeUrl() {
        if (githubClientId == null || githubClientId.isBlank()) return Result.error("GitHub OAuth 未配置");
        String state = oauthStateStore.createState();
        String url = String.format("https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                URLEncoder.encode(githubClientId, StandardCharsets.UTF_8),
                URLEncoder.encode(githubRedirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode("user:email", StandardCharsets.UTF_8),
                URLEncoder.encode(state, StandardCharsets.UTF_8));
        return Result.ok(url);
    }
}
