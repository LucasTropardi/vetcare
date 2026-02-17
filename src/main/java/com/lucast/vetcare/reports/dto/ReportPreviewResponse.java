package com.lucast.vetcare.reports.dto;

import java.util.List;
import java.util.Map;

public record ReportPreviewResponse(
        String reportKey,
        String title,
        List<ReportColumnResponse> columns,
        List<Map<String, Object>> rows,
        long totalElements,
        int totalPages,
        int page,
        int size
) {
}
