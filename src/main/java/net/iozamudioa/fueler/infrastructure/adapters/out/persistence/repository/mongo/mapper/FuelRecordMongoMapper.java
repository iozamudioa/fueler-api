package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordDocument;

@Component
public class FuelRecordMongoMapper {

    public FuelRecordDocument toDocument(FuelRecord record) {
        return new FuelRecordDocument(
                record.id(),
                record.clientId(),
                record.deviceId(),
                record.requestId(),
                record.date(),
                record.vehicleReference(),
                record.gasStationReference(),
                record.area(),
                record.operatorReference(),
                record.operatorName(),
                record.tankLevel(),
                record.distanceKm(),
                record.odometerKm(),
                record.ticketNumber(),
                record.note(),
                record.fuelType(),
                record.pricePerLiter(),
                record.liters(),
                record.totalAmount(),
                record.efficiencyKmPerLiter(),
                record.createdAt(),
                record.version());
    }

    public FuelRecord toDomain(FuelRecordDocument document) {
        return new FuelRecord(
                document.id(),
                document.clientId(),
                document.deviceId(),
                document.requestId(),
                document.date(),
                document.vehicleReference(),
                document.gasStationReference(),
                document.area(),
                document.operatorReference(),
                document.operatorName(),
                document.tankLevel(),
                document.distanceKm(),
                document.odometerKm(),
                document.ticketNumber(),
                document.note(),
                document.fuelType(),
                document.pricePerLiter(),
                document.liters(),
                document.totalAmount(),
                document.efficiencyKmPerLiter(),
                document.createdAt(),
                document.version());
    }
}