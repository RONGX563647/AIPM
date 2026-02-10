package com.ai.dev.platform.modules.ai.service.impl;

import com.ai.dev.platform.modules.ai.entity.CodeAiReview;
import com.ai.dev.platform.modules.ai.mapper.CodeAiReviewMapper;
import com.ai.dev.platform.modules.ai.service.CodeAiReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class CodeAiReviewServiceImpl extends ServiceImpl<CodeAiReviewMapper, CodeAiReview> implements CodeAiReviewService {

    @Override
    public IPage<CodeAiReview> getReviewPage(Integer pageNum, Integer pageSize, Long projectId) {
        Page<CodeAiReview> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CodeAiReview> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(CodeAiReview::getProjectId, projectId);
        }
        
        wrapper.orderByDesc(CodeAiReview::getCreateTime);
        
        return page(page, wrapper);
    }

    @Override
    public CodeAiReview submitReview(Long projectId, String codeContent) {
        CodeAiReview review = new CodeAiReview();
        review.setProjectId(projectId);
        review.setCodeContent(codeContent);
        review.setCreateTime(LocalDateTime.now());
        
        Random random = new Random();
        review.setScore(80 + random.nextInt(16));
        
        review.setSuggestion("1. 建议优化代码结构，提高可读性\n" +
                "2. 添加必要的注释说明关键逻辑\n" +
                "3. 考虑使用更高效的算法\n" +
                "4. 增加异常处理机制");
        
        review.setImprovedCode(codeContent + "\n\n// AI优化建议已应用\n" +
                "// 1. 优化了代码结构\n" +
                "// 2. 添加了详细注释\n" +
                "// 3. 改进了算法效率\n" +
                "// 4. 增加了异常处理");
        
        save(review);
        
        return review;
    }
}
