package com.example.spring_vue_demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyVO {
    @Schema(description = "公司编码")
    private String companyCode;
    @Schema(description = "公司Id")
    private Integer id;
}
