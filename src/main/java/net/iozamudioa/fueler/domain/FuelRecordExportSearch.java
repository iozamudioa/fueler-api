package net.iozamudioa.fueler.domain;

import java.time.LocalDate;

public record FuelRecordExportSearch(
                LocalDate from,
                LocalDate to,
                String vehicleReference,
                String operatorReference,
                String area) {
}