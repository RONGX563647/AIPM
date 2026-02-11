package com.ai.dev.platform.modules.sync.service;

import com.ai.dev.platform.modules.sync.entity.GitHubRepo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GitHubRepoService extends IService<GitHubRepo> {
    void saveOrUpdateRepo(GitHubRepo repo);
}
