package com.lucast.vetcare.reports;

import com.lucast.vetcare.reports.dto.ReportDefinitionResponse;
import com.lucast.vetcare.reports.dto.ReportPreviewResponse;
import com.lucast.vetcare.reports.dto.ReportRunRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Centralized dynamic reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/definitions")
    @Operation(
            summary = "List report definitions",
            description = "Returns available reports, filters, columns and supported formats"
    )
    public List<ReportDefinitionResponse> definitions() {
        return reportService.listDefinitions();
    }

    @PostMapping("/{reportKey}/preview")
    @Operation(
            summary = "Preview report",
            description = "Runs selected report in JSON format with pagination"
    )
    public ReportPreviewResponse preview(
            @PathVariable String reportKey,
            @RequestBody(required = false) ReportRunRequest request
    ) {
        return reportService.preview(reportKey, request);
    }

    @PostMapping("/{reportKey}/export")
    @Operation(
            summary = "Export report",
            description = "Exports report in PDF, CSV or XLSX"
    )
    public ResponseEntity<byte[]> export(
            @PathVariable String reportKey,
            @RequestParam ReportFormat format,
            @RequestParam(defaultValue = "LANDSCAPE") PdfOrientation orientation,
            @RequestBody(required = false) ReportRunRequest request
    ) {
        byte[] bytes = reportService.export(reportKey, format, orientation, request);

        String extension = switch (format) {
            case PDF -> "pdf";
            case CSV -> "csv";
            case XLSX -> "xlsx";
        };

        String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = sanitizeFilename(reportKey) + "-" + timestamp + "." + extension;

        MediaType contentType = switch (format) {
            case PDF -> MediaType.APPLICATION_PDF;
            case CSV -> new MediaType("text", "csv");
            case XLSX -> new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }

    private String sanitizeFilename(String value) {
        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9-]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
