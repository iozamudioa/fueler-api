
package net.iozamudioa.fueler.domain;

import java.time.Instant;

public record DeviceActivation(
        String deviceId,
        String otpHash,
        Instant expiresAt,
        int attempts,
        Instant consumedAt) {

    public boolean isExpired(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public boolean hasAttemptsRemaining(int maxAttempts) {
        return attempts < maxAttempts;
    }

    public boolean canBeRedeemed(
            Instant now,
            int maxAttempts) {

        return !isExpired(now)
                && !isConsumed()
                && hasAttemptsRemaining(maxAttempts);
    }
}
