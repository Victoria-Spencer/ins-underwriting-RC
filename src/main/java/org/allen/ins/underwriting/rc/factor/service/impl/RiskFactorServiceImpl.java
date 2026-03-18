package org.allen.ins.underwriting.rc.factor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.allen.ins.underwriting.rc.factor.dao.RiskFactorMapper;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.allen.ins.underwriting.rc.factor.service.RiskFactorService;
import org.springframework.stereotype.Service;

@Service
public class RiskFactorServiceImpl extends ServiceImpl<RiskFactorMapper, RiskFactorRecord>
        implements RiskFactorService {
}
