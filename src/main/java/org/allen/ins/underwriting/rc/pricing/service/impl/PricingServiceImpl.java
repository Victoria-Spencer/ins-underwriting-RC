package org.allen.ins.underwriting.rc.pricing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.allen.ins.underwriting.rc.pricing.dao.PricingMapper;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.allen.ins.underwriting.rc.pricing.service.PricingService;
import org.springframework.stereotype.Service;

@Service
public class PricingServiceImpl  extends ServiceImpl<PricingMapper, PricingRecord>
        implements PricingService {

    @Override
    public PricingVO calculate(PricingRequestDTO request) {
        return null;
    }
}
