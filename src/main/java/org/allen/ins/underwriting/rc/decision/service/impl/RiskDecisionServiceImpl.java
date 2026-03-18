package org.allen.ins.underwriting.rc.decision.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionMapper;
import org.allen.ins.underwriting.rc.decision.service.RiskDecisionService;
import org.springframework.stereotype.Service;

@Service
public class RiskDecisionServiceImpl extends ServiceImpl<RiskDecisionMapper,RiskDecisionRecord>
        implements RiskDecisionService {
}
