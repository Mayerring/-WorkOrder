package com.example.spring_vue_demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStaffVO {
    private long id;
    private String staffNumber;
    private String companyCode;
    private String departmentCode;
    private String managerNUmber;
}
