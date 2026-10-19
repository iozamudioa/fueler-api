package net.iozamudioa.fueler.application.usecase;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.VehicleInputPort;
import net.iozamudioa.fueler.application.ports.out.VehicleOutputPort;
import net.iozamudioa.fueler.domain.Vehicle;
import net.iozamudioa.fueler.domain.exception.ResourceAlreadyExistsException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;

@Service
public class VehicleUseCase implements VehicleInputPort {

    private final VehicleOutputPort vehicleOutputPort;

    public VehicleUseCase(VehicleOutputPort vehicleOutputPort) {
        this.vehicleOutputPort = vehicleOutputPort;
    }

    @Override
    public Vehicle create(String clientId, String reference, String area) {

        String normalizedReference = reference.trim().toUpperCase(Locale.ROOT);

        if (vehicleOutputPort.existsByClientIdAndReference(
                clientId, normalizedReference)) {

            throw new ResourceAlreadyExistsException(
                    "Vehicle", normalizedReference);
        }

        Vehicle vehicle = new Vehicle(
                clientId + ":" + normalizedReference,
                clientId,
                normalizedReference,
                area == null ? null : area.trim(),
                true);

        return vehicleOutputPort.save(vehicle);
    }

    @Override
    public List<Vehicle> findAllByClientId(String clientId) {
        return vehicleOutputPort.findAllByClientId(clientId);
    }

    @Override
    public Vehicle findByReferenceAndClientId(
            String reference,
            String clientId) {

        String normalizedReference = reference.trim().toUpperCase(Locale.ROOT);

        return vehicleOutputPort
                .findByReferenceAndClientId(normalizedReference, clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle", normalizedReference));
    }
}