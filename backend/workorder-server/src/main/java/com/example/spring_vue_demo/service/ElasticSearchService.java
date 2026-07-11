package com.example.spring_vue_demo.service;

import com.example.spring_vue_demo.entity.WorkOrder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wtt
 * @date 2026/03/14
 */
@Service
public interface ElasticSearchService {
    public void indexWorkOrder(WorkOrder workOrder);

    public void updateWorkOrder(WorkOrder workOrder);
    public void deleteWorkOrder(String id);
    public void bulkIndex(List<WorkOrder> workOrders);
}
