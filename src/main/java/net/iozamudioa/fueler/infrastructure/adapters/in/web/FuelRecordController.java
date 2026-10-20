package net.iozamudioa.fueler.infrastructure.adapters.in.web;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.FuelRecordInputPort;
import net.iozamudioa.fueler.domain.FuelRecordSearch;
import net.iozamudioa.fueler.domain.PagedResult;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.FuelRecordWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateFuelRecord;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.UpdateFuelRecord;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelPriceResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordDetailResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.FuelRecordResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/fuel")
public class FuelRecordController {

        private final FuelRecordInputPort fuelRecordInputPort;
        private final FuelRecordWebMapper mapper;

        public FuelRecordController(FuelRecordInputPort fuelRecordInputPort, FuelRecordWebMapper mapper) {
                this.fuelRecordInputPort = fuelRecordInputPort;
                this.mapper = mapper;
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public FuelRecordResponse create(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @Valid @RequestBody CreateFuelRecord request) {

                return mapper.toResponse(
                                fuelRecordInputPort.create(
                                                mapper.toCommand(
                                                                principal.clientId(),
                                                                principal.deviceId(),
                                                                request)));
        }

        @PatchMapping("/{id}")
        public FuelRecordResponse update(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @PathVariable String id,
                        @Valid @RequestBody UpdateFuelRecord request) {

                return mapper.toResponse(
                                fuelRecordInputPort.update(
                                                mapper.toUpdateCommand(
                                                                id,
                                                                principal.clientId(),
                                                                principal.deviceId(),
                                                                request)));
        }

        @GetMapping
        public ResponseEntity<PagedResult<FuelRecordDetailResponse>> search(
                        @AuthenticationPrincipal DevicePrincipal principal,

                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,

                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

                        @RequestParam(required = false) String vehicleReference,

                        @RequestParam(required = false) String operatorReference,

                        @RequestParam(required = false) String area) {

                FuelRecordSearch criteria = new FuelRecordSearch(
                                from,
                                to,
                                vehicleReference,
                                operatorReference,
                                area,
                                page,
                                size);

                var result = fuelRecordInputPort.search(
                                principal.clientId(),
                                criteria);

                var response = PagedResult.of(
                                result.content()
                                                .stream()
                                                .map(mapper::toDetailResponse)
                                                .toList(),
                                result.page(),
                                result.size(),
                                result.totalElements());

                return ResponseEntity.ok(response);
        }

        @GetMapping("/prices/latest")
        public ResponseEntity<FuelPriceResponse> findLatestPrice(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @RequestParam String gasStationReference,
                        @RequestParam String fuelType,
                        @RequestParam @DateTimeFormat(pattern = "dd-MM-yy") LocalDate date) {

                return fuelRecordInputPort.findLatestPrice(
                                principal.clientId(),
                                gasStationReference,
                                fuelType,
                                date)
                                .map(mapper::toPriceResponse)
                                .map(ResponseEntity::ok)
                                .orElseGet(() -> ResponseEntity.noContent().build());
        }

}