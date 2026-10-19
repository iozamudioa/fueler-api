
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

public record ApprovedDeviceResponse(
        String deviceId,
        String clientId,
        String token) {
}
