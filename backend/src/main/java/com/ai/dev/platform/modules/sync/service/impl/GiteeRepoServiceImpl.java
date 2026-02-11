package com.ai.dev.platform.modules.sync.service.impl;

import com.ai.dev.platform.modules.sync.entity.GiteeRepo;
import com.ai.dev.platform.modules.sync.mapper.GiteeRepoMapper;
import com.ai.dev.platform.modules.sync.service.GiteeRepoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GiteeRepoServiceImpl extends ServiceImpl<GiteeRepoMapper, GiteeRepo> implements GiteeRepoService {

    @Override
    public void saveOrUpdateRepo(GiteeRepo repo) {
        LambdaQueryWrapper<GiteeRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GiteeRepo::getRepoId, repo.getRepoId());
        
        GiteeRepo existingRepo = this.getOne(wrapper);
        if (existingRepo != null) {
            repo.setId(existingRepo.getId());
            this.updateById(repo);
        } else {
            this.save(repo);
        }
    }
}
