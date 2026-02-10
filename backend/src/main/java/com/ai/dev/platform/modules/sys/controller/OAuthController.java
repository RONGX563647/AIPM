package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.entity.SysUser;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import com.ai.dev.platform.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth/github")
@Tag(name = "oauth", description = "GitHub OAuth 回调（简化实现）")
public class OAuthController {
    @Value("${github.client-id:}")
    private String clientId;
    @Value("${github.client-secret:}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;

    @Value("${frontend.redirect-uri:http://localhost:5173}")
    private String frontendRedirect;

    public OAuthController(SysUserService sysUserService, JwtUtil jwtUtil) {
        this.sysUserService = sysUserService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/callback")
    @Operation(summary = "GitHub 授权回调（会尝试用 code 换取用户信息并自动注册/登录）")
    public ResponseEntity<Void> callback(@RequestParam String code) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        // exchange code for access token
        String tokenUrl = "https://github.com/login/oauth/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Map.of("client_id", clientId, "client_secret", clientSecret, "code", code);
        RequestEntity<Map<String, String>> req = RequestEntity.post(URI.create(tokenUrl)).contentType(MediaType.APPLICATION_JSON).body(body);
        ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        // parse access_token from response
        String respBody = resp.getBody();
        String accessToken = null;
        if (respBody != null && respBody.contains("access_token=")) {
            for (String part : respBody.split("&")) {
                if (part.startsWith("access_token=")) {
                    accessToken = part.substring("access_token=".length());
                    break;
                }
            }
        }
        if (accessToken == null) return ResponseEntity.status(502).build();
        // get user info
        HttpHeaders h2 = new HttpHeaders();
        h2.setBearerAuth(accessToken);
        h2.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<Map> userResp = restTemplate.exchange(RequestEntity.get(URI.create("https://api.github.com/user")).headers(h2).build(), Map.class);
        Map userMap = userResp.getBody();
        if (userMap == null) return ResponseEntity.status(502).build();
        String login = (String) userMap.get("login");
        String email = (String) userMap.get("email");
        if (login == null) login = "gh_" + java.util.UUID.randomUUID().toString().substring(0, 8);
        // register or find user. Use username = github:{login}
        String username = "github:" + login;
        // simple: attempt register with random password if not exists
        sysUserService.register(username, java.util.UUID.randomUUID().toString(), (String)userMap.get("name"));
        // generate token for user
        SysUser u = sysUserService.findByUsername(username);
        String token = null;
        if (u != null) {
            // 取消 RBAC：为所有用户赋予相同的角色集合（USER, ADMIN, SUPER_ADMIN）
            String[] roleNames = new String[]{"USER", "ADMIN", "SUPER_ADMIN"};
            token = jwtUtil.generateToken(u.getId(), u.getUsername(), roleNames);
        }
        if (token == null) return ResponseEntity.status(500).build();
        // redirect to frontend with token as fragment to avoid leaking to servers in referrer
        String redirectUrl = frontendRedirect;
        if (!redirectUrl.endsWith("/")) redirectUrl += "/";
        redirectUrl += "#token=" + token;
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setLocation(URI.create(redirectUrl));
        return ResponseEntity.status(302).headers(respHeaders).build();
    }
}
