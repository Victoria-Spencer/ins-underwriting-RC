package org.allen.ins.underwriting.rc.decision.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.result.Result;
import org.allen.ins.underwriting.rc.decision.service.RiskDecisionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风控决策细分Controller
 * 测试/运营侧单独获取风控决策时调用
 */
@RestController
@RequestMapping("/rc/decision")
public class RiskDecisionController {

    @Resource
    private RiskDecisionService riskDecisionService;

    /**
     * 单独计算风控决策（基于风险因子+逆选择结果）
     */
    @PostMapping("/calculate")
    public Result<RiskDecisionVO> calculateDecision(@Valid @RequestBody RiskDecisionRequestDTO request) {
        RiskDecisionVO vo = riskDecisionService.calculate(request);
        return Result.success(vo);
    }
}