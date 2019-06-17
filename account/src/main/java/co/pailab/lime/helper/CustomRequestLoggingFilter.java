package co.pailab.lime.helper;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CustomRequestLoggingFilter extends AbstractRequestLoggingFilter {
    public CustomRequestLoggingFilter() {
    }

    protected boolean shouldLog(HttpServletRequest request) {
        return this.logger.isDebugEnabled();
    }

    protected void beforeRequest(HttpServletRequest request, String message) {
//        this.logger.info(message);
    }

    protected void afterRequest(HttpServletRequest request, String message) {
        //Log Request
        this.logger.info(message
                .replace(";", "; \n\t")
                .replace("uri=", request.getMethod() + " uri="));
        //Log Response
        this.logger.info("Response: " + CustomLogger.responseMessage);
        CustomLogger.responseMessage = "";
    }
}
