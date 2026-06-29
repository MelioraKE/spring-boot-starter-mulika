package tech.meliora.mulika.http;

/**
 *
 * @author kamochu
 */
public class MulikaHTTPResponse {

    private int responseCode;
    private String body;

    public MulikaHTTPResponse() {
    }

    public MulikaHTTPResponse(int responseCode, String body) {
        this.responseCode = responseCode;
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HTTPResponse{" + "responseCode=" + responseCode + ", body=" + body + '}';
    }

}
 