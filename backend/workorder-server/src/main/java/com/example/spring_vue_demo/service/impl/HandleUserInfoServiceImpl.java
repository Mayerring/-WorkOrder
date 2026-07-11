package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import com.example.spring_vue_demo.mapper.HandleUserInfoMapper;
import com.example.spring_vue_demo.service.HandleUserInfoService;
import org.springframework.stereotype.Service;

/**
 * @author wtt
 * @date 2025/06/01
 */
@Service
public class HandleUserInfoServiceImpl extends ServiceImpl<HandleUserInfoMapper, HandleUserInfo> implements HandleUserInfoService {
}
