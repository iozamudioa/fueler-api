package net.iozamudioa.fueler.infrastructure.adapters.in.web.model.response;

public record OperatorResponse(
                String id,
                String reference,
                String name,
                boolean active) {
}