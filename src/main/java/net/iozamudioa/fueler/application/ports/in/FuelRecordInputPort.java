package net.iozamudioa.fueler.application.ports.in;

import java.util.Optional;

import net.iozamudioa.fueler.application.command.CreateFuelRecordCommand;
import net.iozamudioa.fueler.application.command.UpdateFuelRecordCommand;
import net.iozamudioa.fueler.domain.FuelPriceSuggestion;
import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordSearch;
import net.iozamudioa.fueler.domain.PagedResult;

public interface FuelRecordInputPort {

    FuelRecord create(CreateFuelRecordCommand command);

    FuelRecord update(UpdateFuelRecordCommand command);

    PagedResult<FuelRecordDetail> search(
            String clientId,
            FuelRecordSearch criteria);

    Optional<FuelPriceSuggestion> findLatestPrice(
            String clientId,
            String gasStationReference,
            String fuelType);

}