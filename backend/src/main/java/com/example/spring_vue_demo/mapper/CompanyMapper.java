package com.example.spring_vue_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spring_vue_demo.entity.Company;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
    @Select("SELECT name FROM company")
    List<String> selectAllCompanyNames();

}
