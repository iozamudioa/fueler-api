package net.iozamudioa.fueler.application.ports.in;

import java.util.List;

import net.iozamudioa.fueler.domain.ApprovedDevice;
import net.iozamudioa.fueler.domain.AuthorizedDevice;
import net.iozamudioa.fueler.domain.Device;

public interface DeviceInputPort {

    Device request(String installationId, String label);

    ApprovedDevice approve(String deviceId, String clientId);

    Device revoke(String deviceId);

    List<Device> findAll();

    AuthorizedDevice authorize(String deviceId, String otp);

}
