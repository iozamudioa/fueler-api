
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

public record FuelRecordDetailResponse(
        FuelRecordResponse fuelRecord,
        VehicleInfo vehicle,
        OperatorInfo operator) {

    public record VehicleInfo(
            String reference,
            String area,
            boolean active) {
    }

    public record OperatorInfo(
            String reference,
            String name,
            boolean active) {
    }
}
