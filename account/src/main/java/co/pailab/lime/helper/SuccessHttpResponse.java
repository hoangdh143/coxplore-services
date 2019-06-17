package co.pailab.lime.helper;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletResponse;

public class SuccessHttpResponse extends HttpResponse {
    private JsonNode data;

    public SuccessHttpResponse(Boolean successStatus, int statusCode, String message,
                               HttpServletResponse res, JsonNode data) {
        super(successStatus, statusCode, message, res);
        this.data = data;
    }

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{ \n\t" + super.toString() +
                ", \n\t\"data\"=" + data +
                "\n }";
    }
}
