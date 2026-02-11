package com.ai.dev.platform.modules.sync.util;

import com.ai.dev.platform.modules.sync.entity.GiteeRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gitee API客户端工具类
 * 
 * <p>负责与Gitee平台API进行交互，提供：
 * <ul>
 *   <li>用户仓库信息获取</li>
 *   <li>仓库详情数据解析</li>
 *   <li>API认证和授权处理</li>
 *   <li>错误处理和日志记录</li>
 * </ul>
 * 
 * <p>该组件通过@Component注解标记为Spring组件，
 * 使用RestTemplate进行HTTP请求处理。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class GiteeApiClient {

    /**
     * 日志记录器实例
     * 
     * <p>用于记录API调用过程中的关键信息和错误。
     * 
     * @since 1.0.0
     */
    private static final Logger logger = LoggerFactory.getLogger(GiteeApiClient.class);
    
    /**
     * Gitee API基础URL
     * 
     * <p>Gitee开放平台API的基准地址，所有API调用都基于此URL。
     * 
     * @since 1.0.0
     */
    private static final String GITEE_API_BASE_URL = "https://gitee.com/api/v5";
    
    /**
     * JSON对象映射器
     * 
     * <p>用于解析API响应的JSON数据。
     * 
     * @since 1.0.0
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * REST客户端实例
     * 
     * <p>用于发起HTTP请求到Gitee API。
     * 
     * @since 1.0.0
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取用户仓库列表
     * 
     * <p>通过Gitee API获取指定用户的仓库信息列表：
     * <ul>
     *   <li>构建API请求URL和请求头</li>
     *   <li>处理认证令牌(可选)</li>
     *   <li>发送GET请求获取仓库数据</li>
     *   <li>解析JSON响应为仓库对象列表</li>
     *   <li>记录操作日志和错误信息</li>
     * </ul>
     * 
     * @param username Gitee用户名
     * @param accessToken 访问令牌，用于认证(可选)
     * @return List<GiteeRepo> 仓库列表，获取失败时返回空列表
     * @since 1.0.0
     */
    public List<GiteeRepo> getUserRepos(String username, String accessToken) {
        List<GiteeRepo> repos = new ArrayList<>();
        try {
            String url = GITEE_API_BASE_URL + "/user/repos";
            
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("Authorization", "token " + accessToken);
            }
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                for (JsonNode node : root) {
                    GiteeRepo repo = parseRepoNode(node);
                    if (repo != null) {
                        repos.add(repo);
                    }
                }
                logger.info("成功从Gitee获取{}个仓库", repos.size());
            } else {
                logger.error("Gitee API请求失败，状态码：{}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("获取Gitee仓库数据失败", e);
        }
        return repos;
    }

    public GiteeRepo getRepo(String owner, String repoName, String accessToken) {
        try {
            String url = GITEE_API_BASE_URL + "/repos/" + owner + "/" + repoName;
            
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("Authorization", "token " + accessToken);
            }
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode node = objectMapper.readTree(response.getBody());
                GiteeRepo repo = parseRepoNode(node);
                logger.info("成功获取Gitee仓库：{}/{}", owner, repoName);
                return repo;
            }
        } catch (Exception e) {
            logger.error("获取Gitee仓库失败：{}/{}", owner, repoName, e);
        }
        return null;
    }

    private GiteeRepo parseRepoNode(JsonNode node) {
        try {
            GiteeRepo repo = new GiteeRepo();
            repo.setRepoId(node.get("id").asLong());
            repo.setRepoName(node.get("full_name").asText());
            repo.setRepoUrl(node.get("html_url").asText());
            
            if (node.has("description") && !node.get("description").isNull()) {
                repo.setDescription(node.get("description").asText());
            }
            
            if (node.has("language") && !node.get("language").isNull()) {
                repo.setLanguage(node.get("language").asText());
            }
            
            if (node.has("stargazers_count")) {
                repo.setStarsCount(node.get("stargazers_count").asInt());
            }
            
            if (node.has("forks_count")) {
                repo.setForksCount(node.get("forks_count").asInt());
            }
            
            if (node.has("open_issues_count")) {
                repo.setOpenIssuesCount(node.get("open_issues_count").asInt());
            }
            
            if (node.has("updated_at") && !node.get("updated_at").isNull()) {
                String updatedAtStr = node.get("updated_at").asText();
                repo.setUpdatedAt(LocalDateTime.parse(updatedAtStr, DateTimeFormatter.ISO_DATE_TIME));
            }
            
            return repo;
        } catch (Exception e) {
            logger.error("解析Gitee仓库数据失败", e);
            return null;
        }
    }
}
