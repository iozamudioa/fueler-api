package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Vehicle;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.VehicleDocument;

@Component
public class VehicleMongoMapper {

    public VehicleDocument toDocument(Vehicle vehicle) {
        return new VehicleDocument(
                vehicle.id(),
                vehicle.clientId(),
                vehicle.reference(),
                vehicle.area(),
                vehicle.active());
    }

    public Vehicle toDomain(VehicleDocument document) {
        return new Vehicle(
                document.id(),
                document.clientId(),
                document.reference(),
                document.area(),
                document.active());
    }
}