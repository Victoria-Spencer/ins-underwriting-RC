package org.allen.ins.underwriting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 投保人Mapper接口
 */
@Mapper
public interface PolicyHolderMapper extends BaseMapper<PolicyHolder> {
}
