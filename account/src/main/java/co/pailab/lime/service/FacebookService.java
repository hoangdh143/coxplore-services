package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ResponseBody;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.Group;
import co.pailab.lime.repository.*;
import co.pailab.lime.security.GenerateJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import co.pailab.lime.model.UserNonSecuredInfo;

@Service
public class FacebookService {
    @Value("${spring.social.facebook.appId}")
    String facebookAppId;
    @Value("${spring.social.facebook.appSecret}")
    String facebookSecret;
    @Value("${server}")
    String server;
    @Value("${social.apiUrl.Facebook}")
    private String facebookApiUrl;
    @Value("${numberGenUserName}")
    private String numberGenUserName;
    @Value("${userNamePrefix}")
    private String userNamePrefix;

    @Value("${file.avatar}")
    private String avatarLocation;

    @Value("${social.profileImageSize}")
    private int profileImageSize;

    @Value("${invitationCodePrefix}")
    private String invitationCodePrefix;

    private Environment env;

    private String accessToken;
    private UserRepository userRepository;
    private AuthenticationTokenRepository authenticationTokenRepository;
    private GroupRepository groupRepository;

    @Autowired
    public FacebookService(UserRepository userRepository, AuthenticationTokenRepository authenticationTokenRepository,
                           GroupRepository groupRepository, Environment env) {
        this.userRepository = userRepository;
        this.authenticationTokenRepository = authenticationTokenRepository;
        this.groupRepository = groupRepository;
        this.env = env;
    }

    public String createFacebookAuthorizationURL() {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(server + "/facebook");
        params.setScope("public_profile,email");
        return oauthOperations.buildAuthorizeUrl(params);
    }

    public HttpResponse createFacebookAccessToken(String code, HttpServletResponse res) {
        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, server + "/facebook", null);
        accessToken = accessGrant.getAccessToken();

        //prepare data for http response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("accessToken", accessToken);

        HttpResponse response = new SuccessHttpResponse(true, 200, "Successfully get fb token", res, rootNode);
        return response;

    }

    public User getInfo() {
        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "name", "email", "first_name", "last_name", "picture"};
        return facebook.fetchObject("me", User.class, fields);
    }

    private String getFacebookProfilePicUrl(String fbUserId, String accessToken, String defaultLocation) {
        try {
            URL url = new URL(facebookApiUrl + "/" + fbUserId + "?fields=picture.width(" + profileImageSize + ").height(" + profileImageSize + ")&redirect=false&access_token=" + accessToken);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                String responseBody = IOUtils.toString(in, "UTF-8");
                JSONObject json = new JSONObject(responseBody);
                String imageUrl = json.getJSONObject("picture").getJSONObject("data").getString("url");
                return imageUrl;
            } else {
                System.out.println("Could not get User Profile Picture");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultLocation;
    }

    public HttpResponse registerAndLogin(String accessToken, String introduceCode, HttpServletRequest req, HttpServletResponse res) throws Exception {
        try {

            //Get Facebook User Info
            User facebookUser = this.getFacebookUserInfo(accessToken);
            avatarLocation = this.getFacebookProfilePicUrl(facebookUser.getId(), accessToken, avatarLocation);

            //Return error if email not found
            if (facebookUser.getEmail() == null) {
                return new ErrorHttpResponse(false, 400, "FACEBOOK_EMAIL_NOT_EXIST", "Facebook email does not exist", res);
            }

            co.pailab.lime.model.User userExist = userRepository.findByEmail(facebookUser.getEmail());

            if (userExist != null && userExist.getActivated() == 1)
                return this.getResponseOfExistingUserAccount(userExist, req, res);

            return registerAndActivateNewUserAccount(facebookUser, userExist, introduceCode, req, res);

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }

    }

    private User getFacebookUserInfo(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken);
        String[] fields = {"id", "name", "email", "first_name", "last_name", "picture"};
        return facebook.fetchObject("me", User.class, fields);
    }

    private HttpResponse getResponseOfExistingUserAccount(co.pailab.lime.model.User user, HttpServletRequest req, HttpServletResponse res) {
        //get header parameters
        String deviceToken = req.getHeader("deviceToken");
        int deviceType = Integer.parseInt(req.getHeader("deviceType"));

        ResponseBody responseBody = new ResponseBody();
        //create new token after register via google success
        String token = GenerateJWT.runLogin(user.getId(), user, deviceToken, deviceType, authenticationTokenRepository, env);

        // prepare data for http response
        responseBody.put("userId", user.getId());
        responseBody.put("token", token);
        responseBody.put("username", user.getUsername());
        responseBody.put("avatar", user.getAvatar());
        responseBody.put("groupId", user.getGroup().getId());

        return new SuccessHttpResponse(true, 200, "Successfully login", res, responseBody.get());
    }

    private HttpResponse registerAndActivateNewUserAccount(User facebookUser, co.pailab.lime.model.User userExist, String introduceCode, HttpServletRequest req, HttpServletResponse res) throws Exception {
        co.pailab.lime.model.User user;

        if (userExist != null) {
            user = userExist;
        } else {
            user = new co.pailab.lime.model.User();
        }

        Group group = groupRepository.findByName("user");
        user.setGroup(group);
        user.setEmail(facebookUser.getEmail());
        user.setFirstName(facebookUser.getFirstName());
        user.setLastName(facebookUser.getLastName());
        user.setActivated(1);
        user.setGender(0);
        user.setAvatar(avatarLocation);

        co.pailab.lime.model.User userDb = userRepository.save(user);

        //set default username for the user
        int userId = userDb.getId();
        String username = userNamePrefix + (Integer.parseInt(numberGenUserName) + userId);
        userDb.setUsername(username);

            userExist = userRepository.save(userDb);
        return this.getResponseOfExistingUserAccount(userExist, req, res);
    }

}
