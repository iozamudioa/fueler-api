package net.iozamudioa.fueler.domain.exception;

public class ResourceAlreadyExistsException extends BusinessException {

    public ResourceAlreadyExistsException(String resource, String identifier) {
        super("%s already exists: %s".formatted(resource, identifier));
    }
}