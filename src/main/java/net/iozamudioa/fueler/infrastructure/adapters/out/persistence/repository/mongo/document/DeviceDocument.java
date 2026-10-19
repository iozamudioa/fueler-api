
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import net.iozamudioa.fueler.domain.Device.Status;

@Document(collection = "devices")
public record DeviceDocument(

        @Id String id,

        @Indexed(unique = true) String installationId,

        String label,

        String clientId,

        Status status,

        @Indexed(unique = true, sparse = true) String tokenHash,

        Instant createdAt,

        Instant approvedAt

) {
}
