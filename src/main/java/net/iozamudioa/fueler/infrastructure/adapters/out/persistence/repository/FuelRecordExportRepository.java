package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordExportDocument;

public interface FuelRecordExportRepository
        extends MongoRepository<FuelRecordExportDocument, String> {

    Optional<FuelRecordExportDocument> findByIdAndClientId(
            String id,
            String clientId);

    List<FuelRecordExportDocument> findByExpiresAtBefore(
            Instant now);
}