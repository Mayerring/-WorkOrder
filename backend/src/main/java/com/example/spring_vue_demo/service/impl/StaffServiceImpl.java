package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Department;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.entity.Staff;
import com.example.spring_vue_demo.mapper.CompanyMapper;
import com.example.spring_vue_demo.mapper.DepartmentMapper;
import com.example.spring_vue_demo.mapper.StaffMapper;
import com.example.spring_vue_demo.param.AddStaffParam;
import com.example.spring_vue_demo.service.DepartmentService;
import com.example.spring_vue_demo.service.StaffService;
import com.example.spring_vue_demo.utils.OrganizationCodeUtils;
import com.example.spring_vue_demo.vo.AddStaffVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public Result addStaff(AddStaffParam param) {
        Staff staff = new Staff();
        // 1. 查公司信息
        Company company = companyMapper.selectOne(
                new QueryWrapper<Company>().eq("name", param.getCompanyName())
        );
        if (company == null) {
            return Result.error("公司不存在");
        }
        staff.setCompany(param.getCompanyName());
        staff.setCompanyCode(company.getCode());

        // 2. 查部门信息
        Department department = departmentMapper.selectOne(
                new QueryWrapper<Department>()
                        .eq("name", param.getDepartmentName())
                        .eq("company_code", company.getCode())
        );
        if (department == null) {
            return Result.error("部门不存在");
        }
        staff.setDepartment(param.getDepartmentName());
        staff.setDepartmentCode(department.getCode());

        // 3.查主管
        if(param.getManagerName() != null && !param.getManagerName().equals("")) {
            Staff manager = staffMapper.selectOne(
                    new QueryWrapper<Staff>().eq("name", param.getManagerName())
            );
            if (manager == null) {
                return Result.error("主管不存在");
            }
            staff.setManagerName(manager.getManagerName());
            staff.setManagerNumber(manager.getStaffNumber());
        }
        staff.setName(param.getName());
        staff.setStatus(0);
        staff.setPosition(param.getPosition());
        staff.setPhone(param.getPhone());
        staff.setEmail(param.getEmail());
        //默认密码，员工可以自行修改
        staff.setPassword("123456");
        //生成员工编号
        String staffNumber = OrganizationCodeUtils.generateStaffCode();
        staff.setStaffNumber(staffNumber);
        staffMapper.insert(staff);

        // 6. 构建返回 VO
        AddStaffVO vo = new AddStaffVO();
        vo.setId(staff.getId());
        vo.setStaffNumber(staffNumber);
        vo.setCompanyCode(staff.getCompanyCode());
        vo.setDepartmentCode(staff.getDepartmentCode());
        vo.setManagerNUmber(staff.getManagerNumber());

        return Result.success(vo);
    }

    @Override
    public Result allStaff() {
        List<Staff> staffList = staffMapper.selectList(null); // 查询所有员工
        return Result.success(staffList);
    }
}
