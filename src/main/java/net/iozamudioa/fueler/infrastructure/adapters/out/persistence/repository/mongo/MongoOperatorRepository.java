package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.OperatorDocument;

public interface MongoOperatorRepository extends MongoRepository<OperatorDocument, String> {

    List<OperatorDocument> findAllByClientId(String clientId);

    Optional<OperatorDocument> findByReferenceAndClientId(
            String reference,
            String clientId);

    boolean existsByClientIdAndReference(
            String clientId,
            String reference);
}