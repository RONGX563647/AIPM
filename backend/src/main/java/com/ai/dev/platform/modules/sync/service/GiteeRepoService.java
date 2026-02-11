package com.ai.dev.platform.modules.sync.service;

import com.ai.dev.platform.modules.sync.entity.GiteeRepo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface GiteeRepoService extends IService<GiteeRepo> {
    void saveOrUpdateRepo(GiteeRepo repo);
}
