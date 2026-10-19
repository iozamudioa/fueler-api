package net.iozamudioa.fueler.application.ports.out;

import java.util.List;
import java.util.Optional;

import net.iozamudioa.fueler.domain.Operator;

public interface OperatorOutputPort {

    Operator save(Operator operator);

    List<Operator> findAllByClientId(String clientId);

    Optional<Operator> findByReferenceAndClientId(
            String reference,
            String clientId);

    boolean existsByClientIdAndReference(
            String clientId,
            String reference);
}