package org.allen.ins.underwriting.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.pojo.dto.PolicyHolderDTO;
import org.allen.ins.underwriting.pojo.vo.PolicyHolderVO;
import org.allen.ins.underwriting.service.PolicyHolderService;
import org.springframework.stereotype.Service;

/**
 * Service实现类，继承ServiceImpl<Mapper, Entity>
 */
@Service
public class PolicyHolderServiceImpl extends ServiceImpl<PolicyHolderMapper, PolicyHolder>
        implements PolicyHolderService {

    @Override
    public Long savePolicyHolder(PolicyHolderDTO dto) {
        // TODO 身份证号加密存储
        PolicyHolder policyHolder = BeanUtil.copyProperties(dto, PolicyHolder.class);
        save(policyHolder);
        return policyHolder.getId();
    }

    @Override
    public PolicyHolderVO getPolicyHolderVOById(Long id) {
        PolicyHolder policyHolder = getById(id);
        return BeanUtil.copyProperties(policyHolder, PolicyHolderVO.class);
    }
}
