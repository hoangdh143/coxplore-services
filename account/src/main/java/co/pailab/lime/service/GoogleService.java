package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.*;
import co.pailab.lime.repository.*;
import co.pailab.lime.security.GenerateJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class GoogleService {
    @Value("${spring.social.google.appId}")
    String googleAppId;
    @Value("${spring.social.google.appSecret}")
    String googleSecret;

    @Value("${server}")
    String server;

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
    public GoogleService(UserRepository userRepository, AuthenticationTokenRepository authenticationTokenRepository,
                         GroupRepository groupRepository, Environment env) {
        this.userRepository = userRepository;
        this.authenticationTokenRepository = authenticationTokenRepository;
        this.groupRepository = groupRepository;
        this.env = env;
    }

    public String createGoogleAuthorizationURL() {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleSecret);
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri(server + "/google");
        params.setScope("profile email");
        return oauthOperations.buildAuthorizeUrl(params);
    }

    public HttpResponse createGoogleAccessToken(String code, HttpServletResponse res) {
        GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(googleAppId, googleSecret);
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
                server + "/google", null);

        accessToken = accessGrant.getAccessToken();
        // prepare data for http response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        ((ObjectNode) rootNode).put("accessToken", accessToken);

        HttpResponse response = new SuccessHttpResponse(true, 200, "Successfully get google token", res, rootNode);
        return response;

    }

    //    public GoogleUserInfo getInfo() {
    //        Google google = new GoogleTemplate(accessToken);
    //        System.out.println(google.userOperations().getUserInfo().getEmail());
    //        return google.userOperations().getUserInfo();
    //    }
    private String getGoogleProfilePicUrl(String url, String defaultUrl) {
        if (url != null) {
            UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(url);
            urlBuilder.replaceQueryParam("sz", profileImageSize);
            String newUrl = urlBuilder.build().toUriString();
            return newUrl;
        }
        return defaultUrl;
    }

    @SuppressWarnings("deprecation")
    public HttpResponse registerAndLogin(String accessToken, String introduceCode, HttpServletRequest req, HttpServletResponse res) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

//         get userInfo from google
        HttpRequest httpRequestGoogleInfo = new HttpRequest();
        GoogleInfo googleInfo = httpRequestGoogleInfo.getGoogleUserInfo(accessToken);
        if (googleInfo == null)
            return new ErrorHttpResponse(false, 404, "NO_GOOGLE_INFO", "No google info has been found",
                    res);

        String email = googleInfo.getEmail();
        String firstName = googleInfo.getGiven_name();
        String lastName = googleInfo.getFamily_name();
        avatarLocation = getGoogleProfilePicUrl(googleInfo.getPicture(), avatarLocation);

        if (email == null)
            return new ErrorHttpResponse(false, 400, "GMAIL_NOT_EXIST",
                    "Gmail does not exist", res);

        //get header parameters
        String deviceToken = req.getHeader("deviceToken");
        int deviceType = Integer.parseInt(req.getHeader("deviceType"));

        //find if user's email already existed
        User userExist = userRepository.findByEmail(email);
        User user = new co.pailab.lime.model.User();

        int userId = 0;
        String username = "";
        String avatar = "";
        int totalPoint = 0;
        boolean activatedRequest = true;


        if (userExist != null && userExist.getActivated() == 1) {
            activatedRequest = false;
            userId = userExist.getId();
            username = userExist.getUsername();
            avatar = userExist.getAvatar();
        }

        //register user if email not exist
        if(activatedRequest){
            if(userExist != null) user = userExist;
            Group group = groupRepository.findByName("user");
            user.setGroup(group);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setActivated(1);
            user.setGender(0);
            avatar = avatarLocation;
            user.setAvatar(avatar);
            co.pailab.lime.model.User userDb = userRepository.save(user);

            //set default username for the user
            userId = userDb.getId();
            username = userNamePrefix + (Integer.parseInt(numberGenUserName) + userId);
            userDb.setUsername(username);

            userExist = userRepository.save(userDb);
        }

        //create new token after register via google success
        String token = GenerateJWT.runLogin(userId, userExist, deviceToken, deviceType, authenticationTokenRepository, env);

        // prepare data for http response
        ((ObjectNode) rootNode).put("userId", userId);
        ((ObjectNode) rootNode).put("token", token);
        ((ObjectNode) rootNode).put("username", username);
        ((ObjectNode) rootNode).put("avatar", avatar);
        ((ObjectNode) rootNode).put("groupId", userExist.getGroup().getId());

        HttpResponse response = new SuccessHttpResponse(true, 200, "Successfully login", res, rootNode);
        return response;
    }
}
