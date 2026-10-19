package net.iozamudioa.fueler.application.ports.out;

import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.Device;

public interface DeviceOutputPort {

    Device save(Device device);

    Optional<Device> findById(String id);

    Optional<Device> findByInstallationId(String installationId);

    Optional<Device> findByTokenHash(String tokenHash);

    List<Device> findAll();

}
