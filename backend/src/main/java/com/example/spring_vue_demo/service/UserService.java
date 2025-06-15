package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.param.ChangePersonalInfoParam;
import com.example.spring_vue_demo.param.StaffPageParam;
import org.springframework.stereotype.Service;


public interface UserService extends IService<Staff> {

    Result belong(Long staffId);

    Result organizationStructure();

    Result staffPage(StaffPageParam param);

    Result change(ChangePersonalInfoParam param);
}
