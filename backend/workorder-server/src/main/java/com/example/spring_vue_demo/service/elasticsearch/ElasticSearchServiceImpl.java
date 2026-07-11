package com.example.spring_vue_demo.service.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import com.example.spring_vue_demo.entity.WorkOrder;
import com.example.spring_vue_demo.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author wtt
 * @date 2026/03/14
 */
@Service
@Slf4j
public class ElasticSearchServiceImpl implements ElasticSearchService {
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private static final String INDEX_NAME = "work_order_index";

    public void indexWorkOrder(WorkOrder workOrder) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(workOrder.getId().toString())
                    .document(workOrder)
            );

            log.debug("Indexed document: {}", response.result());
        } catch (IOException e) {
            log.error("Failed to index work order: {}", workOrder.getId(), e);
            throw new RuntimeException("Index work order failed", e);
        }
    }

    public void updateWorkOrder(WorkOrder workOrder) {
        try {
            UpdateResponse response = elasticsearchClient.update(u -> u
                            .index(INDEX_NAME)
                            .id(workOrder.getId().toString())
                            .doc(workOrder)
                            .docAsUpsert(true),  // 如果不存在则创建
                    WorkOrder.class
            );

            log.debug("Updated document: {}", response.result());
        } catch (IOException e) {
            log.error("Failed to update work order: {}", workOrder.getId(), e);
            throw new RuntimeException("Update work order failed", e);
        }
    }

    public void deleteWorkOrder(String id) {
        try {
            DeleteResponse response = elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(id)
            );

            log.debug("Deleted document: {}", response.result());
        } catch (IOException e) {
            log.error("Failed to delete work order: {}", id, e);
            throw new RuntimeException("Delete work order failed", e);
        }
    }

    public void bulkIndex(List<WorkOrder> workOrders) {
        if (workOrders.isEmpty()) return;

        try {
            BulkRequest.Builder br = new BulkRequest.Builder();

            for (WorkOrder order : workOrders) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(INDEX_NAME)
                                .id(order.getId().toString())
                                .document(order)
                        )
                );
            }

            BulkResponse result = elasticsearchClient.bulk(br.build());

            if (result.errors()) {
                log.error("Bulk index had errors");
                result.items().forEach(item -> {
                    if (item.error() != null) {
                        log.error("Error for document {}: {}", item.id(), item.error().reason());
                    }
                });
            }
        } catch (IOException e) {
            log.error("Bulk index failed", e);
            throw new RuntimeException("Bulk index failed", e);
        }
    }
}
