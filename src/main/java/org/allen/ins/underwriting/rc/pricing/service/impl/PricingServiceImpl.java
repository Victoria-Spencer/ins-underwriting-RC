package org.allen.ins.underwriting.rc.pricing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.allen.ins.underwriting.rc.decision.dao.RiskDecisionMapper;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.pricing.dao.PricingMapper;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.allen.ins.underwriting.rc.pricing.pojo.dto.PricingCoreDTO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingCoreVO;
import org.allen.ins.underwriting.rc.pricing.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class PricingServiceImpl  extends ServiceImpl<PricingMapper, PricingRecord>
        implements PricingService {

    // 金融行业标准：保费保留2位小数，四舍五入
    private static final int PRECISION_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Resource
    private RiskDecisionMapper riskDecisionMapper;

    /**
     * 执行核心保费定价计算
     */
    @Override
    public PricingCoreVO calculatePremium(@Valid PricingCoreDTO request) {
        // 1. 初始化VO，回显输入参数
        PricingCoreVO response = new PricingCoreVO();
        response.setInsureAmount(request.getInsureAmount());
        response.setRiskProbability(request.getRiskProbability());
        response.setCostCoefficient(request.getCostCoefficient());
        response.setProfitCoefficient(request.getProfitCoefficient());

        // 2. 步骤1：计算预期赔付金额 = 保额 × 风险概率
        BigDecimal expectedPayoutAmount = request.getInsureAmount()
                .multiply(request.getRiskProbability())
                .setScale(PRECISION_SCALE, ROUNDING_MODE);
        response.setExpectedPayoutAmount(expectedPayoutAmount);

        // 3. 步骤2：计算基础保费 = 预期赔付金额 × (1 + 成本系数)
        BigDecimal basePremium = expectedPayoutAmount
                .multiply(BigDecimal.ONE.add(request.getCostCoefficient()))
                .setScale(PRECISION_SCALE, ROUNDING_MODE);
        response.setBasePremium(basePremium);

        // 4. 步骤3：计算最终保费 = 基础保费 × (1 + 利润系数)
        BigDecimal finalPremium = basePremium
                .multiply(BigDecimal.ONE.add(request.getProfitCoefficient()))
                .setScale(PRECISION_SCALE, ROUNDING_MODE);
        response.setFinalPremium(finalPremium);

        // 5. 生成公式明细（便于追溯）
        response.setPricingFormulaDesc(buildFormulaDesc(request, response));

        // 6. 保存定价记录到数据库
        savePricingRecord(request, response);

        return response;
    }

    private void savePricingRecord(@Valid PricingCoreDTO request, PricingCoreVO response) {
        RiskDecisionRecord riskDecisionRecord = riskDecisionMapper.selectOne(
                new QueryWrapper<RiskDecisionRecord>().eq("trace_id", TraceIdContext.getTraceId())
        );

        PricingRecord record = new PricingRecord()
                .setPolicyHolderId(request.getPolicyHolderId())
                .setTraceId(TraceIdContext.getTraceId())
                .setRiskDecisionId(riskDecisionRecord.getId())
                .setBasePremium(response.getBasePremium())
                .setFinalPremium(response.getFinalPremium())
                .setPremium_start_time(LocalDateTime.now().plusDays(1))
                .setPremium_end_time(LocalDateTime.now().plusDays(365));

        this.save(record);
    }

    /**
     * 构建定价公式明细（文字描述，便于理解）
     */
    private String buildFormulaDesc(PricingCoreDTO request, PricingCoreVO response) {
        return String.format(
                "1. 预期赔付金额 = 投保保额(%s元) × 风险概率(%s) = %s元；" +
                        "2. 基础保费 = 预期赔付金额(%s元) × (1 + 成本系数(%s)) = %s元；" +
                        "3. 最终保费 = 基础保费(%s元) × (1 + 利润系数(%s)) = %s元；" +
                        "合并公式：最终保费 = %s × %s × (1+%s) × (1+%s) = %s元",
                request.getInsureAmount(),
                request.getRiskProbability(),
                response.getExpectedPayoutAmount(),
                response.getExpectedPayoutAmount(),
                request.getCostCoefficient(),
                response.getBasePremium(),
                response.getBasePremium(),
                request.getProfitCoefficient(),
                response.getFinalPremium(),
                request.getInsureAmount(),
                request.getRiskProbability(),
                request.getCostCoefficient(),
                request.getProfitCoefficient(),
                response.getFinalPremium()
        );
    }
}
