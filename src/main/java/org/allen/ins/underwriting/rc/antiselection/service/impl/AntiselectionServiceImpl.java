package org.allen.ins.underwriting.rc.antiselection.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.allen.ins.underwriting.rc.antiselection.dao.AntiselectionMapper;
import org.allen.ins.underwriting.rc.antiselection.pojo.domain.AntiselectionRecord;
import org.allen.ins.underwriting.rc.antiselection.service.AntiselectionService;
import org.springframework.stereotype.Service;

@Service
public class AntiselectionServiceImpl extends ServiceImpl<AntiselectionMapper, AntiselectionRecord>
        implements AntiselectionService {
    @Override
    public AntiselectionVO check(AntiselectionRequestDTO request) {
        return null;
    }
}
