package com.example.spring_vue_demo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDepartmentParam {
    private String name;
    private String code;
    private String parentDepartmentCode;
    private String companyCode;
    private String leaderNumber;
}
