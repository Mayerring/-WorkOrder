package com.example.workorder.cli.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDTO {

    private String dataCode;
    private String name;
    private String description;
    private Map<String, Object> inputSchema;
    private Map<String, Object> outputSchema;
}