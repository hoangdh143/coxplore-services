package co.pailab.lime.helper;


public class ValidationError extends Error {
    private static final long serialVersionUID = 1L;
    private String field;
    private String errorMessage;

    public ValidationError(String field, String errorMessage) {
        super();
        this.field = field;
        this.errorMessage = errorMessage;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
