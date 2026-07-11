package com.example.workorder.api.entity;

import lombok.Data;

@Data
public class WorkOrder {
    private Long id;
    private String code;
    private Integer type;
    private String title;
    private Integer priorityLevel;
    private Integer status;
    private String createTime;
}