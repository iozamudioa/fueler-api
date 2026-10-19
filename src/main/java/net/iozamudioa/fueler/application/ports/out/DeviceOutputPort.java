
package net.iozamudioa.fueler.application.ports.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.Device;

public interface DeviceOutputPort {

        Device save(Device device);

        Optional<Device> findById(String id);

        Optional<Device> findByInstallationId(
                        String installationId);

        Optional<Device> findByTokenHash(
                        String tokenHash);

        List<Device> findAll();

        boolean approveIfPending(
                        String deviceId,
                        String clientId,
                        String otpHash,
                        Instant approvedAt,
                        Instant otpExpiresAt);

        boolean registerFailedOtpAttempt(
                        String deviceId,
                        String submittedOtpHash,
                        Instant now,
                        int maxAttempts);

        boolean activateIfApproved(
                        String deviceId,
                        String otpHash,
                        Instant now,
                        int maxAttempts,
                        String tokenHash);

        boolean revokeIfNotRevoked(String deviceId);
}
