package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fuel_records")
@CompoundIndexes({
                @CompoundIndex(name = "client_request_unique", def = "{'clientId': 1, 'requestId': 1}", unique = true),
                @CompoundIndex(name = "client_date_idx", def = "{'clientId': 1, 'date': -1}"),
                @CompoundIndex(name = "client_station_fuel_price_idx", def = "{'clientId': 1, 'gasStationReference': 1, 'fuelType': 1, 'createdAt': -1, '_id': -1}")
})
public record FuelRecordDocument(
                @Id String id,
                String clientId,
                String deviceId,
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
                Instant createdAt,
                Long version) {
}