package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOperator(

                @NotBlank String reference,

                @NotBlank String name

) {
}