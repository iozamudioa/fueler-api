package net.iozamudioa.fueler.application.ports.in;

import java.util.List;

import net.iozamudioa.fueler.domain.Vehicle;

public interface VehicleInputPort {

    Vehicle create(String clientId, String reference, String area);

    List<Vehicle> findAllByClientId(String clientId);

    Vehicle findByReferenceAndClientId(String reference, String clientId);

}
