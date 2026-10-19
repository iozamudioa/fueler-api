package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.OperatorOutputPort;
import net.iozamudioa.fueler.domain.Operator;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.MongoOperatorRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.OperatorMongoMapper;

@Component
public class OperatorPersistenceAdapter implements OperatorOutputPort {

    private final MongoOperatorRepository mongoOperatorRepository;
    private final OperatorMongoMapper mapper;

    public OperatorPersistenceAdapter(MongoOperatorRepository mongoOperatorRepository, OperatorMongoMapper mapper) {
        this.mongoOperatorRepository = mongoOperatorRepository;
        this.mapper = mapper;
    }

    @Override
    public Operator save(Operator operator) {
        return mapper.toDomain(
                mongoOperatorRepository.save(
                        mapper.toDocument(operator)));
    }

    @Override
    public List<Operator> findAllByClientId(String clientId) {
        return mongoOperatorRepository.findAllByClientId(clientId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Operator> findByReferenceAndClientId(
            String reference,
            String clientId) {

        return mongoOperatorRepository
                .findByReferenceAndClientId(reference, clientId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByClientIdAndReference(
            String clientId,
            String reference) {

        return mongoOperatorRepository
                .existsByClientIdAndReference(clientId, reference);
    }
}