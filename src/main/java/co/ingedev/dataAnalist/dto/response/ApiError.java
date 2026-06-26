package co.ingedev.dataAnalist.dto.response;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) {
    public static ApiError of(int status, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, message, path);
    }
}
