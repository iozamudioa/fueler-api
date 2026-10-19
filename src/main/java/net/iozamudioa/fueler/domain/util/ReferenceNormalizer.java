
package net.iozamudioa.fueler.domain.util;

import java.text.Normalizer;
import java.util.Locale;

public final class ReferenceNormalizer {

    private ReferenceNormalizer() {
        // Utility class
    }

    public static String normalize(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Reference is required");
        }

        String normalized = Normalizer.normalize(
                value.trim(),
                Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    public static String normalizeNullable(String value) {
        return value == null ? null : normalize(value);
    }
}
