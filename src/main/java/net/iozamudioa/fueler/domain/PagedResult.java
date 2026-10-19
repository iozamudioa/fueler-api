
package net.iozamudioa.fueler.domain;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public PagedResult {
        content = List.copyOf(content);

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to zero");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero");
        }

        if (totalElements < 0) {
            throw new IllegalArgumentException(
                    "Total elements cannot be negative");
        }

        long expectedPages = totalElements / size
                + (totalElements % size == 0 ? 0 : 1);

        if (expectedPages > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Total pages exceed the supported range");
        }

        if (totalPages != expectedPages) {
            throw new IllegalArgumentException(
                    "Total pages do not match total elements and page size");
        }
    }

    public static <T> PagedResult<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements) {

        long pages = totalElements / size
                + (totalElements % size == 0 ? 0 : 1);

        return new PagedResult<>(
                content,
                page,
                size,
                totalElements,
                Math.toIntExact(pages));
    }

    public boolean hasNext() {
        return (long) page + 1 < totalPages;
    }

    public boolean hasPrevious() {
        return page > 0 && totalPages > 0;
    }
}
