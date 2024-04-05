package requests;

public class ClearRequest extends Request {
    private final String authToken;

    public ClearRequest(String authToken) {
        this.authToken = authToken;
    }
}
