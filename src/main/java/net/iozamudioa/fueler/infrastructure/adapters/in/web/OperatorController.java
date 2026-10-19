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

import net.iozamudioa.fueler.application.ports.in.OperatorInputPort;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.OperatorWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateOperator;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.OperatorResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/operators")
public class OperatorController {

        private final OperatorInputPort operatorInputPort;
        private final OperatorWebMapper mapper;

        public OperatorController(OperatorInputPort operatorInputPort, OperatorWebMapper mapper) {
                this.operatorInputPort = operatorInputPort;
                this.mapper = mapper;
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public OperatorResponse create(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @Valid @RequestBody CreateOperator request) {

                CreateOperator normalized = mapper.toRequest(request);

                return mapper.toResponse(
                                operatorInputPort.create(
                                                principal.clientId(),
                                                normalized.reference(),
                                                normalized.name()));
        }

        @GetMapping
        public List<OperatorResponse> findAll(
                        @AuthenticationPrincipal DevicePrincipal principal) {

                return operatorInputPort
                                .findAllByClientId(principal.clientId())
                                .stream()
                                .map(mapper::toResponse)
                                .toList();
        }

        @GetMapping("/{reference}")
        public OperatorResponse findByReference(
                        @AuthenticationPrincipal DevicePrincipal principal,
                        @PathVariable String reference) {

                return mapper.toResponse(
                                operatorInputPort.findByReferenceAndClientId(
                                                reference,
                                                principal.clientId()));
        }
}