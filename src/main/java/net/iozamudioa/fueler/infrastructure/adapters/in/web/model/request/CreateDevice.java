
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDevice(
        @NotBlank String installationId,
        String label) {
}
