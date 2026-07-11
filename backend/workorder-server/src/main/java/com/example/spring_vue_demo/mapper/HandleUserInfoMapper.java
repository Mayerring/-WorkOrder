package com.example.spring_vue_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spring_vue_demo.entity.HandleUserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HandleUserInfoMapper extends BaseMapper<HandleUserInfo> {
}
