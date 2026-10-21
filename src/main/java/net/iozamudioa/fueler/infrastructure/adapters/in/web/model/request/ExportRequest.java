package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public record ExportRequest(
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

                String vehicleReference,
                String operatorReference,
                String area) {
}
