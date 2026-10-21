package net.iozamudioa.fueler.application.ports.in;

import net.iozamudioa.fueler.domain.ExportedFile;

public interface DownloadFuelRecordInputPort {

    ExportedFile download(String clientId, String exportId);

}
