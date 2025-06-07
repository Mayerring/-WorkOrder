package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.param.AddStaffParam;
import com.example.spring_vue_demo.service.AdminService;
import com.example.spring_vue_demo.utils.OrganizationCodeUtils;
import com.example.spring_vue_demo.vo.AddStaffVO;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<StaffMapper, Staff> implements AdminService {

    @Override
    public Result addStaff(AddStaffParam param) {
        // 1. 构建实体类
        Staff staff = new Staff();

        // 生成员工编号（例如：STAFF202506061001）
        //String staffNumber = "STAFF" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String staffNumber="";
        // 假设生成公司编号/部门编号逻辑简单处理（真实情况可能查询字典表）
        String companyCode = "COM_" + param.getCompany().hashCode();
        String departmentCode = "DEP_" + param.getDepartment().hashCode();
        String managerNumber = "MGR_" + param.getManagerName().hashCode();

        // 设置员工信息
        staff.setStaffNumber(staffNumber);
        staff.setName(param.getName());
        staff.setCompany(param.getCompany());
        staff.setCompanyCode(companyCode);
        staff.setDepartment(param.getDepartment());
        staff.setDepartmentCode(departmentCode);
        staff.setPosition(param.getPosition());
        staff.setStatus(0);
        staff.setManagerName(param.getManagerName());
        staff.setManagerNumber(managerNumber);
        staff.setPhone(param.getPhone());
        staff.setEmail(param.getEmail());


        // 2. 保存到数据库
        //this.insert(staff); // 假设你已经配置了 MyBatis-Plus 的 mapper
                // 3. 封装返回值
        AddStaffVO vo = new AddStaffVO(staffNumber, companyCode, departmentCode, managerNumber);

        return null;
    }
}
