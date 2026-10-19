package net.iozamudioa.fueler.application.ports.in;

import java.util.List;

import net.iozamudioa.fueler.domain.Operator;

public interface OperatorInputPort {

    Operator create(String clientId, String reference, String name);

    List<Operator> findAllByClientId(String clientId);

    Operator findByReferenceAndClientId(String reference, String clientId);
}