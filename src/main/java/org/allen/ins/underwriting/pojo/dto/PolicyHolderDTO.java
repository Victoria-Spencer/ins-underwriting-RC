package org.allen.ins.underwriting.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 投保人信息录入DTO（前端传入的参数）
 */
@Data
public class PolicyHolderDTO {
    /**
     * 投保人姓名（非空）
     */
    @NotBlank(message = "投保人姓名不能为空")
    private String name;

    /**
     * 身份证号（非空，格式校验）
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
            message = "身份证号格式错误")
    private String idCard;

    /**
     * 年龄（非空）
     */
    @NotNull(message = "年龄不能为空")
    private Integer age;

    /**
     * 职业（非空）
     */
    @NotBlank(message = "职业不能为空")
    private String occupation;

    /**
     * 健康告知
     */
    private String healthInfo;

    /**
     * 手机号（非空，格式校验）
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 投保险种类型
     */
    private String insuranceType;
}
