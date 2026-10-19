
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.DeviceDocument;

public interface DeviceRepository
        extends MongoRepository<DeviceDocument, String> {

    Optional<DeviceDocument> findByInstallationId(
            String installationId);

    Optional<DeviceDocument> findByTokenHash(
            String tokenHash);
}
