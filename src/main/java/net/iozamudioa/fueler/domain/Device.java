
package net.iozamudioa.fueler.domain;

import java.time.Instant;

public record Device(
        String id,
        String installationId,
        String label,
        String clientId,
        Status status,
        String tokenHash,
        Instant createdAt,
        Instant approvedAt) {

    public enum Status {
        PENDING,
        ACTIVE,
        REVOKED
    }
}
