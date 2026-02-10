package com.ai.dev.platform.security;

import com.ai.dev.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            Jws<io.jsonwebtoken.Claims> parsed = jwtUtil.parseToken(token);
            if (parsed != null) {
                Claims claims = parsed.getBody();
                Object uid = claims.get("uid");
                Object uname = claims.get("uname");
                // 取消RBAC权限校验 - 忽略角色权限检查
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("USER"),
                    new SimpleGrantedAuthority("ADMIN"), 
                    new SimpleGrantedAuthority("SUPER_ADMIN")
                );
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(uname, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute("currentUserId", uid);
                request.setAttribute("currentUsername", uname);
            }
        }
        filterChain.doFilter(request, response);
    }
}
