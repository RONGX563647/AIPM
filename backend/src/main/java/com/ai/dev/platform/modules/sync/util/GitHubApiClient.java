package com.ai.dev.platform.modules.sync.util;

import com.ai.dev.platform.modules.sync.entity.GitHubRepo;
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

@Component
public class GitHubApiClient {

    private static final Logger logger = LoggerFactory.getLogger(GitHubApiClient.class);
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    public List<GitHubRepo> getUserRepos(String username, String accessToken) {
        List<GitHubRepo> repos = new ArrayList<>();
        try {
            String url = GITHUB_API_BASE_URL + "/users/" + username + "/repos";
            
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("Authorization", "token " + accessToken);
            }
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                for (JsonNode node : root) {
                    GitHubRepo repo = parseRepoNode(node);
                    if (repo != null) {
                        repos.add(repo);
                    }
                }
                logger.info("成功从GitHub获取{}个仓库", repos.size());
            } else {
                logger.error("GitHub API请求失败，状态码：{}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("获取GitHub仓库数据失败", e);
        }
        return repos;
    }

    public GitHubRepo getRepo(String owner, String repoName, String accessToken) {
        try {
            String url = GITHUB_API_BASE_URL + "/repos/" + owner + "/" + repoName;
            
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) {
                headers.set("Authorization", "token " + accessToken);
            }
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode node = objectMapper.readTree(response.getBody());
                GitHubRepo repo = parseRepoNode(node);
                logger.info("成功获取GitHub仓库：{}/{}", owner, repoName);
                return repo;
            }
        } catch (Exception e) {
            logger.error("获取GitHub仓库失败：{}/{}", owner, repoName, e);
        }
        return null;
    }

    private GitHubRepo parseRepoNode(JsonNode node) {
        try {
            GitHubRepo repo = new GitHubRepo();
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
            logger.error("解析GitHub仓库数据失败", e);
            return null;
        }
    }
}
