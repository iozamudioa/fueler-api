package net.iozamudioa.fueler.domain;

public record ApprovedDevice(
        String deviceId,
        String clientId,
        String token) {
}