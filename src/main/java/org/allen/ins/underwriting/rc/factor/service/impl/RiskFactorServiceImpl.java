package org.allen.ins.underwriting.rc.factor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.allen.ins.underwriting.common.constant.SystemConstant;
import org.allen.ins.underwriting.common.exception.BusinessException;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.dao.dict.AgeRiskDictMapper;
import org.allen.ins.underwriting.dao.dict.OccupationRiskDictMapper;
import org.allen.ins.underwriting.dao.dict.SumInsuredRiskDictMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.rc.factor.constant.FactorWeightConst;
import org.allen.ins.underwriting.rc.factor.dao.RiskFactorMapper;
import org.allen.ins.underwriting.rc.factor.enums.RiskLevelEnum;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.allen.ins.underwriting.rc.factor.pojo.dto.RiskFactorCalculateDTO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class RiskFactorServiceImpl extends ServiceImpl<RiskFactorMapper, RiskFactorRecord>
        implements RiskFactorService {

    @Resource
    private PolicyHolderMapper policyHolderMapper;
    @Resource
    private AgeRiskDictMapper ageRiskDictMapper;
    @Resource
    private OccupationRiskDictMapper occupationRiskDictMapper;
    @Resource
    private SumInsuredRiskDictMapper sumInsuredRiskDictMapper;

    /**
     * 查询投保人信息
     * 计算基础因子（年龄/职业/保额风险因子）
     * 调用第三方接口核验住院记录
     * 计算健康风险因子
     * 加权计算总风险因子
     * 填充计算时间/计算人
     * 将RiskFactorRecord对象插入数据库
     * 封装返回
     */
    @Override
    public RiskFactorVO calculate(RiskFactorCalculateDTO request) {
        Long policyHolderId = request.getPolicyHolderId();
        PolicyHolder policyHolder = policyHolderMapper.selectById(policyHolderId);
        String traceId = TraceIdContext.getTraceId();

        BigDecimal ageRiskValue = ageRiskDictMapper.getRiskValueByAge(policyHolder.getAge());
        BigDecimal occupationRiskValue = occupationRiskDictMapper.getRiskValueByOccupationName(policyHolder.getOccupation());
        BigDecimal amountRiskValue = sumInsuredRiskDictMapper.getRiskValueByAmount(request.getInsureAmount());
        if (null == ageRiskValue ||  null == occupationRiskValue || null == amountRiskValue) {
            throw new BusinessException("年龄/职业/保额风险因子不能为空");
        }

        // TODO 调用第三方接口核验住院记录

        // TODO 调用第三方接口获取健康因子的核心数据，然后计算健康风险因子
        BigDecimal healthRiskValue  = BigDecimal.valueOf(0.10);

        // 加权计算总风险因子
        BigDecimal totalRiskValue = calculateTotalRiskValue(
                ageRiskValue,
                occupationRiskValue,
                amountRiskValue,
                healthRiskValue
        );

        RiskFactorRecord riskFactorRecord = new RiskFactorRecord()
                .setAgeRiskValue(ageRiskValue)
                .setOccupationRiskValue(occupationRiskValue)
                .setAmountRiskValue(amountRiskValue)
                .setHealthRiskValue(healthRiskValue)
                .setTotalRiskValue(totalRiskValue)
                .setPolicyHolderId(policyHolderId)
                .setTraceId(traceId)
                .setCalculatorUser(SystemConstant.DEFAULT_CALCULATOR_USER)
                .setCalculateTime(LocalDateTime.now());

        boolean isSuccess = this.save(riskFactorRecord);
        if (!isSuccess) {
            throw new BusinessException("风险因子记录异常!");
        }

        RiskFactorVO riskFactorVO = BeanUtil.copyProperties(riskFactorRecord, RiskFactorVO.class);
        // 总风险值 映射为 等级
        String levelName = RiskLevelEnum.getByTotalValue(totalRiskValue).getLevelName();
        riskFactorVO.setTotalRiskLevel(levelName);
        return riskFactorVO;
    }

    /**
     * 从常量类获取各维度权重
     * 加权求和（各因子 × 对应权重）
     * 归一化处理（确保总风险值≤1，保险风控核心规则）
     * 保留6位小数（避免精度冗余）
     */
    private BigDecimal calculateTotalRiskValue(
            BigDecimal ageRiskValue,
            BigDecimal occupationRiskValue,
            BigDecimal amountRiskValue,
            BigDecimal healthRiskValue) {

        BigDecimal ageWeight = FactorWeightConst.getWeight("RISK_FACTOR_AGE");
        BigDecimal occupationWeight = FactorWeightConst.getWeight("RISK_FACTOR_OCCUPATION");
        BigDecimal amountWeight = FactorWeightConst.getWeight("RISK_FACTOR_AMOUNT");
        BigDecimal healthWeight = FactorWeightConst.getWeight("RISK_FACTOR_HEALTH");

        BigDecimal weightedSum = ageRiskValue.multiply(ageWeight)
                .add(occupationRiskValue.multiply(occupationWeight))
                .add(amountRiskValue.multiply(amountWeight))
                .add(healthRiskValue.multiply(healthWeight));

        BigDecimal totalRiskValue = weightedSum.compareTo(BigDecimal.ONE) > 0
                ? BigDecimal.ONE
                : weightedSum;

        return totalRiskValue.setScale(6, RoundingMode.HALF_UP);
    }
}
