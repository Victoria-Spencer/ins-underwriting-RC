package org.allen.ins.underwriting.rc.antiselection.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.allen.ins.underwriting.rc.antiselection.pojo.domain.AntiselectionRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AntiselectionMapper extends BaseMapper<AntiselectionRecord> {
}
