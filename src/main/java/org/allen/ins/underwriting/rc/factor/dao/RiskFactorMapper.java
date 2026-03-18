package org.allen.ins.underwriting.rc.factor.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiskFactorMapper  extends BaseMapper<RiskFactorRecord> {
}
