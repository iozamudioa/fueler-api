package net.iozamudioa.fueler.application.usecase;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.DownloadFuelRecordInputPort;
import net.iozamudioa.fueler.application.ports.out.ExportFuelRecordOutputPort;
import net.iozamudioa.fueler.domain.ExportedFile;

@Service
public class DownloadFuelRecordUseCase
        implements DownloadFuelRecordInputPort {

    private final ExportFuelRecordOutputPort exportOutputPort;

    public DownloadFuelRecordUseCase(
            ExportFuelRecordOutputPort exportOutputPort) {
        this.exportOutputPort = exportOutputPort;
    }

    @Override
    public ExportedFile download(String clientId, String exportId) {

        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "Client ID is required");
        }

        if (exportId == null || exportId.isBlank()) {
            throw new IllegalArgumentException(
                    "Export ID is required");
        }

        return exportOutputPort.download(clientId, exportId);
    }
}