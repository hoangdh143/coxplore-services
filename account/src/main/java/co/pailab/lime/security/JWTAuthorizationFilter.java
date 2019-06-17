package co.pailab.lime.security;

import co.pailab.lime.model.AuthenticationToken;
import co.pailab.lime.repository.AuthenticationTokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static co.pailab.lime.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationTokenRepository authenticationTokenRepository;

    public JWTAuthorizationFilter(AuthenticationManager authManager,
                                  AuthenticationTokenRepository authenticationTokenRepository) {
        super(authManager);
        this.authenticationTokenRepository = authenticationTokenRepository;
    }

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            logger.info("No Token found on Request Header");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req, res);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        String token = request.getHeader(HEADER_STRING);
        int userIdFromRequestHeader = Integer.parseInt(request.getHeader("userId"));
        String headerDeviceToken = request.getHeader("deviceToken").trim();
        String deviceToken = (headerDeviceToken == "" || headerDeviceToken == null) ? "NON0DEVICE0TOKEN" : headerDeviceToken;

        if (token != null) {
            String userId = null;
            // parse the token.

            try {
                userId = JWT.require(Algorithm.HMAC512(SECRET.getBytes())).build()
                        .verify(token.replace(TOKEN_PREFIX, "")).getSubject();

                // verify if userId from token payload matches with userId from request header
                if (Integer.parseInt(userId) != userIdFromRequestHeader) {
                    logger.info("Invalid User Id");
                    return null;
                }

            } catch (Exception e) {
                logger.info("Token verification exception");
                if (e.getClass().getName() == "com.auth0.jwt.exceptions.TokenExpiredException")
                    response.setStatus(401);
                else
                    response.setStatus(4011);
            }

            if (userId != null) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                AuthenticationToken userToken = authenticationTokenRepository.findByUserIdAndDeviceToken(userIdFromRequestHeader, deviceToken);

                if (userToken == null) {
                    logger.info("No User Token found");
                    return null;
                }

                if (!bCryptPasswordEncoder.matches(token.replace(TOKEN_PREFIX, ""), userToken.getAuthenticationToken())) {
                    logger.info("User Token not matched");
                    return null;
                }

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null,
                        new ArrayList<>());
                return auth;

            }
            return null;
        }
        return null;
    }
}
