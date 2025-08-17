package com.example.spring_vue_demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.example.spring_vue_demo.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SpringVueDemoApplicationTests {
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private WorkOrderService workOrderService;

    @Test
    void testElasticsearchConnection() throws IOException {
        InfoResponse info = elasticsearchClient.info();
        assertNotNull(info);
        assertNotNull(info.clusterName());
        assertNotNull(info.version());
        System.out.println("Connected to Elasticsearch cluster: " + info.clusterName() +
                ", version: " + info.version().number());
    }

    @Test
    void testFullSyncWorkOrdersToEs()throws IOException {
//        workOrderService.fullSyncWorkOrdersToEs();
    }

    @Test
    void testElasticSearch (){
        String keyword="服务器";
        int pageNum=1;
        int pageSize=10;
        try {
            workOrderService.searchWorkOrders(keyword, pageNum, pageSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
