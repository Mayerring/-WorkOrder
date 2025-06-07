package com.example.spring_vue_demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStaffVO {
    private String staffNumber;
    private String companyCode;
    private String departmentCode;
    private String managerNUmber;
}
