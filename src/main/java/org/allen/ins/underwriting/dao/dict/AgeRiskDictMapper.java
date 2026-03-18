package org.allen.ins.underwriting.dao.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.dict.AgeRiskDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 年龄风险字典Mapper接口（基于MyBatis-Plus）
 * 对应表：t_age_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Mapper
public interface AgeRiskDictMapper extends BaseMapper<AgeRiskDict> {
}