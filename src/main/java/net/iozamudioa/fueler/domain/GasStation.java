
package net.iozamudioa.fueler.domain;

public record GasStation(
        String id,
        String clientId,
        String reference,
        String name,
        boolean active) {
}
