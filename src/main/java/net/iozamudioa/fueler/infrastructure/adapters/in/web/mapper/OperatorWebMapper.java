package net.iozamudioa.fueler.infrastructure.adapters.in.web.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Operator;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.request.CreateOperator;
import net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response.OperatorResponse;

@Component
public class OperatorWebMapper {

    public CreateOperator toRequest(CreateOperator request) {
        return new CreateOperator(
                request.reference().trim(),
                request.name().trim());
    }

    public OperatorResponse toResponse(Operator operator) {
        return new OperatorResponse(
                operator.id(),
                operator.reference(),
                operator.name(),
                operator.active());
    }
}