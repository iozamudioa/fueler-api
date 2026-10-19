
package net.iozamudioa.fueler.infrastructure.adapters.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.GasStationInputPort;
import net.iozamudioa.fueler.domain.GasStation;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.GasStationWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateGasStation;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.GasStationResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/gas-stations")
public class GasStationController {

    private final GasStationInputPort gasStationInputPort;
    private final GasStationWebMapper mapper;

    public GasStationController(
            GasStationInputPort gasStationInputPort,
            GasStationWebMapper mapper) {

        this.gasStationInputPort = gasStationInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<GasStationResponse> register(
            @AuthenticationPrincipal DevicePrincipal principal,
            @Valid @RequestBody CreateGasStation request) {

        GasStation gasStation = gasStationInputPort.register(
                principal.clientId(),
                mapper.toName(request));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toResponse(gasStation));
    }

    @GetMapping
    public ResponseEntity<List<GasStationResponse>> findAll(
            @AuthenticationPrincipal DevicePrincipal principal) {

        List<GasStationResponse> response = gasStationInputPort
                .findAll(principal.clientId())
                .stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<GasStationResponse> findByReference(
            @AuthenticationPrincipal DevicePrincipal principal,
            @PathVariable String reference) {

        GasStation gasStation = gasStationInputPort.findByReference(
                principal.clientId(),
                reference);

        return ResponseEntity.ok(
                mapper.toResponse(gasStation));
    }

    @PostMapping("/{reference}/deactivate")
    public ResponseEntity<GasStationResponse> deactivate(
            @AuthenticationPrincipal DevicePrincipal principal,
            @PathVariable String reference) {

        GasStation gasStation = gasStationInputPort.deactivate(
                principal.clientId(),
                reference);

        return ResponseEntity.ok(
                mapper.toResponse(gasStation));
    }
}
