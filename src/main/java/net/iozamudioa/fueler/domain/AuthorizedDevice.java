package net.iozamudioa.fueler.domain;

public record AuthorizedDevice(
        String deviceId,
        String clientId,
        String token) {
}