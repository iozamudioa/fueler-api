package net.iozamudioa.fueler.domain;

public record ExportedFile(
        String fileName,
        byte[] content) {
}
