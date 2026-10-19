
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.GasStationOutputPort;
import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.GasStationRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.GasStationMongoMapper;

@Component
public class GasStationPersistenceAdapter
        implements GasStationOutputPort {

    private final GasStationRepository repository;
    private final GasStationMongoMapper mapper;

    public GasStationPersistenceAdapter(
            GasStationRepository repository,
            GasStationMongoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public GasStation save(GasStation gasStation) {
        return mapper.toDomain(
                repository.save(
                        mapper.toDocument(gasStation)));
    }

    @Override
    public Optional<GasStation> findByClientIdAndReference(
            String clientId,
            String reference) {

        return repository
                .findByClientIdAndReference(clientId, reference)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<GasStation> findByIdAndClientId(
            String id,
            String clientId) {

        return repository
                .findByIdAndClientId(id, clientId)
                .map(mapper::toDomain);
    }

    @Override
    public List<GasStation> findAllByClientId(String clientId) {
        return repository
                .findAllByClientIdOrderByNameAsc(clientId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
