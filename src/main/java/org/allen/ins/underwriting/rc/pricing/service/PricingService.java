package org.allen.ins.underwriting.rc.pricing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.allen.ins.underwriting.rc.pricing.pojo.dto.PricingCoreDTO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingCoreVO;

public interface PricingService  extends IService<PricingRecord> {
    PricingCoreVO calculatePremium(PricingCoreDTO request);
}
