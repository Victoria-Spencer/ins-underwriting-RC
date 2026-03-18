package org.allen.ins.underwriting.rc.factor.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.result.Result;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 风险因子计算细分Controller
 * 运营/测试侧单独计算风险因子时调用
 */
@RestController
@RequestMapping("/rc/factor")
public class RiskFactorController {

    @Resource
    private RiskFactorService riskFactorService;

    /**
     * 单独计算风险因子（不触发完整风控流程）
     */
    @PostMapping("/calculate")
    public Result<RiskFactorVO> calculateRiskFactor(@Valid @RequestBody RiskFactorRequestDTO request) {
        RiskFactorVO vo = riskFactorService.calculate(request);
        return Result.success(vo);
    }
}