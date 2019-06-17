package co.pailab.lime.helper;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

public class CustomLogger {
    public static String responseMessage;

    public static void logRequestResponse(Logger logger, HttpServletRequest request, HttpResponse response) {
        CustomLogger.responseMessage = response.toString();
    }

    public static void logRequestResponse(Logger logger, HttpServletRequest request, String responseMessage) {
        CustomLogger.responseMessage = "Could not catch full Response body. Content available: \n\t" + responseMessage;
    }

    public static void logRequestResponseOfFilter(Logger logger, HttpServletRequest request, HttpResponse response) {
        try {
            //Log Request
            String body = "Unknown";
//            if ("POST".equalsIgnoreCase(request.getMethod())) {
//                body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//            }
            logger.info("Request: { \n\tMethod: {}, \n\tURL: {}, \n\tHeader.UserID: {}, " +
                            "\n\tHeader.Authorization: {}, \n\tHeader.deviceToken: {}, " +
                            "\n\tHeader.deviceType: {}, \n\tHeader.user-agent: {}, \n\tBody: {}}",
                    request.getMethod(), request.getRequestURL(), request.getHeader("userId"),
                    request.getHeader("Authorization"), request.getHeader("deviceToken"),
                    request.getHeader("deviceType"), request.getHeader("user-agent"), body);

            //Log Response
            logger.info("Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
