package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.VehicleOutputPort;
import net.iozamudioa.fueler.domain.Vehicle;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.MongoVehicleRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.VehicleMongoMapper;

@Component
public class VehiclePersistenceAdapter implements VehicleOutputPort {

    private final MongoVehicleRepository mongoVehicleRepository;
    private final VehicleMongoMapper mapper;

    public VehiclePersistenceAdapter(MongoVehicleRepository mongoVehicleRepository, VehicleMongoMapper mapper) {
        this.mongoVehicleRepository = mongoVehicleRepository;
        this.mapper = mapper;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return mapper.toDomain(
                mongoVehicleRepository.save(
                        mapper.toDocument(vehicle)));
    }

    @Override
    public List<Vehicle> findAllByClientId(String clientId) {
        return mongoVehicleRepository.findAllByClientId(clientId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Vehicle> findByReferenceAndClientId(
            String reference,
            String clientId) {

        return mongoVehicleRepository
                .findByReferenceAndClientId(reference, clientId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByClientIdAndReference(
            String clientId,
            String reference) {

        return mongoVehicleRepository
                .existsByClientIdAndReference(clientId, reference);
    }
}