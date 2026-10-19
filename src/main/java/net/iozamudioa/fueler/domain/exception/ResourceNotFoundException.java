package net.iozamudioa.fueler.domain.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String identifier) {
        super("%s not found: %s".formatted(resource, identifier));
    }
}