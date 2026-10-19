package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "device_activations")
public record DeviceActivationDocument(

        @Id String deviceId,
        String otpHash,
        @Indexed Instant expiresAt,
        int attempts,
        Instant consumedAt) {
}
