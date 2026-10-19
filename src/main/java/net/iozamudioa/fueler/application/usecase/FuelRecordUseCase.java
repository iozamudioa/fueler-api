package net.iozamudioa.fueler.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.command.CreateFuelRecordCommand;
import net.iozamudioa.fueler.application.command.UpdateFuelRecordCommand;
import net.iozamudioa.fueler.application.ports.in.FuelRecordInputPort;
import net.iozamudioa.fueler.application.ports.out.FuelRecordOutputPort;
import net.iozamudioa.fueler.application.ports.out.GasStationOutputPort;
import net.iozamudioa.fueler.domain.FuelPriceSuggestion;
import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordSearch;
import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.domain.PagedResult;
import net.iozamudioa.fueler.domain.exception.InvalidFuelRecordException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;
import net.iozamudioa.fueler.domain.util.ReferenceNormalizer;

@Service
public class FuelRecordUseCase implements FuelRecordInputPort {

        private final FuelRecordOutputPort fuelRecordOutputPort;
        private final GasStationOutputPort gasStationOutputPort;

        public FuelRecordUseCase(FuelRecordOutputPort fuelRecordOutputPort,
                        GasStationOutputPort gasStationOutputPort) {
                this.fuelRecordOutputPort = fuelRecordOutputPort;
                this.gasStationOutputPort = gasStationOutputPort;
        }

        @Override
        public FuelRecord create(CreateFuelRecordCommand command) {

                String clientId = command.clientId();
                String requestId = command.requestId().toString();

                var existing = fuelRecordOutputPort
                                .findByClientIdAndRequestId(clientId, requestId);

                if (existing.isPresent()) {
                        return existing.get();
                }

                validateQuantities(
                                command.liters(),
                                command.pricePerLiter(),
                                command.distanceKm(),
                                command.odometerKm());

                String gasStationReference = validateGasStation(
                                clientId,
                                command.gasStationReference());

                FuelRecord record = new FuelRecord(
                                UUID.randomUUID().toString(),
                                clientId,
                                command.deviceId(),
                                requestId,
                                command.date(),
                                ReferenceNormalizer.normalizeNullable(command.vehicleReference()),
                                gasStationReference,
                                command.area(),
                                command.operatorReference(),
                                command.operatorName(),
                                command.tankLevel(),
                                command.distanceKm(),
                                command.odometerKm(),
                                command.ticketNumber(),
                                command.note(),
                                ReferenceNormalizer.normalizeNullable(command.fuelType()),
                                command.pricePerLiter(),
                                command.liters(),
                                calculateTotal(command.liters(), command.pricePerLiter()),
                                calculateEfficiency(command.distanceKm(), command.liters()),
                                Instant.now(),
                                0L);

                try {
                        return fuelRecordOutputPort.save(record);
                } catch (DuplicateKeyException ex) {
                        return fuelRecordOutputPort
                                        .findByClientIdAndRequestId(clientId, requestId)
                                        .orElseThrow(() -> ex);
                }
        }

        @Override
        public FuelRecord update(UpdateFuelRecordCommand command) {

                // 1. Recuperar registro dentro del tenant.
                FuelRecord current = fuelRecordOutputPort
                                .findByIdAndClientId(command.id(), command.clientId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "FuelRecord", command.id()));

                // 2. Validar motivo de corrección.
                if (command.reason() == null
                                || command.reason().isBlank()) {
                        throw new InvalidFuelRecordException(
                                        "Correction reason is required");
                }

                // 3. Validar versión recibida.
                if (command.version() == null
                                || !command.version().equals(current.version())) {
                        throw new InvalidFuelRecordException(
                                        "Fuel record version mismatch");
                }

                // 4. Aplicar PATCH parcial.
                BigDecimal liters = coalesce(
                                command.liters(), current.liters());

                BigDecimal pricePerLiter = coalesce(
                                command.pricePerLiter(), current.pricePerLiter());

                BigDecimal distanceKm = coalesce(
                                command.distanceKm(), current.distanceKm());

                BigDecimal odometerKm = coalesce(
                                command.odometerKm(), current.odometerKm());

                // 5. Validar resultado final.
                validateQuantities(
                                liters,
                                pricePerLiter,
                                distanceKm,
                                odometerKm);

