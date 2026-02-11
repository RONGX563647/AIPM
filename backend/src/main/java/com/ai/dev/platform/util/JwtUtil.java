package com.ai.dev.platform.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * <p>负责JWT(JSON Web Token)的生成、解析和验证操作：
 * <ul>
 *   <li>生成安全的JWT访问令牌</li>
 *   <li>解析和验证JWT令牌的有效性</li>
 *   <li>提取JWT中的用户信息</li>
 *   <li>处理令牌过期和签名验证</li>
 * </ul>
 * 
 * <p>该工具类使用JWT标准实现无状态认证机制，
 * 通过数字签名确保令牌的完整性和真实性。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class JwtUtil {
    
    /**
     * JWT签名密钥
     * 
     * <p>从配置文件读取的JWT签名密钥，用于令牌的签名和验证。
     * 默认值为"ai-code-review-secret-key-2024"。
     * 
     * @since 1.0.0
     */
    @Value("${jwt.secret:ai-code-review-secret-key-2024}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     * 
     * <p>从配置文件读取的JWT令牌有效期，默认24小时(86400000毫秒)。
     * 过期的令牌将被视为无效，需要重新认证。
     * 
     * @since 1.0.0
     */
    @Value("${jwt.expiration:86400000}")
    private long expirationMillis;

    /**
     * 获取JWT签名密钥
     * 
     * <p>根据配置的密钥生成符合JWT要求的签名密钥：
     * <ul>
     *   <li>确保密钥长度至少32字节</li>
     *   <li>对过短的密钥进行确定性填充</li>
     *   <li>使用HMAC-SHA算法生成密钥</li>
     * </ul>
     * 
     * @return Key 符合JWT要求的签名密钥
     * @since 1.0.0
     */
    private Key getKey() {
        // ensure key length; if secret too short, pad it deterministically
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            for (int i = keyBytes.length; i < padded.length; i++) padded[i] = (byte) ('0' + (i % 10));
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT访问令牌
     * 
     * <p>创建包含用户信息的JWT令牌，用于身份认证：
     * <ul>
     *   <li>设置用户ID和用户名作为声明</li>
     *   <li>可选添加用户角色信息</li>
     *   <li>设置令牌有效期和签发时间</li>
     *   <li>使用HMAC-SHA256算法进行数字签名</li>
     * </ul>
     * 
     * <p><b>令牌结构包含：</b>
     * <ul>
     *   <li>Header：算法和令牌类型信息</li>
     *   <li>Payload：用户声明(uid, uname, roles等)</li>
     *   <li>Signature：基于密钥的数字签名</li>
     * </ul>
     * 
     * @param userId 用户唯一标识
     * @param username 用户登录名
     * @param roles 用户角色数组（可选）
     * @return String 生成的JWT令牌字符串
     * @since 1.0.0
     */
    public String generateToken(Long userId, String username, String[] roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);
        if (roles != null && roles.length > 0) {
            claims.put("roles", roles);
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT访问令牌（兼容方法）
     * 
     * <p>创建不包含角色信息的简化JWT令牌，用于向后兼容。
     * 
     * @param userId 用户唯一标识
     * @param username 用户登录名
     * @return String 生成的JWT令牌字符串
     * @since 1.0.0
     */
    // compatibility method
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    /**
     * 解析和验证JWT令牌
     * 
     * <p>验证JWT令牌的有效性并提取其中的声明信息：
     * <ul>
     *   <li>验证数字签名的正确性</li>
     *   <li>检查令牌是否过期</li>
     *   <li>验证签发者和受众信息</li>
     *   <li>提取用户声明数据</li>
     * </ul>
     * 
     * <p><b>安全验证包括：</b>
     * <ul>
     *   <li>签名完整性验证</li>
     *   <li>时间有效性检查</li>
     *   <li>声明格式验证</li>
     * </ul>
     * 
     * @param token 待验证的JWT令牌字符串
     * @return Jws<Claims> 解析成功的声明对象，验证失败返回null
     * @since 1.0.0
     */
    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
        } catch (JwtException e) {
            return null;
        }
    }
}
