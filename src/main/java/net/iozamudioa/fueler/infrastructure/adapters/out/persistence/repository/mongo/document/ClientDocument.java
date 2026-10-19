
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
public record ClientDocument(
        @Id String id,
        String name,
        boolean active) {
}
