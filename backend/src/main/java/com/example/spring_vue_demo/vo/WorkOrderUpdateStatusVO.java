package com.example.spring_vue_demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @date 2025/05/25
 */
@Data
@AllArgsConstructor
public class WorkOrderUpdateStatusVO {
    private List<String> successCodes;
    private List<String> failureCodes;
    private String failureMessage;
}
