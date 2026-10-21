package net.iozamudioa.fueler.application.ports.out;

import java.util.List;

import net.iozamudioa.fueler.domain.ExportedFile;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordExport;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;

public interface ExportFuelRecordOutputPort {

    FuelRecordExport generate(
            String clientId,
            List<FuelRecordDetail> records,
            FuelRecordExportSearch criteria);

    ExportedFile download(String clientId, String exportId);

    void deleteExpired();

}
