package org.allen.ins.underwriting.dao.dict;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.dict.OccupationRiskDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 职业风险字典Mapper接口（基于MyBatis-Plus）
 * 对应表：t_occupation_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Mapper
public interface OccupationRiskDictMapper extends BaseMapper<OccupationRiskDict> {

    /**
     * 核心业务方法：根据职业名称匹配对应的风险因子
     * 逻辑：查询职业名称匹配且启用的风险规则
     *
     * @param occupationName 职业名称（如“办公室职员”“教师”“司机”等）
     * @return 职业风险值（null表示无匹配规则）
     */
    @Select("SELECT risk_value FROM t_occupation_risk_dict " +
            "WHERE status = 1 " +
            "AND occupation_name = #{occupationName} " +
            "LIMIT 1")
    BigDecimal getRiskValueByOccupationName(@Param("occupationName") String occupationName);

    /**
     * 扩展方法：根据职业名称匹配完整的风险规则（含风险等级）
     *
     * @param occupationName 职业名称（如“办公室职员”“教师”“司机”等）
     * @return 完整的职业风险字典对象
     */
    @Select("SELECT * FROM t_occupation_risk_dict " +
            "WHERE status = 1 " +
            "AND occupation_name = #{occupationName} " +
            "LIMIT 1")
    OccupationRiskDict getByOccupationName(@Param("occupationName") String occupationName);
}