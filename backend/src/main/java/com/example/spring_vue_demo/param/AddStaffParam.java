package com.example.spring_vue_demo.param;

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
public class AddStaffParam {
    private String name;
    private String companyName;
    private String departmentName;
    private String position;
    private String managerName;
    private String phone;
    private String email;
}
