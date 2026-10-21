package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fuel_record_exports")
public record FuelRecordExportDocument(
        @Id String id,
        String clientId,
        String fileName,
        Instant createdAt,
        Instant expiresAt) {
}