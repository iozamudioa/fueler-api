package net.iozamudioa.fueler.application.usecase;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.DeviceInputPort;
import net.iozamudioa.fueler.application.ports.out.ClientOutputPort;
import net.iozamudioa.fueler.application.ports.out.DeviceOutputPort;
import net.iozamudioa.fueler.application.ports.out.TokenOutputPort;
import net.iozamudioa.fueler.domain.ApprovedDevice;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.domain.exception.ResourceAlreadyExistsException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;

@Service
public class DeviceUseCase implements DeviceInputPort {

        private final DeviceOutputPort deviceOutputPort;
        private final ClientOutputPort clientOutputPort;
        private final TokenOutputPort tokenOutputPort;

        public DeviceUseCase(DeviceOutputPort deviceOutputPort, ClientOutputPort clientOutputPort,
                        TokenOutputPort tokenOutputPort) {
                this.deviceOutputPort = deviceOutputPort;
                this.clientOutputPort = clientOutputPort;
                this.tokenOutputPort = tokenOutputPort;
        }

        @Override
        public Device request(String installationId, String label) {

                if (installationId == null || installationId.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Installation ID is required");
                }

                String normalizedInstallationId = installationId.trim();

                if (deviceOutputPort.findByInstallationId(
                                normalizedInstallationId).isPresent()) {

                        throw new ResourceAlreadyExistsException(
                                        "Device", normalizedInstallationId);
                }

                Device device = new Device(
                                UUID.randomUUID().toString(),
                                normalizedInstallationId,
                                label == null ? null : label.trim(),
                                null,
                                Device.Status.PENDING,
                                null,
                                Instant.now(),
                                null);

                return deviceOutputPort.save(device);
        }

        @Override
        public ApprovedDevice approve(String deviceId, String clientId) {

                Device device = deviceOutputPort.findById(deviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Device", deviceId));

                if (device.status() == Device.Status.ACTIVE) {
                        throw new ResourceAlreadyExistsException(
                                        "Active device", deviceId);
                }

                boolean validClient = clientOutputPort.findById(clientId)
                                .filter(client -> client.active())
                                .isPresent();

                if (!validClient) {
                        throw new IllegalArgumentException(
                                        "Client not found or inactive");
                }

                String token = tokenOutputPort.issue();

                Device approved = new Device(
                                device.id(),
                                device.installationId(),
                                device.label(),
                                clientId,
                                Device.Status.ACTIVE,
                                tokenOutputPort.hash(token),
                                device.createdAt(),
                                Instant.now());

                Device saved = deviceOutputPort.save(approved);

                return new ApprovedDevice(
                                saved.id(),
                                saved.clientId(),
                                token);
        }

        @Override
        public Device revoke(String deviceId) {
                Device device = deviceOutputPort.findById(deviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Device", deviceId));

                Device revoked = new Device(
                                device.id(),
                                device.installationId(),
                                device.label(),
                                device.clientId(),
                                Device.Status.REVOKED,
                                null,
                                device.createdAt(),
                                device.approvedAt());

                return deviceOutputPort.save(revoked);
        }

        @Override
        public List<Device> findAll() {
                return deviceOutputPort.findAll();
        }

}
