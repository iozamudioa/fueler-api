package net.iozamudioa.fueler.infrastructure.adapters.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.VehicleInputPort;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.VehicleWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateVehicle;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.VehicleResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

        private final VehicleInputPort vehicleInputPort;
        private final VehicleWebMapper mapper;

        public VehicleController(VehicleInputPort vehicleInputPort, VehicleWebMapper mapper) {
                this.vehicleInputPort = vehicleInputPort;
                this.mapper = mapper;
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public VehicleResponse create(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @Valid @RequestBody CreateVehicle request) {

                CreateVehicle normalized = mapper.toRequest(request);

                return mapper.toResponse(
                                vehicleInputPort.create(
                                                principal.clientId(),
                                                normalized.reference(),
                                                normalized.area()));
        }

        @GetMapping
        public List<VehicleResponse> findAll(
                        @AuthenticationPrincipal DevicePrincipal principal) {

                return vehicleInputPort
                                .findAllByClientId(principal.clientId())
                                .stream()
                                .map(mapper::toResponse)
                                .toList();
        }

        @GetMapping("/{reference}")
        public VehicleResponse findByReference(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @PathVariable String reference) {

                return mapper.toResponse(
                                vehicleInputPort.findByReferenceAndClientId(
                                                reference,
                                                principal.clientId()));
        }
}