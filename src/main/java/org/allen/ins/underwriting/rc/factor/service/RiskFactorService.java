package org.allen.ins.underwriting.rc.factor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.allen.ins.underwriting.rc.factor.pojo.dto.RiskFactorCalculateDTO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;

public interface RiskFactorService  extends IService<RiskFactorRecord> {
    RiskFactorVO calculate(@Valid RiskFactorCalculateDTO request);
}
