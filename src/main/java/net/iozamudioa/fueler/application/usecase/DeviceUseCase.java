
package net.iozamudioa.fueler.application.usecase;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.DeviceInputPort;
import net.iozamudioa.fueler.application.ports.out.ClientOutputPort;
import net.iozamudioa.fueler.application.ports.out.DeviceOtpOutputPort;
import net.iozamudioa.fueler.application.ports.out.DeviceOutputPort;
import net.iozamudioa.fueler.application.ports.out.TokenOutputPort;
import net.iozamudioa.fueler.domain.ApprovedDevice;
import net.iozamudioa.fueler.domain.AuthorizedDevice;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.domain.exception.ResourceAlreadyExistsException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;

@Service
public class DeviceUseCase implements DeviceInputPort {

        private static final Duration OTP_EXPIRATION = Duration.ofMinutes(5);

        private static final int MAX_OTP_ATTEMPTS = 5;

        private final DeviceOutputPort deviceOutputPort;
        private final ClientOutputPort clientOutputPort;
        private final DeviceOtpOutputPort deviceOtpOutputPort;
        private final TokenOutputPort tokenOutputPort;

        public DeviceUseCase(
                        DeviceOutputPort deviceOutputPort,
                        ClientOutputPort clientOutputPort,
                        DeviceOtpOutputPort deviceOtpOutputPort,
                        TokenOutputPort tokenOutputPort) {

                this.deviceOutputPort = deviceOutputPort;
                this.clientOutputPort = clientOutputPort;
                this.deviceOtpOutputPort = deviceOtpOutputPort;
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
        public ApprovedDevice approve(
                        String deviceId,
                        String clientId) {

                Device device = deviceOutputPort.findById(deviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Device", deviceId));

                if (device.status() != Device.Status.PENDING) {
                        throw new ResourceAlreadyExistsException(
                                        "Device is not pending", deviceId);
                }

                boolean validClient = clientOutputPort.findById(clientId)
                                .filter(client -> client.active())
                                .isPresent();

                if (!validClient) {
                        throw new IllegalArgumentException(
                                        "Client not found or inactive");
                }

                Instant now = Instant.now();
                Instant expiresAt = now.plus(OTP_EXPIRATION);

                String otp = deviceOtpOutputPort.generate();
                String otpHash = deviceOtpOutputPort.hash(
                                device.id(), otp);

                boolean approved = deviceOutputPort.approveIfPending(
                                device.id(),
                                clientId,
                                otpHash,
                                now,
                                expiresAt);

                if (!approved) {
                        throw new IllegalStateException(
                                        "Device could not be approved");
                }

                return new ApprovedDevice(
                                device.id(),
                                clientId,
                                otp,
                                expiresAt);
        }

        @Override
        public AuthorizedDevice authorize(
                        String deviceId,
                        String otp) {

                Device device = deviceOutputPort.findById(deviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Device", deviceId));

                if (device.status() != Device.Status.APPROVED) {
                        throw new IllegalArgumentException(
                                        "Device is not approved for activation");
                }

                if (otp == null || !otp.matches("[0-9]{6}")) {
                        throw new IllegalArgumentException(
                                        "Invalid OTP");
                }

                String otpHash = deviceOtpOutputPort.hash(
                                deviceId, otp);

                String token = tokenOutputPort.issue();
                String tokenHash = tokenOutputPort.hash(token);

                boolean activated = deviceOutputPort.activateIfApproved(
                                deviceId,
                                otpHash,
                                Instant.now(),
                                MAX_OTP_ATTEMPTS,
                                tokenHash);

                if (!activated) {

                        deviceOutputPort.registerFailedOtpAttempt(
                                        deviceId,
                                        otpHash,
                                        Instant.now(),
                                        MAX_OTP_ATTEMPTS);

                        throw new IllegalArgumentException(
                                        "Invalid or expired OTP");
                }

                return new AuthorizedDevice(
                                device.id(),
                                device.clientId(),
                                token);
        }

        @Override
        public Device revoke(String deviceId) {

                if (!deviceOutputPort.revokeIfNotRevoked(deviceId)) {
                        throw new IllegalStateException(
                                        "Device not found or already revoked");
                }

                return deviceOutputPort.findById(deviceId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Device", deviceId));
        }

        @Override
        public List<Device> findAll() {
                return deviceOutputPort.findAll();
        }
}
