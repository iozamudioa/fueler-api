package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordDocument;

public interface FuelRecordRepository
        extends MongoRepository<FuelRecordDocument, String> {

    Optional<FuelRecordDocument> findByClientIdAndRequestId(
            String clientId,
            String requestId);

    List<FuelRecordDocument> findAllByClientId(String clientId);

    List<FuelRecordDocument> findAllByClientIdAndDateBetween(
            String clientId,
            LocalDate startDate,
            LocalDate endDate);

    Optional<FuelRecordDocument> findByIdAndClientId(
            String id,
            String clientId);

    boolean existsByIdAndClientId(
            String id,
            String clientId);
}