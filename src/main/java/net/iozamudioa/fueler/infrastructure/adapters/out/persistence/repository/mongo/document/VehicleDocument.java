package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
@CompoundIndex(name = "uk_vehicle_client_eco", def = "{'clientId': 1, 'reference': 1}", unique = true)
public record VehicleDocument(

                @Id String id,

                String clientId,

                String reference,

                String area,

                boolean active

) {
}