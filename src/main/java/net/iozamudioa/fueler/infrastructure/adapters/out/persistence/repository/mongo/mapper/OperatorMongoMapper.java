package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Operator;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.OperatorDocument;

@Component
public class OperatorMongoMapper {

    public OperatorDocument toDocument(Operator operator) {
        return new OperatorDocument(
                operator.id(),
                operator.clientId(),
                operator.reference(),
                operator.name(),
                operator.active());
    }

    public Operator toDomain(OperatorDocument document) {
        return new Operator(
                document.id(),
                document.clientId(),
                document.reference(),
                document.name(),
                document.active());
    }
}