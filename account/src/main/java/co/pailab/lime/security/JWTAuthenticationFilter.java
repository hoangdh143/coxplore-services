package co.pailab.lime.security;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.*;
import co.pailab.lime.repository.AuthenticationTokenRepository;
import co.pailab.lime.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static co.pailab.lime.helper.CustomLogger.logRequestResponseOfFilter;
import static co.pailab.lime.security.SecurityConstants.HEADER_STRING;
import static co.pailab.lime.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private AuthenticationTokenRepository authenticationTokenRepository;

    private Environment env;
    private Object lock = new Object();
    private Object lock1 = new Object();

    @Autowired
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository,
                                   AuthenticationTokenRepository authenticationTokenRepository, Environment env) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.authenticationTokenRepository = authenticationTokenRepository;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Credential creds;
            synchronized (lock) {
                creds = mapper.readValue(req.getInputStream(), Credential.class);
            }

            Credential user = userRepository.findCredsByEmail(creds.getEmail());

            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            PrintWriter out = res.getWriter();

            if (user == null) {
                ErrorHttpResponse errorHttpResponse = new ErrorHttpResponse(false, 400, "INVALID_INFO",
                        "Invalid info", null);
                String errorHttResInString1 = mapper.writeValueAsString(errorHttpResponse);
                res.setStatus(400);
                out.print(errorHttResInString1);
                out.flush();
                logRequestResponseOfFilter(logger, req, errorHttpResponse);
                return null;
            }

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            if (creds.getPassword() == null || creds.getPassword().trim() == "" || !bCryptPasswordEncoder.matches(creds.getPassword(), user.getPassword())) {
                ErrorHttpResponse errorHttpResponse = new ErrorHttpResponse(false, 400, "Invalid info",
                        "Invalid info", res);
                String errorHttResInString2 = mapper.writeValueAsString(errorHttpResponse);
                out.print(errorHttResInString2);
                out.flush();
                logRequestResponseOfFilter(logger, req, errorHttpResponse);
                return null;
            }

            if (user.getActivated() != 1) {
                ErrorHttpResponse errorHttpResponse = new ErrorHttpResponse(false, 400, "INACTIVATED_ACCOUNT",
                        "Account has not been activated yet", res);
                String errorHttResInString3 = mapper.writeValueAsString(errorHttpResponse);
                out.print(errorHttResInString3);
                out.flush();
                logRequestResponseOfFilter(logger, req, errorHttpResponse);
                return null;
            }

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        String deviceToken = req.getHeader("deviceToken");
        int deviceType = Integer.parseInt(req.getHeader("deviceType"));

        if (deviceToken.trim().equals("") || req.getHeader("deviceType").trim().equals("")) {
            ErrorHttpResponse errorHttpResponse = new ErrorHttpResponse(false, 403, "UNAUTHORIZED", "Access denied", res);

            logRequestResponseOfFilter(logger, req, errorHttpResponse);
            logger.info("deviceToken or deviceType null");

            String errorHttResInString = mapper.writeValueAsString(errorHttpResponse);
            out.print(errorHttpResponse);
            out.flush();
        }

        String token;
        co.pailab.lime.model.User user;
        String email;
        int userId;
        synchronized (lock1) {
            user = ((UserSecurity) auth.getPrincipal()).getUser();
            userId = user.getId();
            token = GenerateJWT.runLogin(userId, user, deviceToken, deviceType, authenticationTokenRepository, env);
        }

        // prepare data for http response
        rootNode.put("token", token);
        rootNode.put("userId", userId);
        rootNode.put("groupId", user.getGroup().getId());
        rootNode.put("username", user.getUsername());
        rootNode.put("avatar", user.getAvatar());

        HttpResponse httpResponse = new SuccessHttpResponse(true, 200, "Successfully login", res, rootNode);
        String successHttResInString = mapper.writeValueAsString(httpResponse);
        out.print(successHttResInString);
        out.flush();

//        logger.info("Successfully login: {'userId': " + rootNode.get("userId") + ", 'username': " + rootNode.get("username")
//                + ", 'responseStatus' : " + res.getStatus() + "}");
        logRequestResponseOfFilter(logger, req, httpResponse);

//        res.getWriter().write(httpResponse.toString());
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
