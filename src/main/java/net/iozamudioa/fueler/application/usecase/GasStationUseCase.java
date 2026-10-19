
package net.iozamudioa.fueler.application.usecase;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.GasStationInputPort;
import net.iozamudioa.fueler.application.ports.out.GasStationOutputPort;
import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;
import net.iozamudioa.fueler.domain.util.ReferenceNormalizer;

@Service
public class GasStationUseCase implements GasStationInputPort {

    private final GasStationOutputPort gasStationOutputPort;

    public GasStationUseCase(
            GasStationOutputPort gasStationOutputPort) {

        this.gasStationOutputPort = gasStationOutputPort;
    }

    @Override
    public GasStation register(String clientId, String name) {

        validateClientId(clientId);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Gas station name is required");
        }

        String normalizedName = name.trim().replaceAll("\\s+", " ");
        String reference = ReferenceNormalizer.normalize(normalizedName);

        return gasStationOutputPort
                .findByClientIdAndReference(clientId, reference)
                .orElseGet(() -> create(
                        clientId,
                        reference,
                        normalizedName));
    }

    @Override
    public GasStation findByReference(
            String clientId,
            String reference) {

        validateClientId(clientId);

        String normalizedReference = ReferenceNormalizer.normalize(reference);

        return gasStationOutputPort
                .findByClientIdAndReference(
                        clientId,
                        normalizedReference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GasStation",
                        normalizedReference));
    }

    @Override
    public List<GasStation> findAll(String clientId) {

        validateClientId(clientId);

        return gasStationOutputPort.findAllByClientId(clientId);
    }

    @Override
    public GasStation deactivate(
            String clientId,
            String reference) {

        GasStation current = findByReference(
                clientId,
                reference);

        if (!current.active()) {
            return current;
        }

        GasStation updated = new GasStation(
                current.id(),
                current.clientId(),
                current.reference(),
                current.name(),
                false);

        return gasStationOutputPort.save(updated);
    }

    private GasStation create(
            String clientId,
            String reference,
            String name) {

        GasStation gasStation = new GasStation(
                clientId + ":" + reference,
                clientId,
                reference,
                name,
                true);

        try {
            return gasStationOutputPort.save(gasStation);
        } catch (DuplicateKeyException ex) {
            return gasStationOutputPort
                    .findByClientIdAndReference(clientId, reference)
                    .orElseThrow(() -> ex);
        }
    }

    private void validateClientId(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException(
                    "Client ID is required");
        }
    }
}
