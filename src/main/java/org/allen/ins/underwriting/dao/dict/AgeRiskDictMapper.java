package org.allen.ins.underwriting.dao.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.dict.AgeRiskDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 年龄风险字典Mapper接口（基于MyBatis-Plus）
 * 对应表：t_age_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Mapper
public interface AgeRiskDictMapper extends BaseMapper<AgeRiskDict> {

    /**
     * 核心业务方法：根据投保人年龄匹配对应的风险因子
     * 逻辑：查询 age_min ≤ age ≤ age_max 且启用的风险规则
     *
     * @param age 投保人年龄
     * @return 年龄风险值（null表示无匹配规则）
     */
    @Select("SELECT risk_value FROM t_age_risk_dict " +
            "WHERE status = 1 " +
            "AND age_min <= #{age} " +
            "AND age_max >= #{age} " +
            "LIMIT 1")
    BigDecimal getRiskValueByAge(@Param("age") Integer age);

    /**
     * 扩展方法：根据年龄匹配完整的风险规则（含风险等级）
     *
     * @param age 投保人年龄
     * @return 完整的年龄风险字典对象
     */
    @Select("SELECT * FROM t_age_risk_dict " +
            "WHERE status = 1 " +
            "AND age_min <= #{age} " +
            "AND age_max >= #{age} " +
            "LIMIT 1")
    AgeRiskDict getByAge(@Param("age") Integer age);
}