package co.pailab.lime.security;

public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 5_184_000_000L; // 60 days 5_184_000_000
    //	public static final long EXPIRATION_TIME = 60_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/register";
    public static final String ACTIVATION_URL = "/activate/process";
    public static final String ACTIVATE_REQUEST_URL = "/activate/request";
    public static final String FORGET_PASSWORD = "/accounts/forget_password";
    public static final String VERIFY_FORGET_PASSWORD_TOKEN = "/accounts/forget_password/verify";
    public static final String NEW_PASSWORD_WHEN_FORGET = "/forget_password/change_password";
    public static final String LOGIN_WITH_FACEBOOK = "/login/facebook";
    public static final String LOGIN_WITH_GOOGLE = "/login/google";
    public static final long INTERNAL_EXPIRATION_TIME = 31_536_000_000L;
    public static final String INTERNAL_SECRET = "InternalSecretKeyToGenJWTs";
    public static String internalToken;

    public SecurityConstants() {
        internalToken = GenerateJWT.internalRun();
    }
}
