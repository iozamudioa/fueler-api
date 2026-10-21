package net.iozamudioa.fueler.infrastructure.adapters.out.export;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import net.iozamudioa.fueler.application.ports.out.ExportFuelRecordOutputPort;
import net.iozamudioa.fueler.domain.ExportedFile;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordExport;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.FuelRecordExportRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordExportDocument;

@Component
public class FuelRecordExcelAdapter
        implements ExportFuelRecordOutputPort {

    private final FuelRecordExportRepository repository;
    private final Path exportPath;
    private final Duration expiration;

    public FuelRecordExcelAdapter(
            FuelRecordExportRepository repository,
            @Value("${fueler.export.path:/app/exports}") String path,
            @Value("${fueler.export.expiration-hours:24}") long hours) {

        this.repository = repository;
        this.exportPath = Path.of(path).toAbsolutePath().normalize();
        this.expiration = Duration.ofHours(hours);

        if (hours <= 0) {
            throw new IllegalArgumentException(
                    "Export expiration must be positive");
        }
    }

    @Override
    public FuelRecordExport generate(
            String clientId,
            List<FuelRecordDetail> records,
            FuelRecordExportSearch criteria) {

        String id = UUID.randomUUID().toString();
        String fileName = "fueler-" + criteria.from()
                + "-" + criteria.to() + ".xlsx";

        Path destination = filePath(id);
        Path temporary = exportPath.resolve(id + ".tmp");

        Instant createdAt = Instant.now();
        Instant expiresAt = createdAt.plus(expiration);

        try {
            Files.createDirectories(exportPath);

            try (Workbook workbook = new XSSFWorkbook();
                    OutputStream output = Files.newOutputStream(temporary)) {

                createWorkbook(workbook, records, criteria);
                workbook.write(output);
            }

            Files.move(
                    temporary,
                    destination,
                    StandardCopyOption.ATOMIC_MOVE);

            repository.save(new FuelRecordExportDocument(
                    id,
                    clientId,
                    fileName,
                    createdAt,
                    expiresAt));

            return new FuelRecordExport(
                    id,
                    clientId,
                    fileName,
                    createdAt,
                    expiresAt);

        } catch (Exception ex) {
            try {
                Files.deleteIfExists(temporary);
                Files.deleteIfExists(destination);
            } catch (IOException cleanupError) {
                ex.addSuppressed(cleanupError);
            }

            throw new IllegalStateException(
                    "Could not generate fuel report", ex);
        }
    }

    @Override
    public ExportedFile download(String clientId, String exportId) {

        FuelRecordExportDocument document = repository
                .findByIdAndClientId(exportId, clientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Export not found"));

        if (!document.expiresAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Export not found");
        }

        Path path = filePath(document.id());

        try {
            if (!Files.isRegularFile(path)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Export not found");
            }

            return new ExportedFile(
                    document.fileName(),
                    new FileSystemResource(path),
                    Files.size(path));

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not access export file", ex);
        }
    }

    @Override
    public void deleteExpired() {

        var expired = repository.findByExpiresAtBefore(
                Instant.now());

        for (var document : expired) {
            try {
                Files.deleteIfExists(filePath(document.id()));
                repository.deleteById(document.id());
            } catch (IOException ex) {
                // Conservar metadatos para reintentar limpieza.
            }
        }
    }

    private Path filePath(String id) {
        UUID.fromString(id);
        return exportPath.resolve(id + ".xlsx");
    }

    private void createWorkbook(
            Workbook workbook,
            List<FuelRecordDetail> records,
            FuelRecordExportSearch criteria) {

        Sheet sheet = workbook.createSheet("Cargas");

        String[] headers = {
                "fecha",
                "unidad",
                "area",
                "operador",
                "nombre",
                "nivel",
                "kmAcumulado",
                "kmRecorrido",
                "folio",
                "nota",
                "tipo",
                "precio",
                "litros",
                "total",
                "rendimiento",
                "gasolinera"
        };

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(
                IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(
                workbook.createDataFormat().getFormat("#,##0.00"));

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        BigDecimal litersTotal = BigDecimal.ZERO;
        BigDecimal amountTotal = BigDecimal.ZERO;

        int index = 1;

        for (FuelRecordDetail detail : records) {
            var record = detail.fuelRecord();
            Row row = sheet.createRow(index++);

            int col = 0;

            text(row, col++, record.date());
            text(row, col++, record.vehicleReference());
            text(row, col++, record.area());
            text(row, col++, record.operatorReference());
            text(row, col++, record.operatorName());
            text(row, col++, record.tankLevel());

            number(row, col++, record.odometerKm(), numberStyle);
            number(row, col++, record.distanceKm(), numberStyle);

            text(row, col++, record.ticketNumber());
            text(row, col++, record.note());
            text(row, col++, record.fuelType());

            number(row, col++, record.pricePerLiter(), numberStyle);
            number(row, col++, record.liters(), numberStyle);
            number(row, col++, record.totalAmount(), numberStyle);
            number(row, col++, record.efficiencyKmPerLiter(), numberStyle);

            text(row, col++, record.gasStationReference());

            litersTotal = litersTotal.add(
                    valueOrZero(record.liters()));

            amountTotal = amountTotal.add(
                    valueOrZero(record.totalAmount()));
        }

        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(
                new org.apache.poi.ss.util.CellRangeAddress(
                        0, Math.max(1, records.size()),
                        0, headers.length - 1));

        Sheet summary = workbook.createSheet("Resumen");
        summary.setColumnWidth(0, 30 * 256);
        summary.setColumnWidth(1, 28 * 256);

        summaryRow(summary, 0, "FUELER - REPORTE", "");
        summaryRow(summary, 2, "Desde", criteria.from());
        summaryRow(summary, 3, "Hasta", criteria.to());
        summaryRow(summary, 4, "Unidad", criteria.vehicleReference());
        summaryRow(summary, 5, "Operador", criteria.operatorReference());
        summaryRow(summary, 6, "Área", criteria.area());
        summaryRow(summary, 8, "Total cargas", records.size());

        Row litersRow = summary.createRow(9);
        litersRow.createCell(0).setCellValue("Total litros");
        number(litersRow, 1, litersTotal, numberStyle);

        Row amountRow = summary.createRow(10);
        amountRow.createCell(0).setCellValue("Importe total");
        number(amountRow, 1, amountTotal, numberStyle);
    }

    private void text(Row row, int column, Object value) {
        String text = value == null ? "" : value.toString();
        // Excel admite hasta 32767 caracteres por celda.
        if (text.length() > 32767) {
            text = text.substring(0, 32767);
        }
        row.createCell(column).setCellValue(text);
    }

    private void number(
            Row row, int column,
            BigDecimal value, CellStyle style) {

        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void summaryRow(
            Sheet sheet, int index, String label, Object value) {

        Row row = sheet.createRow(index);
        row.createCell(0).setCellValue(label);

        if (value instanceof Number number) {
            row.createCell(1).setCellValue(number.doubleValue());
        } else {
            text(row, 1, value);
        }
    }
}