package com.example.spring_vue_demo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCompanyParam {
    private String name;
    private String parentCompanyName;
    private int level;
}
