package com.lucast.vetcare.reports.dto;

import com.lucast.vetcare.reports.ReportFilterType;

import java.util.List;

public record ReportFilterResponse(
        String key,
        String label,
        ReportFilterType type,
        List<ReportOptionResponse> options,
        String placeholder
) {
}
