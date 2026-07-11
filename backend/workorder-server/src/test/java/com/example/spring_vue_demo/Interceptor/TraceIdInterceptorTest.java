package com.example.spring_vue_demo.Interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraceIdInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    private TraceIdInterceptor interceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new TraceIdInterceptor();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void preHandle_ShouldGenerateTraceId_WhenNotProvided() {
        when(request.getHeader(TraceIdInterceptor.TRACE_ID_HEADER)).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        String traceId = MDC.get(TraceIdInterceptor.TRACE_ID_KEY);
        assertNotNull(traceId);
        assertTrue(java.util.UUID.fromString(traceId) != null);
        verify(response).setHeader(TraceIdInterceptor.TRACE_ID_HEADER, traceId);
    }

    @Test
    void preHandle_ShouldUseProvidedTraceId() {
        String expectedTraceId = "test-trace-id-12345";
        when(request.getHeader(TraceIdInterceptor.TRACE_ID_HEADER)).thenReturn(expectedTraceId);

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        assertEquals(expectedTraceId, MDC.get(TraceIdInterceptor.TRACE_ID_KEY));
        verify(response).setHeader(TraceIdInterceptor.TRACE_ID_HEADER, expectedTraceId);
    }

    @Test
    void preHandle_ShouldGenerateTraceId_WhenEmptyString() {
        when(request.getHeader(TraceIdInterceptor.TRACE_ID_HEADER)).thenReturn("");

        boolean result = interceptor.preHandle(request, response, handler);

        assertTrue(result);
        String traceId = MDC.get(TraceIdInterceptor.TRACE_ID_KEY);
        assertNotNull(traceId);
        assertFalse(traceId.isEmpty());
    }

    @Test
    void afterCompletion_ShouldRemoveTraceIdFromMDC() {
        String traceId = "test-trace-id";
        MDC.put(TraceIdInterceptor.TRACE_ID_KEY, traceId);

        interceptor.afterCompletion(request, response, handler, null);

        assertNull(MDC.get(TraceIdInterceptor.TRACE_ID_KEY));
    }

    @Test
    void afterCompletion_ShouldHandleNullException() {
        String traceId = "test-trace-id";
        MDC.put(TraceIdInterceptor.TRACE_ID_KEY, traceId);

        assertDoesNotThrow(() -> interceptor.afterCompletion(request, response, handler, new RuntimeException("test")));

        assertNull(MDC.get(TraceIdInterceptor.TRACE_ID_KEY));
    }
}