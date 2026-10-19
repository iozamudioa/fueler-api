
package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.ApprovedDevice;
import net.iozamudioa.fueler.domain.AuthorizedDevice;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateDevice;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ApprovedDeviceResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.AuthorizedDeviceResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.DeviceResponse;

@Component
public class DeviceWebMapper {

    public String toInstallationId(CreateDevice request) {
        return request.installationId().trim();
    }

    public String toLabel(CreateDevice request) {
        return request.label() == null
                ? null
                : request.label().trim();
    }

    public DeviceResponse toResponse(Device device) {
        return new DeviceResponse(
                device.id(),
                device.installationId(),
                device.label(),
                device.clientId(),
                device.status(),
                device.createdAt(),
                device.approvedAt());
    }

    public ApprovedDeviceResponse toApprovedResponse(
            ApprovedDevice approved) {

        return new ApprovedDeviceResponse(
                approved.deviceId(),
                approved.clientId(),
                approved.otp(),
                approved.expiresAt());
    }

    public AuthorizedDeviceResponse toAuthorizedResponse(
            AuthorizedDevice authorized) {

        return new AuthorizedDeviceResponse(
                authorized.deviceId(),
                authorized.clientId(),
                authorized.token());
    }

}
