
package net.iozamudioa.fueler.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuelPriceSuggestion(
        String gasStationReference,
        String fuelType,
        BigDecimal pricePerLiter,
        LocalDate date) {
}
