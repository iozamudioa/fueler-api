
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateFuelRecord(

        LocalDate date,
        String vehicleReference,
        String gasStationReference,
        String area,
        String operatorReference,

        String operatorName,
        String tankLevel,

        @PositiveOrZero BigDecimal distanceKm,

        @PositiveOrZero BigDecimal odometerKm,

        String ticketNumber,
        String note,
        String fuelType,

        BigDecimal pricePerLiter,
        BigDecimal liters,

        @NotBlank String reason,

        @NotNull Long version

) {
}
