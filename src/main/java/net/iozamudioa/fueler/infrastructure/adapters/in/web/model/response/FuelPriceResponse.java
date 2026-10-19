
package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuelPriceResponse(
        String gasStationReference,
        String fuelType,
        BigDecimal pricePerLiter,
        LocalDate date) {
}
