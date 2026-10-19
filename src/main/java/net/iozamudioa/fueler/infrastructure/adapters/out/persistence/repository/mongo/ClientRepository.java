
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.ClientDocument;

public interface ClientRepository
        extends MongoRepository<ClientDocument, String> {
}
