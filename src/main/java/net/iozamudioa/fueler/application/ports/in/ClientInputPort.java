
package net.iozamudioa.fueler.application.ports.in;

import java.util.List;

import net.iozamudioa.fueler.domain.Client;

public interface ClientInputPort {

    Client create(String name);

    List<Client> findAll();
}
