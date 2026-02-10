package com.ai.dev.platform.modules.sys.service.impl;

import com.ai.dev.platform.modules.sys.entity.SysUser;
import com.ai.dev.platform.modules.sys.mapper.SysUserMapper;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import com.ai.dev.platform.util.JwtUtil;
import com.ai.dev.platform.modules.sys.mapper.PasswordResetMapper;
import com.ai.dev.platform.modules.sys.entity.PasswordReset;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetMapper passwordResetMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, PasswordResetMapper passwordResetMapper) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.passwordResetMapper = passwordResetMapper;
    }

    @Override
    public String login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        // 取消RBAC权限校验 - 给所有用户赋予所有角色
        String[] allRoles = {"USER", "ADMIN", "SUPER_ADMIN"};
        // generate token with user id, username and all roles
        return jwtUtil.generateToken(user.getId(), user.getUsername(), allRoles);
    }

    @Override
    public boolean register(String username, String password, String nickname) {
        // check exists
        SysUser exists = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (exists != null) return false;
        SysUser u = new SysUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setNickname(nickname != null ? nickname : "");
        sysUserMapper.insert(u);
        return true;
    }

    @Override
    public String forgotPassword(String username) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) return null;
        String token = java.util.UUID.randomUUID().toString();
        java.time.LocalDateTime expire = java.time.LocalDateTime.now().plusHours(1);
        PasswordReset pr = new PasswordReset();
        pr.setUserId(user.getId());
        pr.setToken(token);
        pr.setExpireTime(expire);
        pr.setUsed(false);
        passwordResetMapper.insert(pr);
        // log reset link (for now)
        String link = String.format("/sys/user/reset?token=%s", token);
        System.out.println("Password reset link: " + link);
        return token;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        PasswordReset pr = passwordResetMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PasswordReset>().eq("token", token));
        if (pr == null) return false;
        if (pr.getUsed() != null && pr.getUsed()) return false;
        if (pr.getExpireTime() == null || pr.getExpireTime().isBefore(java.time.LocalDateTime.now())) return false;
        SysUser user = sysUserMapper.selectById(pr.getUserId());
        if (user == null) return false;
        user.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(user);
        pr.setUsed(true);
        passwordResetMapper.updateById(pr);
        return true;
    }

    @Override
    public SysUser findByUsername(String username) {
        return sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
    }

}
