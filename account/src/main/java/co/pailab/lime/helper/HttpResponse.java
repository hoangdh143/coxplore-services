package co.pailab.lime.helper;


import javax.servlet.http.HttpServletResponse;


public class HttpResponse {
    private Boolean success;
    private int statusCode;
    private String message;

    public HttpResponse(Boolean successStatus, int statusCode, String message, HttpServletResponse res) {
        super();
        this.success = successStatus;
        this.statusCode = statusCode;
        this.message = message;

        setHttpStatusCode(res);
    }

    public void setHttpStatusCode(HttpServletResponse res) {
        if (res == null) return;
        res.setStatus(statusCode);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "\"success\"=" + success +
                ", \n\t\"statusCode\"=" + statusCode +
                ", \n\t\"message\"='" + message + '\'';
    }
}
