
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.ClientOutputPort;
import net.iozamudioa.fueler.domain.Client;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.ClientRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.ClientMongoMapper;

@Component
public class ClientPersistenceAdapter implements ClientOutputPort {

    private final ClientRepository repository;
    private final ClientMongoMapper mapper;

    public ClientPersistenceAdapter(
            ClientRepository repository,
            ClientMongoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Client save(Client client) {
        return mapper.toDomain(
                repository.save(mapper.toDocument(client)));
    }

    @Override
    public List<Client> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
