package net.iozamudioa.fueler.application.usecase;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import net.iozamudioa.fueler.application.ports.in.OperatorInputPort;
import net.iozamudioa.fueler.application.ports.out.OperatorOutputPort;
import net.iozamudioa.fueler.domain.Operator;
import net.iozamudioa.fueler.domain.exception.ResourceAlreadyExistsException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;

@Service
public class OperatorUseCase implements OperatorInputPort {

    private final OperatorOutputPort operatorOutputPort;

    public OperatorUseCase(OperatorOutputPort operatorOutputPort) {
        this.operatorOutputPort = operatorOutputPort;
    }

    @Override
    public Operator create(String clientId, String reference, String name) {

        String normalizedReference = reference.trim().toUpperCase(Locale.ROOT);

        if (operatorOutputPort.existsByClientIdAndReference(
                clientId, normalizedReference)) {

            throw new ResourceAlreadyExistsException(
                    "Operator", normalizedReference);
        }

        Operator operator = new Operator(
                clientId + ":" + normalizedReference,
                clientId,
                normalizedReference,
                name.trim(),
                true);

        return operatorOutputPort.save(operator);
    }

    @Override
    public List<Operator> findAllByClientId(String clientId) {
        return operatorOutputPort.findAllByClientId(clientId);
    }

    @Override
    public Operator findByReferenceAndClientId(
            String reference,
            String clientId) {

        String normalizedReference = reference.trim().toUpperCase(Locale.ROOT);

        return operatorOutputPort
                .findByReferenceAndClientId(normalizedReference, clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Operator", normalizedReference));
    }
}