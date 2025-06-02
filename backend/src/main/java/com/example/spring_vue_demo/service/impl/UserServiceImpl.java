package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.service.UserService;
import com.example.spring_vue_demo.vo.StaffBelongInfoVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<StaffMapper, Staff> implements UserService {

    @Override
    public Result belong(Long staffId) {
        // 1. 根据ID查Staff
        Staff staff = this.getById(staffId);
        if (staff == null) {
            return Result.error("员工不存在");
        }

        // 2. 构造VO
        StaffBelongInfoVO vo = new StaffBelongInfoVO();
        vo.setStaffNumber(staff.getStaffNumber());
        vo.setCompanyCode(staff.getCompanyCode());
        vo.setCompanyName(staff.getCompany());
        vo.setDepartmentCode(staff.getDepartmentCode());
        vo.setDepartmentName(staff.getDepartment());
        vo.setPosition(staff.getPosition());

        // 3. 返回
        return Result.success(vo);
    }
}
