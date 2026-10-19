
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateClient(
        @NotBlank String name) {
}
