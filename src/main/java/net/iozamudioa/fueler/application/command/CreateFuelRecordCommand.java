package net.iozamudioa.fueler.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateFuelRecordCommand(
                String clientId,
                String deviceId,
                UUID requestId,
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
                BigDecimal liters) {
}