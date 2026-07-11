package com.example.spring_vue_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.param.AddStaffParam;
import com.example.spring_vue_demo.param.ChangeStaffInfoParam;
import com.example.spring_vue_demo.param.StaffPageParam;

/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

public interface StaffService extends IService<Staff> {
    Result addStaff(AddStaffParam param);

    Result allStaff();

    Result staffPage(StaffPageParam param);

    Result delete(Long id);

    Result changeStaff(ChangeStaffInfoParam param);
}
