package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import org.springframework.stereotype.Service;


public interface UserService extends IService<Staff> {

    Result belong(Long staffId);
}
