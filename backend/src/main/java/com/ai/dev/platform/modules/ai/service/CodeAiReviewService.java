package com.ai.dev.platform.modules.ai.service;

import com.ai.dev.platform.modules.ai.entity.CodeAiReview;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CodeAiReviewService extends IService<CodeAiReview> {
    IPage<CodeAiReview> getReviewPage(Integer pageNum, Integer pageSize, Long projectId);

    CodeAiReview submitReview(Long projectId, String codeContent);
}
