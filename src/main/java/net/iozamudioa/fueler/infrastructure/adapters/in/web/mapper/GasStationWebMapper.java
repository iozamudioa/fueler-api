
package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateGasStation;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.GasStationResponse;

@Component
public class GasStationWebMapper {

    public String toName(CreateGasStation request) {
        return request.name();
    }

    public GasStationResponse toResponse(GasStation gasStation) {
        return new GasStationResponse(
                gasStation.id(),
                gasStation.reference(),
                gasStation.name(),
                gasStation.active());
    }
}
