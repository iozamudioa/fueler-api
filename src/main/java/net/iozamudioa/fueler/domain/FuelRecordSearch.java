
package net.iozamudioa.fueler.domain;

import java.time.LocalDate;

public record FuelRecordSearch(
        LocalDate from,
        LocalDate to,
        String vehicleReference,
        String operatorReference,
        String area,
        int page,
        int size) {

    public FuelRecordSearch {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to zero");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        vehicleReference = normalize(vehicleReference);
        operatorReference = normalize(operatorReference);
        area = normalize(area);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim().toUpperCase();
    }
}
