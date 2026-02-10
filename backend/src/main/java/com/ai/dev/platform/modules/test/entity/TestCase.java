package com.ai.dev.platform.modules.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("api_test_case")
@Schema(description = "测试用例实体")
public class TestCase {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用例唯一标识")
    private Long id;

    @TableField("api_id")
    @Schema(description = "关联接口ID")
    private Long apiId;

    @TableField("case_name")
    @Schema(description = "用例名称")
    private String caseName;

    @Schema(description = "测试参数（JSON格式）")
    private String params;

    @Schema(description = "预期结果（JSON格式）")
    private String expectedResult;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

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

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
