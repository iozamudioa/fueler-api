
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

public record AuthorizedDeviceResponse(
        String deviceId,
        String clientId,
        String token) {
}
