package com.ai.dev.platform.modules.sys.service;

public interface SysUserService {
    /**
     * Attempt login; returns JWT token when success, otherwise null
     */
    String login(String username, String password);
}
