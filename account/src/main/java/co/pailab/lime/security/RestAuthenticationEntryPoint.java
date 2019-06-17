package co.pailab.lime.security;

import co.pailab.lime.helper.CustomLogger;
import co.pailab.lime.helper.ErrorHttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPoint
        implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        PrintWriter out = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ErrorHttpResponse errorHttpResponse = null;
        if (response.getStatus() == 401)
            errorHttpResponse = new ErrorHttpResponse(false, 401, "TOKEN_EXPIRED", "Authentication token is expired ", response);
        if (response.getStatus() == 4011)
            errorHttpResponse = new ErrorHttpResponse(false, 401, "INVALID_TOKEN", "Token is invalid ", response);
        else errorHttpResponse = new ErrorHttpResponse(false, 403, "UNAUTHORIZED", "Access denied", response);
        String jsonInString = mapper.writeValueAsString(errorHttpResponse);

        logger.info("Unsuccessful authorization attempt");
        CustomLogger.logRequestResponseOfFilter(logger, request, errorHttpResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonInString);
        out.flush();
    }
}
