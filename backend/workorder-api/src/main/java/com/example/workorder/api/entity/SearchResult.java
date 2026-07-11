package com.example.workorder.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult<T> {
    private List<T> list;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
}