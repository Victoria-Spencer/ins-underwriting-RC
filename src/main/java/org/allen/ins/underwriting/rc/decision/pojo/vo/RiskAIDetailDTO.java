package org.allen.ins.underwriting.rc.decision.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RiskAIDetailDTO {
    /**
     * 本次复查使用的 AI 模型版本
     */
    private String aiModelVersion;

    /**
     * AI复查时间
     */
    private LocalDateTime reviewTime;
}
