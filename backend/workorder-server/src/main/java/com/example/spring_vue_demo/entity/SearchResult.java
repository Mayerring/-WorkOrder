package com.example.spring_vue_demo.entity;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author wtt
 * @date 2025/08/17
 */
@Data
public class SearchResult<T> {
    private List<T> list;
    private long total;
    private int pageNum;
    private int pageSize;

    public SearchResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
