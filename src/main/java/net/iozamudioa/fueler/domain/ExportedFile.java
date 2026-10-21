package net.iozamudioa.fueler.domain;

import org.springframework.core.io.Resource;

public record ExportedFile(
                String fileName,
                Resource resource,
                long size) {
}
