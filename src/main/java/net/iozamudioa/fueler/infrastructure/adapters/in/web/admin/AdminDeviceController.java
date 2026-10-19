
package net.iozamudioa.fueler.infrastructure.adapters.in.web.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.DeviceInputPort;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.DeviceWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.ApproveDevice;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ApprovedDeviceResponse;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.DeviceResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(AdminRoutes.DEVICES)
public class AdminDeviceController {

    private final DeviceInputPort deviceInputPort;
    private final DeviceWebMapper mapper;

    public AdminDeviceController(
            DeviceInputPort deviceInputPort,
            DeviceWebMapper mapper) {

        this.deviceInputPort = deviceInputPort;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DeviceResponse> findAll() {
        return deviceInputPort.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PostMapping("/{id}/approve")
    public ApprovedDeviceResponse approve(
            @PathVariable String id,
            @Valid @RequestBody ApproveDevice request) {

        return mapper.toApprovedResponse(
                deviceInputPort.approve(
                        id,
                        request.clientId()));
    }

    @PostMapping("/{id}/revoke")
    public DeviceResponse revoke(@PathVariable String id) {
        return mapper.toResponse(
                deviceInputPort.revoke(id));
    }
}
