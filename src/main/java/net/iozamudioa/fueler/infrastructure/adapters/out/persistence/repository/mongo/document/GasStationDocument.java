
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gas_stations")
@CompoundIndex(name = "uk_gas_station_client_reference", def = "{'clientId': 1, 'reference': 1}", unique = true)
public record GasStationDocument(
        @Id String id,
        String clientId,
        String reference,
        String name,
        boolean active) {
}
