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
import com.example.spring_vue_demo.param.AddDepartmentParam;
import com.example.spring_vue_demo.service.DepartmentService;
import com.example.spring_vue_demo.utils.OrganizationCodeUtils;
import com.example.spring_vue_demo.vo.AddDepartmentVO;
import lombok.val;
import org.apache.ibatis.type.LongTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private StaffMapper staffMapper;
    @Override
    public Result addDepartment(AddDepartmentParam param) {
        Department department = new Department();

        //查公司
        Company company = companyMapper.selectOne(
                new QueryWrapper<Company>().eq("name", param.getCompanyName())
        );
        if (company == null) {
            return Result.error("公司不存在: " + param.getCompanyName());
        }
        department.setCompanyCode(company.getCode());
        //查上级部门
        if (param.getParentDepartmentName() != null && !param.getParentDepartmentName().isEmpty()) {
            Department parent = departmentMapper.selectOne(
                    new QueryWrapper<Department>()
                            .eq("name", param.getParentDepartmentName())
                            .eq("company_id", company.getId())
            );
            if (parent == null) {
                return Result.error("父部门不存在: " + param.getParentDepartmentName());
            }
            String parentDepartmentCode = parent.getCode();
            department.setParentDepartmentCode(parentDepartmentCode);
        }
        // 查主管
        Staff leader = staffMapper.selectOne(
                new QueryWrapper<Staff>().eq("name", param.getLeaderName())
        );
//        if (leader == null) {
//            return Result.error("负责人不存在: " + param.getLeaderName());
//        }
        if(leader != null){
            department.setLeaderNumber(leader.getStaffNumber());
        }
        // 4. 生成部门编码（可根据公司+已有部门数）
        Integer maxId = departmentMapper.selectObjs(
                        new QueryWrapper<Department>()
                                .select("MAX(id)")
                ).stream()
                .map(obj -> obj != null ? Integer.parseInt(obj.toString()) : 0)
                .findFirst()
                .orElse(0);
        String departmentCode = OrganizationCodeUtils.generateDepartmentCode(company.getCode(),maxId);
        department.setCode(departmentCode);
        department.setName(param.getName());;
        departmentMapper.insert(department);

        // 6. 返回结果
        AddDepartmentVO vo = new AddDepartmentVO();
        vo.setDepartmentCode(departmentCode);
        vo.setId(department.getId());
        if(leader != null){
            vo.setLeaderNumber(leader.getStaffNumber());
        }
        return Result.success(vo);
    }

    @Override
    public Result allDepartmentInCompany(String companyName) {
        // 1. 根据公司名查询公司
        Company company = companyMapper.selectOne(
                new QueryWrapper<Company>().eq("name", companyName)
        );

        if (company == null) {
            return Result.error("公司不存在: " + companyName);
        }

        // 2. 查询该公司下的所有部门
        List<Department> departments = departmentMapper.selectList(
                new QueryWrapper<Department>().eq("company_code", company.getCode())
        );
        return Result.success(departments);
    }
}
