package com.example.spring_vue_demo.service;

import org.springframework.stereotype.Service;

@Service
public interface DashboardService {

    Object getData();

    Object getTodo(Object param);

    Object getStatus(Object param);

    Object getHandleQuantity(Object param);

    Object getMessages(Object param);
}
