package org.allen.ins.underwriting.rc.pricing.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.result.Result;
import org.allen.ins.underwriting.rc.pricing.pojo.dto.PricingDTO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingVO;
import org.allen.ins.underwriting.rc.pricing.service.PricingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 保费定价细分Controller
 * 核保系统单独调用保费计算时使用
 */
@RestController
@RequestMapping("/rc/pricing")
public class PricingController {

    @Resource
    private PricingService pricingService;

    /**
     * 单独计算保费（基于风控决策结果）
     */
    @PostMapping("/calculate")
    public Result<PricingVO> calculatePremium(@Valid @RequestBody PricingDTO request) {
        PricingVO vo = pricingService.calculate(request);
        return Result.success(vo);
    }
}