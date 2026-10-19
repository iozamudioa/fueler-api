
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordHistoryDocument;

public interface FuelRecordHistoryRepository
        extends MongoRepository<FuelRecordHistoryDocument, String> {

    List<FuelRecordHistoryDocument> findAllByClientIdAndFuelRecordIdOrderByVersionDesc(
            String clientId,
            String fuelRecordId);

    boolean existsByClientIdAndFuelRecordIdAndVersion(
            String clientId,
            String fuelRecordId,
            Long version);
}
