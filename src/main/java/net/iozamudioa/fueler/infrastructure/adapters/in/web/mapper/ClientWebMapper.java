package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Client;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateClient;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.ClientResponse;

@Component
public class ClientWebMapper {

    public String toRequest(CreateClient request) {
        return request.name().trim();
    }

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.id(),
                client.name(),
                client.active());
    }
}