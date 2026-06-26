package co.ingedev.dataAnalist.dto.response;

public record JwtResponse(
        String token,
        String type,
        String username,
        String role
) {
    public JwtResponse(String token, String username, String role) {
        this(token, "Bearer", username, role);
    }
}
