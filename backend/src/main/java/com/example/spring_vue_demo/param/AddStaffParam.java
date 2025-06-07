package com.example.spring_vue_demo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStaffParam {
    private String name;
    private String company;
    private String department;
    private String position;
    private String status;
    private String managerName;
    private String phone;
    private String email;
}
