package co.pailab.lime.security;

import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.model.AuthenticationToken;
import co.pailab.lime.model.User;
import co.pailab.lime.repository.AuthenticationTokenRepository;
import com.auth0.jwt.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Date;

import static co.pailab.lime.security.SecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class GenerateJWT {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public static String run(int userId, User user, String deviceToken, AuthenticationTokenRepository authenticationTokenRepository) {
        if (deviceToken == null || deviceToken.trim() == "") deviceToken = "NON0DEVICE0TOKEN";
        String token = JWT.create()
                .withSubject("" + userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        AuthenticationToken userTokenExist = authenticationTokenRepository.findByUserIdAndDeviceToken(userId, deviceToken);

        AuthenticationToken userToken = userTokenExist == null ? new AuthenticationToken() : userTokenExist;

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        userToken.setDeviceToken(deviceToken);
        userToken.setAuthenticationToken(bCryptPasswordEncoder.encode(token));

        userToken.setUser(user);
        authenticationTokenRepository.save(userToken);

        return token;
    }

    public static String runLogin(int userId, User user, String deviceToken, int deviceType, AuthenticationTokenRepository authenticationTokenRepository, Environment env) {
        if (deviceToken == null || deviceToken.trim() == "") deviceToken = "NON0DEVICE0TOKEN";

        String token = JWT.create()
                .withSubject("" + userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        AuthenticationToken userTokenExist = authenticationTokenRepository.findByUserIdAndDeviceToken(userId, deviceToken.trim());

        AuthenticationToken userToken = userTokenExist == null ? new AuthenticationToken() : userTokenExist;

        System.out.println(userToken.getDeviceType());
        //define value of deviceType
        if (userToken.getDeviceType() == null) {
            if (deviceToken.equals("NON0DEVICE0TOKEN")) deviceType = 1;
        } else
            deviceType = userToken.getDeviceType();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        userToken.setDeviceToken(deviceToken.trim());
        userToken.setDeviceType(deviceType);
        userToken.setAuthenticationToken(bCryptPasswordEncoder.encode(token));
        userToken.setUser(user);
        authenticationTokenRepository.save(userToken);

        return token;
    }

    public static String runChangePw(int userId, User user, String deviceToken, int deviceType, boolean deactivateOtherDevice, AuthenticationTokenRepository authenticationTokenRepository, Environment env) {
        if (deviceToken == null || deviceToken.trim() == "") deviceToken = "NON0DEVICE0TOKEN";

        String token = JWT.create()
                .withSubject("" + userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        AuthenticationToken userTokenExist = authenticationTokenRepository.findByUserIdAndDeviceToken(userId, deviceToken);

        AuthenticationToken userToken = userTokenExist == null ? new AuthenticationToken() : userTokenExist;

        System.out.println(userToken.getDeviceType());
        //define value of deviceType
        if (userToken.getDeviceType() == null) {
            if (deviceToken.equals("NON0DEVICE0TOKEN")) deviceType = 1;
        } else
            deviceType = userToken.getDeviceType();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        userToken.setDeviceToken(deviceToken);
        userToken.setDeviceType(deviceType);
        userToken.setAuthenticationToken(bCryptPasswordEncoder.encode(token));
        userToken.setUser(user);
        authenticationTokenRepository.save(userToken);

        return token;
    }

    public static String internalRun() {
        String token = JWT.create()
                .withSubject("vitae-java")
                .withExpiresAt(new Date(System.currentTimeMillis() + INTERNAL_EXPIRATION_TIME))
                .sign(HMAC512(INTERNAL_SECRET.getBytes()));
        return token;
    }
}
