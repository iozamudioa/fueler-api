
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.GasStationDocument;

@Component
public class GasStationMongoMapper {

    public GasStationDocument toDocument(GasStation gasStation) {
        return new GasStationDocument(
                gasStation.id(),
                gasStation.clientId(),
                gasStation.reference(),
                gasStation.name(),
                gasStation.active());
    }

    public GasStation toDomain(GasStationDocument document) {
        return new GasStation(
                document.id(),
                document.clientId(),
                document.reference(),
                document.name(),
                document.active());
    }
}
