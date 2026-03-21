package org.allen.ins.underwriting.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.pojo.domain.UnderwritingRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UnderwritingMapper  extends BaseMapper<UnderwritingRecord> {
}