                String gasStationReference = command.gasStationReference() == null
                                ? current.gasStationReference()
                                : validateGasStation(
                                                command.clientId(),
                                                ReferenceNormalizer.normalize(command.gasStationReference()));

                // 6. Reconstruir el dominio.
                FuelRecord updated = new FuelRecord(
                                current.id(),
                                current.clientId(),
                                current.deviceId(),
                                current.requestId(),
                                coalesce(command.date(), current.date()),
                                command.vehicleReference() == null
                                                ? current.vehicleReference()
                                                : ReferenceNormalizer.normalizeNullable(command.vehicleReference()),
                                gasStationReference,
                                coalesce(command.area(), current.area()),
                                coalesce(
                                                command.operatorReference(),
                                                current.operatorReference()),
                                coalesce(
                                                command.operatorName(),
                                                current.operatorName()),
                                coalesce(command.tankLevel(), current.tankLevel()),
                                distanceKm,
                                odometerKm,
                                coalesce(command.ticketNumber(), current.ticketNumber()),
                                coalesce(command.note(), current.note()),
                                command.fuelType() == null
                                                ? current.fuelType()
                                                : ReferenceNormalizer.normalizeNullable(command.fuelType()),
                                pricePerLiter,
                                liters,
                                calculateTotal(liters, pricePerLiter),
                                calculateEfficiency(distanceKm, liters),
                                current.createdAt(),
                                current.version());

                // 7. Guardar mediante operación de actualización controlada.
                return fuelRecordOutputPort.update(
                                updated,
                                command.version(),
                                command.deviceId(),
                                command.reason().trim());
        }

        @Override
        public PagedResult<FuelRecordDetail> search(
                        String clientId,
                        FuelRecordSearch criteria) {

                if (clientId == null || clientId.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Client ID is required");
                }

                if (criteria == null) {
                        throw new IllegalArgumentException(
                                        "Search criteria are required");
                }

                return fuelRecordOutputPort.search(
                                clientId,
                                criteria);
        }

        @Override
        public Optional<FuelPriceSuggestion> findLatestPrice(
                        String clientId,
                        String gasStationReference,
                        String fuelType) {

                if (clientId == null || clientId.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Client ID is required");
                }

                return fuelRecordOutputPort.findLatestPrice(
                                clientId,
                                ReferenceNormalizer.normalize(gasStationReference),
                                ReferenceNormalizer.normalize(fuelType));
        }

        private String validateGasStation(
                        String clientId,
                        String reference) {

                if (reference == null) {
                        throw new IllegalArgumentException(
                                        "Gas station reference is required");
                }
                // ? current.gasStationReference()
                // : gasStationReference,
                GasStation gasStation = gasStationOutputPort
                                .findByClientIdAndReference(
                                                clientId,
                                                ReferenceNormalizer.normalize(reference))
                                .orElseThrow(() -> new InvalidFuelRecordException(
                                                "Gas station not found"));

                if (!gasStation.active()) {
                        throw new InvalidFuelRecordException(
                                        "Gas station is inactive");
                }

                return gasStation.reference();
        }

        private void validateQuantities(
                        BigDecimal liters,
                        BigDecimal pricePerLiter,
                        BigDecimal distanceKm,
                        BigDecimal odometerKm) {

                if (liters == null
                                || pricePerLiter == null
                                || distanceKm == null
                                || odometerKm == null
                                || liters.signum() <= 0
                                || pricePerLiter.signum() <= 0
                                || distanceKm.signum() < 0
                                || odometerKm.signum() < 0) {

                        throw new InvalidFuelRecordException(
                                        "Invalid quantities");
                }
        }

        private BigDecimal calculateTotal(
                        BigDecimal liters,
                        BigDecimal pricePerLiter) {

                return liters.multiply(pricePerLiter)
                                .setScale(2, RoundingMode.HALF_UP);
        }

        private BigDecimal calculateEfficiency(
                        BigDecimal distanceKm,
                        BigDecimal liters) {

                return distanceKm.divide(
                                liters, 2, RoundingMode.HALF_UP);
        }

        private <T> T coalesce(T newValue, T currentValue) {
                return newValue != null ? newValue : currentValue;
        }

}