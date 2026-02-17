package com.lucast.vetcare.reports.export;

import com.lucast.vetcare.company.CompanyService;
import com.lucast.vetcare.company.dto.CompanyProfileResponse;
import com.lucast.vetcare.reports.PdfOrientation;
import com.lucast.vetcare.reports.ReportFormat;
import com.lucast.vetcare.reports.dto.ReportColumnResponse;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportExportService {

    private static final DateTimeFormatter FRIENDLY_DATE_TIME =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR"));

    private final CompanyService companyService;

    public ReportExportService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public byte[] export(
            ReportFormat format,
            PdfOrientation orientation,
            String reportTitle,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        return switch (format) {
            case CSV -> exportCsv(columns, rows);
            case XLSX -> exportXlsx(columns, rows);
            case PDF -> exportPdf(orientation, reportTitle, columns, rows);
        };
    }

    private byte[] exportCsv(List<ReportColumnResponse> columns, List<Map<String, Object>> rows) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escapeCsv(columns.get(i).label()));
        }
        sb.append('\n');

        for (Map<String, Object> row : rows) {
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) sb.append(',');
                String key = columns.get(i).key();
                sb.append(escapeCsv(formatValue(row.get(key))));
            }
            sb.append('\n');
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] exportXlsx(List<ReportColumnResponse> columns, List<Map<String, Object>> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("report");

            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns.get(i).label());
            }

            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                Map<String, Object> source = rows.get(rowIndex);

                for (int col = 0; col < columns.size(); col++) {
                    String key = columns.get(col).key();
                    Object value = source.get(key);
                    Cell cell = row.createCell(col);

                    if (value instanceof Number number) {
                        cell.setCellValue(number.doubleValue());
                    } else if (value instanceof Boolean bool) {
                        cell.setCellValue(bool);
                    } else {
                        cell.setCellValue(formatValue(value));
                    }
                }
            }

            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error generating XLSX report", e);
        }
    }

    private byte[] exportPdf(
            PdfOrientation orientation,
            String reportTitle,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        Rectangle pageSize = orientation == PdfOrientation.PORTRAIT ? PageSize.A4 : PageSize.A4.rotate();
        Document document = new Document(pageSize, 24, 24, 24, 24);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();

            addPdfHeader(document, reportTitle);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(columns.size());
            table.setWidthPercentage(100f);
            table.setSplitLate(false);

            float[] widths = buildColumnWidths(columns, rows);
            table.setWidths(widths);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10f);

            for (ReportColumnResponse column : columns) {
                PdfPCell cell = new PdfPCell(new Paragraph(column.label(), headerFont));
                cell.setBackgroundColor(new Color(235, 235, 235));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(6f);
                cell.setNoWrap(true);
                table.addCell(cell);
            }

            table.setHeaderRows(1);

            for (Map<String, Object> row : rows) {
                for (ReportColumnResponse column : columns) {
                    PdfPCell cell = new PdfPCell(new Paragraph(formatValue(row.get(column.key())), bodyFont));
                    cell.setPadding(5f);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("Error generating PDF report", e);
        }
    }

    private void addPdfHeader(Document document, String reportTitle) throws DocumentException, IOException {
        CompanyProfileResponse company = null;
        try {
            company = companyService.getCurrentProfile();
        } catch (Exception ignored) {
            // Report still exports even if company profile is not configured.
        }

        PdfPTable headerTable = new PdfPTable(new float[]{1.7f, 4.6f, 1.7f});
        headerTable.setWidthPercentage(100f);

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);

        Image logo = loadLogo();
        if (logo != null) {
            logo.scaleToFit(100f, 60f);
            leftCell.addElement(logo);
        }

        PdfPCell centerCell = new PdfPCell();
        centerCell.setBorder(Rectangle.NO_BORDER);

        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100f);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10f);

        infoTable.addCell(headerLineCell(reportTitle, titleFont, true));

        String companyLine = company == null ? "" : firstNonBlank(company.tradeName(), company.legalName());
        if (!companyLine.isBlank()) {
            infoTable.addCell(headerLineCell(companyLine, subtitleFont, true));
        }

        String cnpj = company == null ? "" : nonNull(company.cnpj());
        if (!cnpj.isBlank()) {
            infoTable.addCell(headerLineCell("CNPJ: " + cnpj, normalFont, true));
        }

        infoTable.addCell(headerLineCell(
                "Gerado em " + OffsetDateTime.now().format(FRIENDLY_DATE_TIME),
                normalFont,
                false
        ));

        centerCell.addElement(infoTable);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);

        headerTable.addCell(leftCell);
        headerTable.addCell(centerCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
    }

    private PdfPCell headerLineCell(String text, Font font, boolean withBottomLine) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(withBottomLine ? Rectangle.BOTTOM : Rectangle.NO_BORDER);
        cell.setBorderColor(new Color(170, 170, 170));
        cell.setBorderWidth(0.6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(4f);
        cell.setPaddingTop(3f);
        cell.addElement(p);
        return cell;
    }

    private float[] buildColumnWidths(List<ReportColumnResponse> columns, List<Map<String, Object>> rows) {
        float[] weights = new float[columns.size()];

        int sampleSize = Math.min(rows.size(), 300);

        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            ReportColumnResponse column = columns.get(colIndex);

            int headerLen = safeLength(column.label());
            int maxLen = headerLen;

            for (int rowIndex = 0; rowIndex < sampleSize; rowIndex++) {
                Map<String, Object> row = rows.get(rowIndex);
                int valueLen = safeLength(formatValue(row.get(column.key())));
                if (valueLen > maxLen) {
                    maxLen = valueLen;
                }
            }

            float baseWeight = Math.max(maxLen + 2f, headerLen + 2f);
            String key = column.key().toLowerCase(Locale.ROOT);

            if (key.equals("id") || key.endsWith("id")) {
                baseWeight = clamp(baseWeight, 7f, 10f);
            }

            if (key.equals("active") || key.equals("status") || key.equals("role")) {
                baseWeight = Math.max(baseWeight, 12f);
            }

            if (key.contains("name") || key.contains("email") || key.contains("sku") || key.contains("document")) {
                baseWeight *= 1.2f;
            }

            if (key.contains("category") || key.contains("unit") || key.contains("price") || key.contains("stock")) {
                baseWeight = Math.max(baseWeight, 11f);
            }

            weights[colIndex] = clamp(baseWeight, 7f, 34f);
        }

        return weights;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private int safeLength(String value) {
        return value == null ? 0 : value.length();
    }

    private Image loadLogo() throws IOException {
        try (InputStream in = ReportExportService.class.getResourceAsStream("/images/report-logo.png")) {
            if (in == null) {
                return null;
            }
            return Image.getInstance(in.readAllBytes());
        } catch (Exception e) {
            return null;
        }
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof OffsetDateTime odt) {
            return odt.format(FRIENDLY_DATE_TIME);
        }
        if (value instanceof BigDecimal bd) {
            return bd.stripTrailingZeros().toPlainString();
        }
        if (value instanceof Boolean bool) {
            return bool ? "Sim" : "Nao";
        }
        return String.valueOf(value);
    }

    private String escapeCsv(String value) {
        String normalized = value == null ? "" : value;
        boolean needQuotes = normalized.contains(",") || normalized.contains("\"") || normalized.contains("\n");
        if (!needQuotes) {
            return normalized;
        }
        return '"' + normalized.replace("\"", "\"\"") + '"';
    }

    private String nonNull(String value) {
        return value == null ? "" : value;
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second == null ? "" : second;
    }
}
