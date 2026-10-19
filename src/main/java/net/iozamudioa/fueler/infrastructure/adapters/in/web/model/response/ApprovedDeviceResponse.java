
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

import java.time.Instant;

public record ApprovedDeviceResponse(
                String deviceId,
                String clientId,
                String otp,
                Instant expiresAt) {
}
