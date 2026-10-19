
package net.iozamudioa.fueler.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.ClientInputPort;
import net.iozamudioa.fueler.application.ports.out.ClientOutputPort;
import net.iozamudioa.fueler.domain.Client;

@Service
public class ClientUseCase implements ClientInputPort {

    private final ClientOutputPort clientOutputPort;

    public ClientUseCase(ClientOutputPort clientOutputPort) {
        this.clientOutputPort = clientOutputPort;
    }

    @Override
    public Client create(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Client name is required");
        }

        Client client = new Client(
                UUID.randomUUID().toString(),
                name.trim(),
                true);

        return clientOutputPort.save(client);
    }

    @Override
    public List<Client> findAll() {
        return clientOutputPort.findAll();
    }
}
