package org.allen.ins.underwriting.service.impl;

import org.allen.ins.underwriting.pojo.dto.UnderwritingRequestDTO;
import org.allen.ins.underwriting.pojo.vo.UnderwritingResponseVO;
import org.allen.ins.underwriting.service.UnderwritingService;
import org.springframework.stereotype.Service;

@Service
public class UnderwritingServiceImpl implements UnderwritingService {
    @Override
    public UnderwritingResponseVO calculateUnderwritingRisk(UnderwritingRequestDTO request) {
        return null;
    }
}
