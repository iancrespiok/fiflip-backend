package com.fiflip.backend.storage;

public record UploadedFile(byte[] content, String filename, String contentType) {
}
