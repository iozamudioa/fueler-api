package net.iozamudioa.fueler.application.ports.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.FuelPriceSuggestion;
import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordSearch;
import net.iozamudioa.fueler.domain.PagedResult;

public interface FuelRecordOutputPort {

        FuelRecord save(FuelRecord fuelRecord);

        Optional<FuelRecord> findByClientIdAndRequestId(
                        String clientId,
                        String requestId);

        List<FuelRecord> findAllByClientId(String clientId);

        List<FuelRecord> findAllByClientIdAndDateBetween(
                        String clientId,
                        LocalDate startDate,
                        LocalDate endDate);

        Optional<FuelRecord> findByIdAndClientId(
                        String id,
                        String clientId);

        FuelRecord update(
                        FuelRecord record,
                        Long expectedVersion,
                        String modifiedBy,
                        String reason);

        PagedResult<FuelRecordDetail> search(
                        String clientId,
                        FuelRecordSearch criteria);

        Optional<FuelPriceSuggestion> findLatestPrice(
                        String clientId,
                        String gasStationReference,
                        String fuelType);

}