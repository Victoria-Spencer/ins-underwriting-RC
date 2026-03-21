package org.allen.ins.underwriting.rc.antiselection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.allen.ins.underwriting.rc.antiselection.pojo.domain.AntiselectionRecord;
import org.allen.ins.underwriting.rc.antiselection.pojo.dto.AntiselectionRequestDTO;
import org.allen.ins.underwriting.rc.antiselection.pojo.vo.AntiselectionVO;

public interface AntiselectionService extends IService<AntiselectionRecord> {
    AntiselectionVO check(AntiselectionRequestDTO request);
}
