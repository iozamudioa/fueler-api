package net.iozamudioa.fueler.application.ports.out;

import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.Vehicle;

public interface VehicleOutputPort {

    Vehicle save(Vehicle vehicle);

    List<Vehicle> findAllByClientId(String clientId);

    Optional<Vehicle> findByReferenceAndClientId(
            String reference,
            String clientId);

    boolean existsByClientIdAndReference(
            String clientId,
            String reference);

}