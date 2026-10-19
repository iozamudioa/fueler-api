
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.DeviceOutputPort;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.DeviceRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.DeviceMongoMapper;

@Component
public class DevicePersistenceAdapter implements DeviceOutputPort {

    private final DeviceRepository repository;
    private final DeviceMongoMapper mapper;

    public DevicePersistenceAdapter(
            DeviceRepository repository,
            DeviceMongoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Device save(Device device) {
        return mapper.toDomain(
                repository.save(mapper.toDocument(device)));
    }

    @Override
    public Optional<Device> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Device> findByInstallationId(
            String installationId) {

        return repository.findByInstallationId(installationId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Device> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public List<Device> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
