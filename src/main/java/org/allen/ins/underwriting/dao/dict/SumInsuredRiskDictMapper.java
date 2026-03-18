package org.allen.ins.underwriting.dao.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.dict.SumInsuredRiskDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保额风险字典Mapper接口（基于MyBatis-Plus）
 * 对应表：t_sum_insured_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Mapper
public interface SumInsuredRiskDictMapper extends BaseMapper<SumInsuredRiskDict> {
}