package com.lucast.vetcare.reports.dto;

import java.util.List;
import java.util.Map;

public record ReportRunRequest(
        Map<String, Object> filters,
        List<String> columns,
        Integer page,
        Integer size,
        List<String> sort
) {
}
