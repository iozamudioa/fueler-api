package net.iozamudioa.fueler.domain;

import java.time.Instant;

public record ApprovedDevice(
                String deviceId,
                String clientId,
                String otp,
                Instant expiresAt) {
}