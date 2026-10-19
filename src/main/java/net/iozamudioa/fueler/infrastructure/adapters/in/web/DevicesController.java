
package net.iozamudioa.fueler.infrastructure.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.DeviceInputPort;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.DeviceWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateDevice;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.DeviceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "Device Registration", description = "Registro público de instalaciones de Fueler")
@RestController
@RequestMapping("/api/v1/devices")
public class DevicesController {

    private final DeviceInputPort deviceInputPort;
    private final DeviceWebMapper mapper;

    public DevicesController(
            DeviceInputPort deviceInputPort,
            DeviceWebMapper mapper) {

        this.deviceInputPort = deviceInputPort;
        this.mapper = mapper;
    }

    @Operation(summary = "Registrar dispositivo", description = "Registra una instalación de Fueler "
            + "para solicitar autorización administrativa.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud de registro creada"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponse create(
            @Valid @RequestBody CreateDevice request) {

        Device device = deviceInputPort.request(
                mapper.toInstallationId(request),
                mapper.toLabel(request));

        return mapper.toResponse(device);
    }
}
