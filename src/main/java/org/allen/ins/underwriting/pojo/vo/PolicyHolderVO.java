package org.allen.ins.underwriting.pojo.vo;


import lombok.Data;

@Data
public class PolicyHolderVO {
    private Long id;
    private String name;
    private String idCard;
    private String phone;
    private String insuranceType;
}