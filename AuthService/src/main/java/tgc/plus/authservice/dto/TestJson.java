package tgc.plus.authservice.dto;

public class TestJson {
    private String method;
    private String message;

    public TestJson(String method, String message) {
        this.method = method;
        this.message = message;
    }

    public String getMethod() {
        return method;
    }

    public String getMessage() {
        return message;
    }
}
