package net.iozamudioa.fueler.domain;

public record Vehicle(
        String id,
        String clientId,
        String reference,
        String area,
        boolean active) {
}