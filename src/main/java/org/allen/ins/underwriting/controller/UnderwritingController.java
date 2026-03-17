package org.allen.ins.underwriting.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.result.Result;
import org.allen.ins.underwriting.pojo.dto.UnderwritingRequestDTO;
import org.allen.ins.underwriting.pojo.vo.UnderwritingResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 承保风控核心聚合Controller
 * 对外暴露核心接口，前端一站式调用完成风控+定价+承保决策
 */
@RestController
@RequestMapping("/underwriting")
public class UnderwritingController {

    @Resource
    private UnderwritingService underwritingService;

    /**
     * 核心聚合接口：一站式完成风控因子计算+逆选择防控+风控决策+保费定价+承保结果
     * 前端参保时主要调用此接口
     */
    @PostMapping("/risk/calculate")
    public Result<UnderwritingResponseVO> calculateUnderwritingRisk(@Valid @RequestBody UnderwritingRequestDTO request) {
            UnderwritingResponseVO response = underwritingService.calculateUnderwritingRisk(request);
            return Result.success(response);
    }
}