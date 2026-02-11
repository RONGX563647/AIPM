package com.ai.dev.platform.modules.sync.service.impl;

import com.ai.dev.platform.modules.sync.entity.GitHubRepo;
import com.ai.dev.platform.modules.sync.entity.GiteeRepo;
import com.ai.dev.platform.modules.sync.service.*;
import com.ai.dev.platform.modules.sync.util.GitHubApiClient;
import com.ai.dev.platform.modules.sync.util.GiteeApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSyncServiceImpl implements DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncServiceImpl.class);

    @Autowired
    private GitHubApiClient gitHubApiClient;

    @Autowired
    private GiteeApiClient giteeApiClient;

    @Autowired
    private GitHubRepoService gitHubRepoService;

    @Autowired
    private GiteeRepoService giteeRepoService;

    @Autowired
    private SyncRecordService syncRecordService;

    @Override
    public void syncGitHubRepos(String username, String accessToken) {
        logger.info("开始同步GitHub仓库数据，用户：{}", username);
        syncRecordService.recordSync("github", username, "pending", null, 0);
        
        try {
            List<GitHubRepo> repos = gitHubApiClient.getUserRepos(username, accessToken);
            
            if (repos != null && !repos.isEmpty()) {
                int successCount = 0;
                for (GitHubRepo repo : repos) {
                    try {
                        gitHubRepoService.saveOrUpdateRepo(repo);
                        successCount++;
                    } catch (Exception e) {
                        logger.error("保存GitHub仓库失败：{}", repo.getRepoName(), e);
                    }
                }
                
                syncRecordService.recordSync("github", username, "success", null, successCount);
                logger.info("GitHub仓库同步完成，成功{}个，总数{}", successCount, repos.size());
            } else {
                syncRecordService.recordSync("github", username, "failed", "未获取到仓库数据", 0);
                logger.warn("GitHub仓库同步完成，未获取到数据");
            }
        } catch (Exception e) {
            syncRecordService.recordSync("github", username, "failed", e.getMessage(), 0);
            logger.error("GitHub仓库同步失败", e);
        }
    }

    @Override
    public void syncGiteeRepos(String username, String accessToken) {
        logger.info("开始同步Gitee仓库数据，用户：{}", username);
        syncRecordService.recordSync("gitee", username, "pending", null, 0);
        
        try {
            List<GiteeRepo> repos = giteeApiClient.getUserRepos(username, accessToken);
            
            if (repos != null && !repos.isEmpty()) {
                int successCount = 0;
                for (GiteeRepo repo : repos) {
                    try {
                        giteeRepoService.saveOrUpdateRepo(repo);
                        successCount++;
                    } catch (Exception e) {
                        logger.error("保存Gitee仓库失败：{}", repo.getRepoName(), e);
                    }
                }
                
                syncRecordService.recordSync("gitee", username, "success", null, successCount);
                logger.info("Gitee仓库同步完成，成功{}个，总数{}", successCount, repos.size());
            } else {
                syncRecordService.recordSync("gitee", username, "failed", "未获取到仓库数据", 0);
                logger.warn("Gitee仓库同步完成，未获取到数据");
            }
        } catch (Exception e) {
            syncRecordService.recordSync("gitee", username, "failed", e.getMessage(), 0);
            logger.error("Gitee仓库同步失败", e);
        }
    }

    @Override
    public void syncAll() {
        logger.info("开始执行全量数据同步");
        
        String gitHubUsername = "your-github-username";
        String gitHubToken = "your-github-token";
        String giteeUsername = "your-gitee-username";
        String giteeToken = "your-gitee-token";
        
        syncGitHubRepos(gitHubUsername, gitHubToken);
        syncGiteeRepos(giteeUsername, giteeToken);
        
        logger.info("全量数据同步完成");
    }
}
