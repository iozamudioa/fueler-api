package net.iozamudioa.fueler.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFuelRecordCommand(
                String id,
                String clientId,
                String deviceId,
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
                String reason,
                Long version) {
}