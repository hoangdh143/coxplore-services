package co.pailab.lime.helper;


public class CustomizedError extends Error {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Boolean success;
    private int statusCode;
    private String message;
    private String errorCode;

    public CustomizedError(Boolean success, int statusCode, String errorCode, String message) {
        super();
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.errorCode = errorCode;
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
