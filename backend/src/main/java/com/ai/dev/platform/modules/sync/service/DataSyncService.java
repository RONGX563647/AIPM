package com.ai.dev.platform.modules.sync.service;

import java.util.List;

public interface DataSyncService {
    void syncGitHubRepos(String username, String accessToken);
    void syncGiteeRepos(String username, String accessToken);
    void syncAll();
}
