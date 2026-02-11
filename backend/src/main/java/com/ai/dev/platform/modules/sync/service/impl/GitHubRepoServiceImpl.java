package com.ai.dev.platform.modules.sync.service.impl;

import com.ai.dev.platform.modules.sync.entity.GitHubRepo;
import com.ai.dev.platform.modules.sync.mapper.GitHubRepoMapper;
import com.ai.dev.platform.modules.sync.service.GitHubRepoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GitHubRepoServiceImpl extends ServiceImpl<GitHubRepoMapper, GitHubRepo> implements GitHubRepoService {

    @Override
    public void saveOrUpdateRepo(GitHubRepo repo) {
        LambdaQueryWrapper<GitHubRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GitHubRepo::getRepoId, repo.getRepoId());
        
        GitHubRepo existingRepo = this.getOne(wrapper);
        if (existingRepo != null) {
            repo.setId(existingRepo.getId());
            this.updateById(repo);
        } else {
            this.save(repo);
        }
    }
}
