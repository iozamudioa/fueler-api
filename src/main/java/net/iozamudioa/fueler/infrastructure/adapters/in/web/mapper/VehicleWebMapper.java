package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Vehicle;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateVehicle;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.VehicleResponse;

@Component
public class VehicleWebMapper {

    public CreateVehicle toRequest(CreateVehicle request) {
        return new CreateVehicle(
                request.reference().trim(),
                request.area() == null ? null : request.area().trim());
    }

    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.id(),
                vehicle.reference(),
                vehicle.area(),
                vehicle.active());
    }
}