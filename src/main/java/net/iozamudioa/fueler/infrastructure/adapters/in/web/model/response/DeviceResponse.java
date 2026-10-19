package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

import java.time.Instant;

import net.iozamudioa.fueler.domain.Device;

public record DeviceResponse(
                String id,
                String installationId,
                String label,
                String clientId,
                Device.Status status,
                Instant createdAt,
                Instant approvedAt) {
}
