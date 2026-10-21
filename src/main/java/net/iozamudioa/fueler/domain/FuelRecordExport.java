package net.iozamudioa.fueler.domain;

import java.time.Instant;

public record FuelRecordExport(
                String id,
                String clientId,
                String fileName,
                Instant createdAt,
                Instant expiresAt) {
}