package com.ai.dev.platform.modules.sys.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import com.ai.dev.platform.modules.sys.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user")
@Tag(name = "sys-user", description = "用户登录控制器")
public class SysUserController {
    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = sysUserService.login(request.getUsername(), request.getPassword());
        if (token == null) return Result.error("用户名或密码错误");
        return Result.ok(token);
    }
}
