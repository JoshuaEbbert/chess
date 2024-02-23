package handlers;

public class ClearRequest extends Request {
    private final String authToken;

    public ClearRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
