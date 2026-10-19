
package net.iozamudioa.fueler.application.ports.in;

import java.util.List;

import net.iozamudioa.fueler.domain.GasStation;

public interface GasStationInputPort {

    GasStation register(
            String clientId,
            String name);

    GasStation findByReference(
            String clientId,
            String reference);

    List<GasStation> findAll(String clientId);

    GasStation deactivate(
            String clientId,
            String reference);
}
