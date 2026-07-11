package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.AddCompanyParam;

public interface CompanyService extends IService<Company> {
    Result addCompany(AddCompanyParam param);

    Result allCompany();

    Result delete(Long id);
}
