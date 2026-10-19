
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthorizeDevice(

        @NotBlank String deviceId,

        @NotBlank @Pattern(regexp = "[0-9]{6}") String otp

) {
}
