package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.VehicleDocument;

public interface MongoVehicleRepository
        extends MongoRepository<VehicleDocument, String> {

    List<VehicleDocument> findAllByClientId(String clientId);

    boolean existsByClientIdAndReference(String clientId, String reference);

    Optional<VehicleDocument> findByReferenceAndClientId(
            String reference,
            String clientId);

}