
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.GasStationDocument;

public interface GasStationRepository
        extends MongoRepository<GasStationDocument, String> {

    Optional<GasStationDocument> findByClientIdAndReference(
            String clientId,
            String reference);

    Optional<GasStationDocument> findByIdAndClientId(
            String id,
            String clientId);

    List<GasStationDocument> findAllByClientIdOrderByNameAsc(
            String clientId);
}
