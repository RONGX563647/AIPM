package com.ai.dev.platform.modules.sys.service;

public interface SysUserService {
    /**
     * Attempt login; returns JWT token when success, otherwise null
     */
    String login(String username, String password);

    /**
     * Register a new user; returns true on success
     */
    boolean register(String username, String password, String nickname);

    /**
     * Initiate password reset; returns the reset token
     */
    String forgotPassword(String username);

    /**
     * Reset password via token; returns true on success
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * Find a user by username
     */
    com.ai.dev.platform.modules.sys.entity.SysUser findByUsername(String username);

}
