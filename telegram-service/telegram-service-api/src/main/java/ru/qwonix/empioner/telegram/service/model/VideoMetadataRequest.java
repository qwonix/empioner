package ru.qwonix.empioner.telegram.service.model;

public record VideoMetadataRequest(int width, int height, int duration, String mimeType, long fileSize, String fileName) {
    public VideoMetadataRequest {
        if (width <= 0 || height <= 0 || duration < 0 || fileSize < 0 || mimeType == null || mimeType.isBlank() || fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("Invalid video metadata");
        }
    }
}
