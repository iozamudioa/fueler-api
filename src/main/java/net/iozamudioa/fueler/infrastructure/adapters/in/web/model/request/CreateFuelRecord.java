package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateFuelRecord(
                @NotNull UUID requestId,
                @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yy") LocalDate date,
                @NotBlank String vehicleReference,
                @NotBlank String gasStationReference,
                String area,
                @NotBlank String operatorReference,
                String operatorName,
                @NotBlank String tankLevel,
                @NotNull @PositiveOrZero BigDecimal distanceKm,
                @NotNull @PositiveOrZero BigDecimal odometerKm,
                String ticketNumber,
                String note,
                @NotBlank String fuelType,
                @NotNull @PositiveOrZero BigDecimal pricePerLiter,
                @NotNull @PositiveOrZero BigDecimal liters) {
}
