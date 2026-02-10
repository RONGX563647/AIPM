package com.ai.dev.platform.modules.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_test_report")
@Schema(description = "测试报告实体")
public class TestReport {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "报告唯一标识")
    private Long id;

    @TableField("api_id")
    @Schema(description = "关联接口ID")
    private Long apiId;

    @TableField("case_id")
    @Schema(description = "关联用例ID")
    private Long caseId;

    @Schema(description = "实际结果（JSON格式）")
    private String actualResult;

    @Schema(description = "测试状态")
    private String status;

    @TableField("test_time")
    @Schema(description = "测试时间")
    private LocalDateTime testTime;

    @Schema(description = "失败备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
