
package net.iozamudioa.fueler.infrastructure.adapters.in.web;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.DownloadFuelRecordInputPort;
import net.iozamudioa.fueler.application.ports.in.ExportFuelRecordInputPort;
import net.iozamudioa.fueler.domain.ExportedFile;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.FuelRecordExportWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.ExportRequest;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ExportResponse;

@RestController
@RequestMapping("/api/v1/fuel/report")
public class FuelRecordExportController {

    private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ExportFuelRecordInputPort exportFuelRecordInputPort;
    private final DownloadFuelRecordInputPort downloadFuelRecordInputPort;
    private final FuelRecordExportWebMapper mapper;

    public FuelRecordExportController(
            ExportFuelRecordInputPort exportFuelRecordInputPort,
            DownloadFuelRecordInputPort downloadFuelRecordInputPort,
            FuelRecordExportWebMapper mapper) {

        this.exportFuelRecordInputPort = exportFuelRecordInputPort;
        this.downloadFuelRecordInputPort = downloadFuelRecordInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ExportResponse> generate(
            @AuthenticationPrincipal DevicePrincipal principal,
            @RequestBody ExportRequest request) {

        return ResponseEntity.ok(
                mapper.toResponse(exportFuelRecordInputPort.generate(
                        principal.clientId(),
                        mapper.toSearch(request))));
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> download(
            @AuthenticationPrincipal DevicePrincipal principal,
            @PathVariable String id) {

        ExportedFile file = downloadFuelRecordInputPort.download(
                principal.clientId(),
                id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.fileName())
                                .build()
                                .toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(EXCEL_MEDIA_TYPE)
                .contentLength(file.content().length)
                .body(file.content());
    }
}
