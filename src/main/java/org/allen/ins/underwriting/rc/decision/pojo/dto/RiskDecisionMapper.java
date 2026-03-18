package org.allen.ins.underwriting.rc.decision.pojo.dto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RiskDecisionMapper extends BaseMapper<RiskDecisionRecord> {
}
