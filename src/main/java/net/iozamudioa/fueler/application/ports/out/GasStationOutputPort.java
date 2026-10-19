package net.iozamudioa.fueler.application.ports.out;

import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.GasStation;

public interface GasStationOutputPort {

    GasStation save(GasStation gasStation);

    Optional<GasStation> findByClientIdAndReference(
            String clientId,
            String reference);

    Optional<GasStation> findByIdAndClientId(
            String id,
            String clientId);

    List<GasStation> findAllByClientId(String clientId);

}
