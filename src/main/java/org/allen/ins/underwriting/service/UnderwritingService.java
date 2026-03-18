package org.allen.ins.underwriting.service;

import jakarta.validation.Valid;
import org.allen.ins.underwriting.pojo.dto.UnderwritingRequestDTO;
import org.allen.ins.underwriting.pojo.vo.UnderwritingResponseVO;

public interface UnderwritingService {
    UnderwritingResponseVO calculateUnderwritingRisk(@Valid UnderwritingRequestDTO request);
}
