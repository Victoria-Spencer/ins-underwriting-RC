package org.allen.ins.underwriting.rc.antiselection.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.allen.ins.underwriting.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 逆选择防控细分Controller
 * 运营侧单独查询逆选择风险时调用
 */
@RestController
@RequestMapping("/rc/antiselection")
public class AntiselectionController {

    @Resource
    private AntiselectionService antiselectionService;

    /**
     * 单独校验逆选择风险（如核查隐瞒住院记录）
     */
    @PostMapping("/check")
    public Result<AntiselectionVO> checkAntiselection(@Valid @RequestBody AntiselectionRequestDTO request) {
        try {
            AntiselectionVO vo = antiselectionService.check(request);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.fail("逆选择风险校验失败：" + e.getMessage());
        }
    }
}