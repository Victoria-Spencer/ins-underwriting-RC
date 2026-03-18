package org.allen.ins.underwriting.rc.pricing.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.rc.pricing.pojo.domain.PricingRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PricingMapper  extends BaseMapper<PricingRecord> {
}
