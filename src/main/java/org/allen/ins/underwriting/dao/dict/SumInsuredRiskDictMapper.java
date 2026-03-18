package org.allen.ins.underwriting.dao.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.dict.SumInsuredRiskDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 保额风险字典Mapper接口（基于MyBatis-Plus）
 * 对应表：t_sum_insured_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Mapper
public interface SumInsuredRiskDictMapper extends BaseMapper<SumInsuredRiskDict> {

    /**
     * 核心业务方法：根据投保金额匹配对应的风险因子
     * 逻辑：查询 sum_insured_min ≤ amount ≤ sum_insured_max 且启用的风险规则
     *
     * @param amount 投保金额（单位：元）
     * @return 保额风险值（null表示无匹配规则）
     */
    @Select("SELECT risk_value FROM t_sum_insured_risk_dict " +
            "WHERE status = 1 " +
            "AND sum_insured_min <= #{amount} " +
            "AND sum_insured_max >= #{amount} " +
            "LIMIT 1")
    BigDecimal getRiskValueByAmount(@Param("amount") BigDecimal amount);

    /**
     * 扩展方法：根据保额匹配完整的风险规则（含风险等级）
     *
     * @param amount 投保金额（单位：元）
     * @return 完整的保额风险字典对象
     */
    @Select("SELECT * FROM t_sum_insured_risk_dict " +
            "WHERE status = 1 " +
            "AND sum_insured_min <= #{amount} " +
            "AND sum_insured_max >= #{amount} " +
            "LIMIT 1")
    SumInsuredRiskDict getByAmount(@Param("amount") BigDecimal amount);
}