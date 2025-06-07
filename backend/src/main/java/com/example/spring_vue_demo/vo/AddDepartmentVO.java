package com.example.spring_vue_demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDepartmentVO {
    private int id;
    private String departmentCode;
    private String leaderNumber;
}
