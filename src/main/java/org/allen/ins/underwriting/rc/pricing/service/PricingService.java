package org.allen.ins.underwriting.rc.pricing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;

public interface PricingService  extends IService<PricingRecord> {
    PricingVO calculate(PricingRequestDTO request);
}
