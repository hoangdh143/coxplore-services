package co.pailab.lime.security;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
    @Override
    public void handle(HttpServletRequest _request, HttpServletResponse _response, AccessDeniedException _exception) throws IOException, ServletException {
        setErrorPage("/securityAccessDenied");  // this is a standard Spring MVC Controller
        System.out.println(_exception.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        _response.setContentType("application/json");
        _response.setCharacterEncoding("UTF-8");
        PrintWriter out = _response.getWriter();
        String successHttResInString;
        if (_exception.getMessage().equals("cannot find module")) {
            HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_MODULE", "Cannot find module", _response);
            successHttResInString = mapper.writeValueAsString(response);
            out.print(successHttResInString);
            out.flush();
        }

        if (_exception.getMessage().equals("cannot find group")) {
            HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_GROUP", "Cannot find group", _response);
            successHttResInString = mapper.writeValueAsString(response);
            out.print(successHttResInString);
            out.flush();
        }

        if (_exception.getMessage().equals("no permission")) {
            HttpResponse response = new ErrorHttpResponse(false, 401, "NO_ACCESS_PERMISSION", "No access permission", _response);
            successHttResInString = mapper.writeValueAsString(response);
            out.print(successHttResInString);
            out.flush();
        }

        // any time a user tries to access a part of the application that they do not have rights to lock their account
        super.handle(_request, _response, _exception);
    }
}
