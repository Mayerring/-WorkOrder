package com.example.spring_vue_demo.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.mapper.WorkOrderMapper;
import com.example.spring_vue_demo.param.LoginParam;
import com.example.spring_vue_demo.service.LoginService;
import com.example.spring_vue_demo.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginServiceImpl extends ServiceImpl<StaffMapper, Staff> implements LoginService {
    @Autowired
    private StaffMapper staffMapper;
    @Override
    public Result login(LoginParam loginParam) {
        //校验密码
        Staff staff = staffMapper.selectOne(
                new LambdaQueryWrapper<Staff>()
                        .eq(Staff::getStaffNumber, loginParam.getStaffNumber())
                        .or()
                        .eq(Staff::getPhone, loginParam.getPhone())
        );
        if(staff == null){
            return Result.error("用户不存在");
        }
        log.info(staff.toString());
        if(!staff.getPassword().equals(loginParam.getPassword())) {
            return Result.error("账号或者密码错误");
        }
        //生成token
        String token = TokenUtil.generateToken(staff);

        //返回token
        return Result.success(token);
    }
}
