package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import com.ai.dev.platform.modules.sys.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户控制器
 * 
 * <p>负责处理用户认证相关的HTTP请求，包括：
 * <ul>
 *   <li>用户登录认证</li>
 *   <li>用户注册</li>
 *   <li>密码重置</li>
 *   <li>用户信息查询</li>
 * </ul>
 * 
 * <p>该控制器使用JWT进行无状态认证，所有接口都遵循RESTful设计原则
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sys/user")
@Tag(name = "sys-user", description = "用户认证管理控制器")
public class SysUserController {
    
    private final SysUserService sysUserService;

    /**
     * 构造函数注入用户服务
     * 
     * @param sysUserService 系统用户服务实例
     * @since 1.0.0
     */
    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 用户登录接口
     * 
     * <p>验证用户凭据并返回JWT访问令牌
     * 
     * <p>业务流程：
     * <ol>
     *   <li>验证用户名和密码</li>
     *   <li>生成JWT访问令牌</li>
     *   <li>返回令牌给客户端</li>
     * </ol>
     * 
     * @param request 登录请求对象，包含用户名和密码
     * @return Result<String> 登录结果，成功时返回JWT令牌，失败时返回错误信息
     * @since 1.0.0
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录认证", description = "验证用户凭据并返回JWT访问令牌")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = sysUserService.login(request.getUsername(), request.getPassword());
        if (token == null) return Result.error("用户名或密码错误");
        return Result.ok(token);
    }
}
