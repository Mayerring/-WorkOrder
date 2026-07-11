package com.example.spring_vue_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spring_vue_demo.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    /**
     * 根据公司名称和部门名称查询部门编号
     * @param companyName 公司名称
     * @param departmentName 部门名称
     * @return 部门编号，如果不存在返回null
     */
    String findDepartmentCodeByName(
            @Param("companyName") String companyName,
            @Param("departmentName") String departmentName);
    /**
     * 根据部门编码查询父部门Leader的ID
     * @param departmentCode 部门编码
     * @return 父部门Leader的ID，如果不存在返回null
     */
    Long findParentDepartmentLeaderId(@Param("departmentCode") String departmentCode);
}
