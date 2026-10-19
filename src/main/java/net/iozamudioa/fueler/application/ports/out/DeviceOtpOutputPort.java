package net.iozamudioa.fueler.application.ports.out;

public interface DeviceOtpOutputPort {

    String generate();

    String hash(String deviceId, String otp);

    boolean matches(
            String deviceId,
            String otp,
            String expectedHash);

}
