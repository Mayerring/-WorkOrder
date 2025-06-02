package com.example.spring_vue_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spring_vue_demo.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
