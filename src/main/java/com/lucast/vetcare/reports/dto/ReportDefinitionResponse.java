package com.lucast.vetcare.reports.dto;

import com.lucast.vetcare.reports.ReportFormat;

import java.util.List;

public record ReportDefinitionResponse(
        String key,
        String title,
        String description,
        List<ReportFilterResponse> filters,
        List<ReportColumnResponse> columns,
        List<ReportFormat> formats
) {
}
