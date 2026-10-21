package net.iozamudioa.fueler.application.ports.in;

import net.iozamudioa.fueler.domain.FuelRecordExport;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;

public interface ExportFuelRecordInputPort {

    FuelRecordExport generate(
            String clientId,
            FuelRecordExportSearch criteria);
}