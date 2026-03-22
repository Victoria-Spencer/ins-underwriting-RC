package org.allen.ins.underwriting.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.exception.BusinessException;
import org.allen.ins.underwriting.common.result.Result;
import org.allen.ins.underwriting.pojo.dto.PolicyHolderDTO;
import org.allen.ins.underwriting.pojo.vo.PolicyHolderVO;
import org.allen.ins.underwriting.service.PolicyHolderService;
import org.springframework.web.bind.annotation.*;

/**
 * 投保人信息管理Controller
 * 处理投保人信息的增删改查
 */
@RestController
@RequestMapping("/policyHolder")
public class PolicyHolderController {

    @Resource
    private PolicyHolderService policyHolderService;

    /**
     * 新增投保人信息（前端录入页调用）
     */
    @PostMapping("/save")
    public Result<Long> savePolicyHolder(@Valid @RequestBody PolicyHolderDTO dto) {
        Long holderId = policyHolderService.savePolicyHolder(dto);
        return Result.success(holderId);
    }

    /**
     * 根据ID查询投保人信息（运营后台/风控流程调用）
     */
    @GetMapping("/getHolderInfo/{id}")
    public Result<PolicyHolderVO> getPolicyHolderById(@PathVariable Long id) {
        PolicyHolderVO vo = policyHolderService.getPolicyHolderVOById(id);
        if (null == vo) {
            throw new BusinessException(404, "投保人ID不存在");
        }
        return Result.success(vo);
    }
}
