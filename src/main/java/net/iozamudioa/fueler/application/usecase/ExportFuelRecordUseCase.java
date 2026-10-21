
package net.iozamudioa.fueler.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.ExportFuelRecordInputPort;
import net.iozamudioa.fueler.application.ports.out.ExportFuelRecordOutputPort;
import net.iozamudioa.fueler.application.ports.out.FuelRecordOutputPort;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordExport;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;

@Service
public class ExportFuelRecordUseCase
        implements ExportFuelRecordInputPort {

    private final FuelRecordOutputPort fuelRecordOutputPort;
    private final ExportFuelRecordOutputPort exportOutputPort;

    public ExportFuelRecordUseCase(
            FuelRecordOutputPort fuelRecordOutputPort,
            ExportFuelRecordOutputPort exportOutputPort) {

        this.fuelRecordOutputPort = fuelRecordOutputPort;
        this.exportOutputPort = exportOutputPort;
    }

    @Override
    public FuelRecordExport generate(
            String clientId,
            FuelRecordExportSearch criteria) {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "Client ID is required");
        }

        if (criteria == null
                || criteria.from() == null
                || criteria.to() == null) {
            throw new IllegalArgumentException(
                    "Date range is required");
        }

        if (criteria.from().isAfter(criteria.to())) {
            throw new IllegalArgumentException(
                    "Invalid date range");
        }

        List<FuelRecordDetail> records = fuelRecordOutputPort.export(clientId, criteria);

        return exportOutputPort.generate(
                clientId,
                records,
                criteria);
    }
}
