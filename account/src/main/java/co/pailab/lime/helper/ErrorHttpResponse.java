package co.pailab.lime.helper;


import javax.servlet.http.HttpServletResponse;


public class ErrorHttpResponse extends HttpResponse {
    private String errorCode;

    public ErrorHttpResponse(Boolean successStatus, int statusCode, String errorCode, String message,
                             HttpServletResponse res) {
        super(successStatus, statusCode, message, res);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "{ \n\t" + super.toString() +
                ",\n\t\"errorCode\"='" + errorCode + '\''
                + "\n }";
    }
}
