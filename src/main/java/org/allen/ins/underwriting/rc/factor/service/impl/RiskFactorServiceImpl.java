package org.allen.ins.underwriting.rc.factor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.allen.ins.underwriting.dao.dict.OccupationRiskDictMapper;
import org.allen.ins.underwriting.rc.factor.dao.RiskFactorMapper;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.allen.ins.underwriting.rc.factor.pojo.dto.RiskFactorCalculateDTO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.springframework.stereotype.Service;

@Service
public class RiskFactorServiceImpl extends ServiceImpl<RiskFactorMapper, RiskFactorRecord>
        implements RiskFactorService {

    @Resource
    private OccupationRiskDictMapper occupationRiskDictMapper;
    @Override
    public RiskFactorVO calculate(RiskFactorCalculateDTO request) {

        return null;
    }
}
