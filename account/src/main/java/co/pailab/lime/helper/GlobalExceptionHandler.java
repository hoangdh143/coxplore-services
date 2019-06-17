package co.pailab.lime.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public HttpServletResponse handleAllException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String message = "Exception catched by GlobalExceptionHandler: " + ex.getMessage();
        logRequestResponse(logger, request, message);
        return response;
    }
}
