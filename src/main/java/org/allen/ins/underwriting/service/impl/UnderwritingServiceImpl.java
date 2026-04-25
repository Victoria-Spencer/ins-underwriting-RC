package org.allen.ins.underwriting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.allen.ins.underwriting.common.constant.SystemConstant;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.dao.UnderwritingMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.pojo.domain.UnderwritingRecord;
import org.allen.ins.underwriting.pojo.dto.UnderwritingRequestDTO;
import org.allen.ins.underwriting.pojo.vo.PolicyHolderVO;
import org.allen.ins.underwriting.pojo.vo.UnderwritingResponseVO;
import org.allen.ins.underwriting.rc.antiselection.dao.AntiselectionMapper;
import org.allen.ins.underwriting.rc.antiselection.pojo.domain.AntiselectionRecord;
import org.allen.ins.underwriting.rc.antiselection.pojo.vo.AntiselectionVO;
import org.allen.ins.underwriting.rc.antiselection.service.AntiselectionService;
import org.allen.ins.underwriting.rc.decision.dao.RiskDecisionMapper;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionDTO;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;
import org.allen.ins.underwriting.rc.decision.service.RiskDecisionService;
import org.allen.ins.underwriting.rc.factor.pojo.dto.RiskFactorCalculateDTO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.allen.ins.underwriting.rc.pricing.dao.PricingMapper;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.allen.ins.underwriting.rc.pricing.pojo.dto.PricingCoreDTO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingCoreVO;
import org.allen.ins.underwriting.rc.pricing.service.PricingService;
import org.allen.ins.underwriting.service.UnderwritingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class UnderwritingServiceImpl extends ServiceImpl<UnderwritingMapper, UnderwritingRecord>
        implements UnderwritingService {

    @Resource
    private PolicyHolderMapper policyHolderMapper;
    @Resource
    private RiskFactorService riskFactorService;
    @Resource
    private AntiselectionMapper antiselectionMapper;
    @Resource
    private RiskDecisionService riskDecisionService;
    @Resource
    private PricingService pricingCoreService;
    @Resource
    private RiskDecisionMapper riskDecisionMapper;
    @Resource
    private PricingMapper pricingMapper;

    // TODO 新增成本和利润模块
    private final BigDecimal DEFAULT_COST_COEFFICIENT = new BigDecimal("0.123456");
    private final BigDecimal DEFAULT_PROFIT_COEFFICIENT = new BigDecimal("0.234567");

    // 承保
    public static final String UNDERWRITING_ACCEPT = "承保";
    // 拒保
    public static final String UNDERWRITING_DECLINE = "拒保";

    @Override
    @Transactional
    public UnderwritingResponseVO calculateUnderwritingRisk(UnderwritingRequestDTO request) {
//        System.out.println("test-qolo");
        log.debug("test-qolo");
        PolicyHolderVO policyHolderVO = getPolicyHolderVOById(request.getPolicyHolderId());

        RiskFactorVO riskFactorVO = calculateRiskFactor(request);

        AntiselectionVO antiselectionVO = getAntiselectionVO();

        RiskDecisionVO riskDecisionVO = calculateRiskDecision(request, riskFactorVO);

        PricingCoreVO pricingCoreVO = new PricingCoreVO();
        if (riskDecisionVO.getDecisionResult().equals(UNDERWRITING_ACCEPT) ) {
            pricingCoreVO = calculatePricing(request, riskDecisionVO);
        }

        saveUnderwritingRecord(request, riskDecisionVO);

        // 封装返回
        return new UnderwritingResponseVO()
                .setPolicyHolderVO(policyHolderVO)
                .setRiskFactorVO(riskFactorVO)
                .setAntiselectionVO(antiselectionVO)
                .setRiskDecisionVO(riskDecisionVO)
                .setPricingVO(pricingCoreVO)
                .setUnderwritingResult(riskDecisionVO.getDecisionResult());
    }

    private void saveUnderwritingRecord(UnderwritingRequestDTO request, RiskDecisionVO riskDecisionVO) {
        // 获取riskDecisionId
        String traceId = TraceIdContext.getTraceId();
        RiskDecisionRecord riskDecisionRecord = riskDecisionMapper.selectOne(
                new QueryWrapper<RiskDecisionRecord>().eq("trace_id", traceId)
        );
        Long riskDecisionId = null;
        if (riskDecisionRecord != null) {
            riskDecisionId = riskDecisionRecord.getId();
        }

        // 获取pricingId
        PricingRecord pricingRecord = pricingMapper.selectOne(
                new QueryWrapper<PricingRecord>().eq("trace_id", traceId)
        );
        Long pricingId = null;
        if (pricingRecord != null) {
            pricingId = pricingRecord.getId();
        }
        UnderwritingRecord underwritingRecord = new UnderwritingRecord()
                .setPolicyHolderId(request.getPolicyHolderId())
                .setTraceId(traceId)
                .setRiskDecisionId(riskDecisionId)
                .setPricingRecordId(pricingId)
                .setUnderwritingResult(riskDecisionVO.getDecisionResult())
                .setOperator(SystemConstant.DEFAULT_CALCULATOR_USER);

        this.save(underwritingRecord);
    }

    private PolicyHolderVO getPolicyHolderVOById(Long holderId) {
        PolicyHolder policyHolder = policyHolderMapper.selectById(holderId);
        return BeanUtil.copyProperties(policyHolder, PolicyHolderVO.class);
    }

    private PricingCoreVO calculatePricing(UnderwritingRequestDTO request, RiskDecisionVO riskDecisionVO) {
        PricingCoreDTO dto = new PricingCoreDTO()
                .setPolicyHolderId(request.getPolicyHolderId())
                .setInsureAmount(request.getInsureAmount())
                .setRiskProbability(riskDecisionVO.getRiskProb())
                .setCostCoefficient(DEFAULT_COST_COEFFICIENT)
                .setProfitCoefficient(DEFAULT_PROFIT_COEFFICIENT);

        return pricingCoreService.calculatePremium(dto);
    }

    private RiskDecisionVO calculateRiskDecision(UnderwritingRequestDTO request, RiskFactorVO riskFactorVO) {
        RiskDecisionDTO dto = new RiskDecisionDTO()
                .setPolicyHolderId(request.getPolicyHolderId())
                .setInsureAmount(request.getInsureAmount())
                .setTotalRiskValue(riskFactorVO.getTotalRiskValue());
        return riskDecisionService.calculate(dto);
    }

    private AntiselectionVO getAntiselectionVO() {
        String traceId = TraceIdContext.getTraceId();
        AntiselectionRecord record = antiselectionMapper.selectOne(
                new QueryWrapper<AntiselectionRecord>().eq("trace_id", traceId)
        );

        return BeanUtil.copyProperties(record, AntiselectionVO.class);
    }

    private RiskFactorVO calculateRiskFactor(UnderwritingRequestDTO request) {
        RiskFactorCalculateDTO dto = BeanUtil.copyProperties(request, RiskFactorCalculateDTO.class);
        return riskFactorService.calculate(dto);
    }
}
