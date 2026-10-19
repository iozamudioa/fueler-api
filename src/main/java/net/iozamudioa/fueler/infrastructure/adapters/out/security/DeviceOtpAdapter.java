
package net.iozamudioa.fueler.infrastructure.adapters.out.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.DeviceOtpOutputPort;

@Component
public class DeviceOtpAdapter implements DeviceOtpOutputPort {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int OTP_BOUND = 1_000_000;
    private static final int MIN_SECRET_BYTES = 32;

    private final SecureRandom secureRandom;
    private final SecretKeySpec secretKey;

    public DeviceOtpAdapter(
            @Value("${fueler.activation.otp-secret}") String secretHex) {

        if (secretHex == null || secretHex.isBlank()) {
            throw new IllegalStateException(
                    "OTP secret is required");
        }

        byte[] secret;

        try {
            secret = HexFormat.of().parseHex(secretHex.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "OTP secret must be hexadecimal", ex);
        }

        if (secret.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "OTP secret must contain at least 32 bytes");
        }

        this.secureRandom = new SecureRandom();

        this.secretKey = new SecretKeySpec(
                secret,
                HMAC_ALGORITHM);
    }

    @Override
    public String generate() {
        int number = secureRandom.nextInt(OTP_BOUND);
        return "%06d".formatted(number);
    }

    @Override
    public String hash(String deviceId, String otp) {
        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Device ID is required");
        }

        if (otp == null || !otp.matches("[0-9]{6}")) {
            throw new IllegalArgumentException(
                    "OTP must contain exactly 6 digits");
        }

        String payload = deviceId + ":" + otp;

        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKey);

            byte[] digest = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);

        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(
                    "Could not calculate OTP HMAC", ex);
        }
    }

    @Override
    public boolean matches(
            String deviceId,
            String otp,
            String expectedHash) {

        if (expectedHash == null || expectedHash.isBlank()) {
            return false;
        }

        if (otp == null || !otp.matches("[0-9]{6}")) {
            return false;
        }

        final String actualHash = hash(deviceId, otp);

        byte[] actual = HexFormat.of().parseHex(actualHash);
        byte[] expected;

        try {
            expected = HexFormat.of().parseHex(expectedHash);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return MessageDigest.isEqual(actual, expected);
    }
}
