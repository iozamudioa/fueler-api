
package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.command.CreateFuelRecordCommand;
import net.iozamudioa.fueler.application.command.UpdateFuelRecordCommand;
import net.iozamudioa.fueler.domain.FuelPriceSuggestion;
import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateFuelRecord;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.UpdateFuelRecord;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelPriceResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordDetailResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordDetailResponse.OperatorInfo;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordDetailResponse.VehicleInfo;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordResponse;

@Component
public class FuelRecordWebMapper {

    public CreateFuelRecordCommand toCommand(
            String clientId,
            String deviceId,
            CreateFuelRecord request) {

        return new CreateFuelRecordCommand(
                clientId,
                deviceId,
                request.requestId(),
                request.date(),
                request.vehicleReference(),
                request.gasStationReference(),
                request.area(),
                request.operatorReference(),
                request.operatorName(),
                request.tankLevel(),
                request.distanceKm(),
                request.odometerKm(),
                request.ticketNumber(),
                request.note(),
                request.fuelType(),
                request.pricePerLiter(),
                request.liters());
    }

    public FuelRecordResponse toResponse(FuelRecord record) {

        return new FuelRecordResponse(
                record.id(),
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
                record.createdAt());
    }

    public UpdateFuelRecordCommand toUpdateCommand(
            String id,
            String clientId,
            String deviceId,
            UpdateFuelRecord request) {

        return new UpdateFuelRecordCommand(
                id,
                clientId,
                deviceId,
                request.date(),
                request.vehicleReference(),
                request.gasStationReference(),
                request.area(),
                request.operatorReference(),
                request.operatorName(),
                request.tankLevel(),
                request.distanceKm(),
                request.odometerKm(),
                request.ticketNumber(),
                request.note(),
                request.fuelType(),
                request.pricePerLiter(),
                request.liters(),
                request.reason(),
                request.version());
    }

    public FuelRecordDetailResponse toDetailResponse(
            FuelRecordDetail detail) {

        VehicleInfo vehicle = detail.vehicle() == null
                ? null
                : new VehicleInfo(
                        detail.vehicle().reference(),
                        detail.vehicle().area(),
                        detail.vehicle().active());

        OperatorInfo operator = detail.operator() == null
                ? null
                : new OperatorInfo(
                        detail.operator().reference(),
                        detail.operator().name(),
                        detail.operator().active());

        return new FuelRecordDetailResponse(
                toResponse(detail.fuelRecord()),
                vehicle,
                operator);
    }

    public FuelPriceResponse toPriceResponse(
            FuelPriceSuggestion suggestion) {

        return new FuelPriceResponse(
                suggestion.gasStationReference(),
                suggestion.fuelType(),
                suggestion.pricePerLiter(),
                suggestion.date());
    }

}
