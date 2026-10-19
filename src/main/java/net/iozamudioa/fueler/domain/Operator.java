package net.iozamudioa.fueler.domain;

public record Operator(
        String id,
        String clientId,
        String reference,
        String name,
        boolean active) {
}