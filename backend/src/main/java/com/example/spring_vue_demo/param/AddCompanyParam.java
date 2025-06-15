package com.example.spring_vue_demo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyParam {
    @Schema(description = "公司名")
    private String name;
    @Schema(description = "上级公司的名字")
    private String parentCompanyName;
    @Schema(description = "公司级别，总部/省/市")
    private Integer level;
}
