package org.allen.ins.underwriting.rc.factor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.allen.ins.underwriting.common.constant.SystemConstant;
import org.allen.ins.underwriting.common.exception.BusinessException;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.dao.dict.AgeRiskDictMapper;
import org.allen.ins.underwriting.dao.dict.OccupationRiskDictMapper;
import org.allen.ins.underwriting.dao.dict.SumInsuredRiskDictMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.rc.factor.dao.RiskFactorMapper;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.allen.ins.underwriting.rc.factor.pojo.dto.RiskFactorCalculateDTO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        BigDecimal ageRiskValue = ageRiskDictMapper.getRiskValueByAge(policyHolder.getAge());
        BigDecimal occupationRiskValue = occupationRiskDictMapper.getRiskValueByOccupationName(policyHolder.getOccupation());
        BigDecimal amountRiskValue = sumInsuredRiskDictMapper.getRiskValueByAmount(request.getInsureAmount());

        // TODO 调用第三方接口核验住院记录

        // TODO 计算健康风险因子
        BigDecimal healthRiskValue  = BigDecimal.valueOf(0.10);

        // TODO 加权计算总风险因子
        BigDecimal totalRiskValue = BigDecimal.valueOf(1);

        RiskFactorRecord riskFactorRecord = new RiskFactorRecord()
                .setAgeRiskValue(ageRiskValue)
                .setOccupationRiskValue(occupationRiskValue)
                .setAmountRiskValue(amountRiskValue)
                .setHealthRiskValue(healthRiskValue)
                .setTotalRiskValue(totalRiskValue)
                .setPolicyHolderId(policyHolderId)
                .setCalculatorUser(SystemConstant.DEFAULT_CALCULATOR_USER)
                .setCalculateTime(LocalDateTime.now());

        boolean isSuccess = this.save(riskFactorRecord);
        if (!isSuccess) {
            throw new BusinessException("风险因子记录异常!");
        }

        RiskFactorVO riskFactorVO = BeanUtil.copyProperties(riskFactorRecord, RiskFactorVO.class);
        // TODO 风险值 映射为 等级
        riskFactorVO.setTotalRiskLevel("中等");
        return riskFactorVO;
    }
}
