package org.allen.ins.underwriting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.pojo.dto.PolicyHolderDTO;
import org.allen.ins.underwriting.pojo.vo.PolicyHolderVO;

public interface PolicyHolderService extends IService<PolicyHolder> {
    Long savePolicyHolder(@Valid PolicyHolderDTO dto);

    PolicyHolderVO getPolicyHolderVOById(Long id);
}
