package net.iozamudioa.fueler.infrastructure.adapters.out.export;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.ExportFuelRecordOutputPort;

@Component
public class FuelRecordExportCleanup {

    private final ExportFuelRecordOutputPort exportOutputPort;

    public FuelRecordExportCleanup(
            ExportFuelRecordOutputPort exportOutputPort) {
        this.exportOutputPort = exportOutputPort;
    }

    @Scheduled(fixedDelayString = "${fueler.export.cleanup-ms:3600000}")
    public void cleanup() {
        exportOutputPort.deleteExpired();
    }
}