
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Client;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.ClientDocument;

@Component
public class ClientMongoMapper {

    public ClientDocument toDocument(Client client) {
        return new ClientDocument(
                client.id(),
                client.name(),
                client.active());
    }

    public Client toDomain(ClientDocument document) {
        return new Client(
                document.id(),
                document.name(),
                document.active());
    }
}
