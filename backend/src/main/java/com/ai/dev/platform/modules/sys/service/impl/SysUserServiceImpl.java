package com.ai.dev.platform.modules.sys.service.impl;

import com.ai.dev.platform.modules.sys.entity.SysUser;
import com.ai.dev.platform.modules.sys.mapper.SysUserMapper;
import com.ai.dev.platform.modules.sys.service.SysUserService;
import com.ai.dev.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(String username, String password) {
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        // generate token with user id and username
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
