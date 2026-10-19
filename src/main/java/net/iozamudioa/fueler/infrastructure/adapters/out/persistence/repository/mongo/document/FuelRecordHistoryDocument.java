package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fuel_record_history")
@CompoundIndexes({
        @CompoundIndex(name = "client_record_version_unique", def = "{'clientId': 1, 'fuelRecordId': 1, 'version': 1}", unique = true),
        @CompoundIndex(name = "client_record_modified_idx", def = "{'clientId': 1, 'fuelRecordId': 1, 'modifiedAt': -1}")
})
public record FuelRecordHistoryDocument(

        @Id String id,
        String clientId,
        String fuelRecordId,
        Long version,
        String modifiedBy,
        Instant modifiedAt,
        String reason,
        Map<String, FieldChange> changes) {

    public record FieldChange(
            Object before,
            Object after) {
    }
}