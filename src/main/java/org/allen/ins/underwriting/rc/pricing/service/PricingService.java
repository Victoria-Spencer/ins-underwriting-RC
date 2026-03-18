package org.allen.ins.underwriting.rc.pricing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.allen.ins.underwriting.rc.pricing.pojo.dto.PricingDTO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingVO;

public interface PricingService  extends IService<PricingRecord> {
    PricingVO calculate(PricingDTO request);
}
