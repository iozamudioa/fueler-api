package net.iozamudioa.fueler.infrastructure.adapters.in.web.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.iozamudioa.fueler.application.ports.in.ClientInputPort;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper.ClientWebMapper;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateClient;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ClientResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(AdminRoutes.CLIENTS)
public class AdminClientController {

    private final ClientInputPort clientInputPort;
    private final ClientWebMapper mapper;

    public AdminClientController(
            ClientInputPort clientInputPort,
            ClientWebMapper mapper) {

        this.clientInputPort = clientInputPort;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(
            @Valid @RequestBody CreateClient request) {

        return mapper.toResponse(
                clientInputPort.create(
                        mapper.toRequest(request)));
    }

    @GetMapping
    public List<ClientResponse> findAll() {
        return clientInputPort.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}