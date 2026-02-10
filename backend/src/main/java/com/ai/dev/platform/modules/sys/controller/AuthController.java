package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/sys/user")
@Tag(name = "auth", description = "注册/找回/社交登录")
public class AuthController {
    private final SysUserService sysUserService;
    private final com.ai.dev.platform.modules.sys.service.OAuthStateStore oauthStateStore;

    @Value("${github.client-id:}")
    private String githubClientId;

    @Value("${github.redirect-uri:http://localhost:8080/api/oauth/github/callback}")
    private String githubRedirectUri;

    public AuthController(SysUserService sysUserService, com.ai.dev.platform.modules.sys.service.OAuthStateStore oauthStateStore) {
        this.sysUserService = sysUserService;
        this.oauthStateStore = oauthStateStore;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        boolean ok = sysUserService.register(request.getUsername(), request.getPassword(), request.getNickname());
        if (!ok) return Result.error("注册失败，用户名已存在");
        return Result.ok(null);
    }

    @PostMapping("/forgot")
    @Operation(summary = "忘记密码：生成重置链接（会在服务端日志打印）")
    public Result<String> forgot(@RequestBody ForgotRequest request) {
        String token = sysUserService.forgotPassword(request.getUsername());
        if (token == null) return Result.error("用户不存在");
        return Result.ok(token);
    }

    @PostMapping("/reset")
    @Operation(summary = "重置密码")
    public Result<Void> reset(@RequestBody ResetRequest request) {
        boolean ok = sysUserService.resetPassword(request.getToken(), request.getNewPassword());
        if (!ok) return Result.error("重置失败，token无效或已过期");
        return Result.ok(null);
    }

    // 注册请求DTO
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

    // 忘记密码请求DTO
    public static class ForgotRequest {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // 重置密码请求DTO
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

    @GetMapping("/oauth/github/authorize")
    @Operation(summary = "获取 GitHub 授权重定向 URL")
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
