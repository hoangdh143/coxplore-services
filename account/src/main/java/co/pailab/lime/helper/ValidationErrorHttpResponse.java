package co.pailab.lime.helper;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.http.HttpServletResponse;

public class ValidationErrorHttpResponse extends ErrorHttpResponse {
    private JsonNode validationError;

    public ValidationErrorHttpResponse(Boolean successStatus, int statusCode, String errorCode, String message,
                                       HttpServletResponse res, JsonNode validationError) {
        super(successStatus, statusCode, errorCode, message, res);

        this.validationError = validationError;
    }

    public JsonNode getValidationError() {
        return validationError;
    }

    public void setValidationError(JsonNode validationError) {
        this.validationError = validationError;
    }
}
