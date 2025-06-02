package com.example.spring_vue_demo.service;

import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.param.LoginParam;

public interface LoginService {
    Result login(LoginParam loginParam);
}
