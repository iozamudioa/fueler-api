package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

import java.time.Instant;

public record ExportResponse(
        String id,
        String fileName,
        String downloadUrl,
        Instant expiresAt) {
}
