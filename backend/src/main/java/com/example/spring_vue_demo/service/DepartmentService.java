package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Department;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddDepartmentParam;

public interface DepartmentService extends IService<Department> {
    Result addDepartment(AddDepartmentParam param);

    Result allDepartmentInCompany(String companyName);
}
