package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FuelRecordResponse(
        String id,
        String requestId,
        LocalDate date,
        String vehicleReference,
        String gasStationReference,
        String area,
        String operatorReference,
        String operatorName,
        String tankLevel,
        BigDecimal distanceKm,
        BigDecimal odometerKm,
        String ticketNumber,
        String note,
        String fuelType,
        BigDecimal pricePerLiter,
        BigDecimal liters,
        BigDecimal totalAmount,
        BigDecimal efficiencyKmPerLiter,
        Instant createdAt) {
}