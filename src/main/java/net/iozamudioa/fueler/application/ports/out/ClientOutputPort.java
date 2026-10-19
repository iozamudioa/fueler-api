package net.iozamudioa.fueler.application.ports.out;

import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.Client;

public interface ClientOutputPort {

    Client save(Client client);

    List<Client> findAll();

    Optional<Client> findById(String id);
}
