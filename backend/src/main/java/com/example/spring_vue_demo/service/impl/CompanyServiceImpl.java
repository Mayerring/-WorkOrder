package com.example.spring_vue_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spring_vue_demo.entity.Company;
import com.example.spring_vue_demo.entity.Result;
import com.example.spring_vue_demo.mapper.CompanyMapper;
import com.example.spring_vue_demo.param.AddCompanyParam;
import com.example.spring_vue_demo.service.CompanyService;
import com.example.spring_vue_demo.utils.OrderCodeUtils;
import com.example.spring_vue_demo.utils.OrganizationCodeUtils;
import com.example.spring_vue_demo.vo.AddCompanyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author WangDayu
 * @date 2025/6/7
 */

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Autowired
    private CompanyMapper companyMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public Result addCompany(AddCompanyParam param) {
        String code = OrganizationCodeUtils.generateCompanyCode();
        Company company = new Company();
        company.setName(param.getName());
        company.setLevel(param.getLevel());
        log.debug(param.toString());
        if (param.getParentCompanyName() != null && !param.getParentCompanyName().isEmpty()) {
            Company parent = companyMapper.selectOne(
                    new QueryWrapper<Company>().eq("name", param.getParentCompanyName())
            );
            if (parent == null) {
                return Result.error("未找到父公司名称为：" + param.getParentCompanyName());
            }
            company.setParentCompanyCode(parent.getCode());
        } else {
            company.setParentCompanyCode(null); // 或者不设置，默认数据库为 NULL
        }
        company.setCode(code);
        company.setCreateTime(formatter.format(LocalDateTime.now()));
        company.setUpdateTime(formatter.format(LocalDateTime.now()));
        boolean isSaved = this.save(company);
        if(isSaved){
            AddCompanyVO addCompanyVO = new AddCompanyVO();
            addCompanyVO.setCompanyCode(company.getCode());
            addCompanyVO.setId(company.getId());
            return Result.success(addCompanyVO);
        }
        return Result.error("插入失败");
    }

    @Override
    public Result allCompany() {
        List<Company> companies = companyMapper.selectList(null);
        return Result.success(companies);
    }
}
