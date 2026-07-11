package com.example.workorder.cli.controller;

import com.example.workorder.cli.dto.request.QueryRequest;
import com.example.workorder.cli.dto.response.ApiResponse;
import com.example.workorder.cli.dto.response.DataCodeDTO;
import com.example.workorder.cli.dto.response.SchemaDTO;
import com.example.workorder.cli.interceptor.TraceIdInterceptor;
import com.example.workorder.cli.service.QueryService;
import com.example.workorder.cli.service.SchemaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private QueryService queryService;

    @GetMapping("/dataCodes")
    public ApiResponse<List<DataCodeDTO>> listDataCodes() {
        log.info("Listing all data codes");
        List<DataCodeDTO> dataCodes = schemaService.listDataCodes();
        return ApiResponse.success(dataCodes);
    }

    @GetMapping("/schema/{dataCode}")
    public ApiResponse<SchemaDTO> getSchema(
            @PathVariable String dataCode,
            HttpServletRequest request) {
        String traceId = request.getHeader(TraceIdInterceptor.TRACE_ID_HEADER);
        log.info("Getting schema for dataCode: {}, traceId: {}", dataCode, traceId);
        
        SchemaDTO schema = schemaService.getSchema(dataCode);
        if (schema == null) {
            return ApiResponse.error(404, "Schema not found for dataCode: " + dataCode, traceId);
        }
        return ApiResponse.success(schema, traceId);
    }

    @PostMapping("/query")
    public ApiResponse<Object> query(
            @Valid @RequestBody QueryRequest request,
            @RequestHeader("Authorization") String token,
            HttpServletRequest httpRequest) {
        String traceId = httpRequest.getHeader(TraceIdInterceptor.TRACE_ID_HEADER);
        log.info("Querying dataCode: {}, token: {}, traceId: {}", 
                request.getDataCode(), token != null ? "***" : null, traceId);

        Object result = queryService.query(
                request.getDataCode(),
                request.getParams(),
                token,
                traceId);

        return ApiResponse.success(result, traceId);
    }
}