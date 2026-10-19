
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateGasStation(
        @NotBlank String name) {
}
