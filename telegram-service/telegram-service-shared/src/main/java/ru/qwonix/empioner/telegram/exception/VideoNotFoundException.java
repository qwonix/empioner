package ru.qwonix.empioner.telegram.exception;

public class VideoNotFoundException extends RuntimeException {
    public VideoNotFoundException() { super("Video not found"); }
}
