package com.fiflip.backend.storage;

public interface ObjectStorage {
    String upload(UploadedFile file, String prefix);
}
