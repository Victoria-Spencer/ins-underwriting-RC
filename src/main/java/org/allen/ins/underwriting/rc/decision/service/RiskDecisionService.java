package org.allen.ins.underwriting.rc.decision.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionDTO;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;

public interface RiskDecisionService extends IService<RiskDecisionRecord> {
    RiskDecisionVO calculate(RiskDecisionDTO request);
}
