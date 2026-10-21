package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.FuelRecordExport;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.ExportRequest;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ExportResponse;

@Component
public class FuelRecordExportWebMapper {

    private static final String REPORT_PATH = "/api/v1/fuel/report/";

    public FuelRecordExportSearch toSearch(ExportRequest request) {

        return new FuelRecordExportSearch(
                request.from(),
                request.to(),
                request.vehicleReference(),
                request.operatorReference(),
                request.area());
    }

    public ExportResponse toResponse(FuelRecordExport export) {

        return new ExportResponse(
                export.id(),
                export.fileName(),
                REPORT_PATH + export.id(),
                export.expiresAt());
    }
}