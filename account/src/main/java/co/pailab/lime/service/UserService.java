package co.pailab.lime.service;

import co.pailab.lime.helper.*;
import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.model.*;
import co.pailab.lime.repository.*;
import co.pailab.lime.security.GenerateJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("userService")
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    private EmailService emailService;
    private GroupRepository groupRepository;
    private StorageService storageService;
    private AuthenticationTokenRepository authenticationTokenRepository;
    private String s3Dir = "/lime/account/image";
    private boolean userBmiChange = false;


    @Value("${numberGenUserName}")
    private String numberGenUserName;

    @Value("${userNamePrefix}")
    private String userNamePrefix;

    @Value("${file.avatar}")
    private String avatarLocation;

    @Value("${invitationCodePrefix}")
    private String invitationCodePrefix;

    private Environment env;

    private Email emailObj;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder
//                        , EmailService emailService
                        , GroupRepository groupRepository, StorageService storageService,
                       AuthenticationTokenRepository authenticationTokenRepository,
                       Environment env, Email emailObj) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.emailService = emailService;
        this.groupRepository = groupRepository;
        this.storageService = storageService;
        this.authenticationTokenRepository = authenticationTokenRepository;
        this.env = env;
        this.emailObj = emailObj;
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(1000000);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String datetime(int minute) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Add minute minutes to the calendar time
        calendar.add(Calendar.MINUTE, minute);
        String newTime = df.format(calendar.getTime());

        return newTime;

    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByActivationToken(String activationToken) {
        return userRepository.findByActivationToken(activationToken);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse findById(HttpServletRequest req, HttpServletResponse res) {
        try {
            User user = userRepository.findById(Integer.parseInt(req.getHeader("userId")));

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(user);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);

            ((ObjectNode) rootNode).put("user", node);

            HttpResponse response = new SuccessHttpResponse(true, 200, "User info", res, rootNode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse adminFindUserById(int id, HttpServletResponse res) {
        User user = userRepository.findById(id);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(user);
        JsonNode node = mapper.valueToTree(userNonSecuredInfo);

        ((ObjectNode) rootNode).put("user", node);
        ((ObjectNode) rootNode).put("groupId", user.getGroup().getId());

        HttpResponse response = new SuccessHttpResponse(true, 200, "User info", res, rootNode);
        return response;
    }

    public HttpResponse getAll(int page, int limit, HttpServletResponse res) {
        Pageable pageable = PageRequest.of(page, limit);
        List<User> users = userRepository.findAllByOrderByIdDesc(pageable);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();

        List<UserNonSecuredInfo> userNonSecuredInfos = users.stream()
                .map(UserNonSecuredInfo::new).collect(Collectors.toList());

        JsonNode node = mapper.valueToTree(userNonSecuredInfos);

        ((ObjectNode) rootNode).put("users", node);
        ((ObjectNode) rootNode).put("totalRecordNumber", userRepository.countAll());

        HttpResponse response = new SuccessHttpResponse(true, 200, "User info", res, rootNode);
        return response;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse update(UserNonSecuredInfo user, HttpServletRequest req, HttpServletResponse res) {
        try {
            String deviceToken = req.getHeader("deviceToken");
            int userIdHeader = Integer.parseInt(req.getHeader("userId"));
            User userExist = userRepository.findById(userIdHeader);

            // check if user exist via id
            if (userExist == null)
                return new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "account does not exist", res);
            if (user.getUsername() != null && user.getUsername().trim() != "")
                userExist.setUsername(user.getUsername());
            if (user.getFirstName() != null && user.getFirstName().trim() != "")
                userExist.setFirstName(user.getFirstName());
            if (user.getLastName() != null && user.getLastName().trim() != "")
                userExist.setLastName(user.getLastName());
            if (user.getAddress() != null && user.getAddress().trim() != "")
                userExist.setAddress(user.getAddress());
            if (user.getPhone() != null && user.getPhone().trim() != "")
                userExist.setPhone(user.getPhone());
            if (user.getDateOfBirth() != null && user.getDateOfBirth().trim() != "")
                userExist.setDateOfBirth(user.getDateOfBirth());
            if (user.getHeight() > 0.00 && user.getHeight() != userExist.getHeight()) {
                userExist.setHeight(user.getHeight());
                userBmiChange = true;
            }
            if (user.getWeight() > 0.00 && user.getWeight() != userExist.getWeight()) {
                userExist.setWeight(user.getWeight());
                userBmiChange = true;
            }
            if (user.getGender() >= 1 && user.getGender() <= 3)
                userExist.setGender(user.getGender());

            // update user into db
            userRepository.save(userExist);

            // create new token following the success update
            String token = GenerateJWT.run(userExist.getId(), userExist, deviceToken, authenticationTokenRepository);

            // prepare data for http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(userExist);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);
            ((ObjectNode) rootNode).put("token", token);
            ((ObjectNode) rootNode).put("user", node);

            // success http response
            HttpResponse response = new SuccessHttpResponse(true, 200, "Account has been successfully updated", res,
                    rootNode);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse changePassword(UserPassword user, HttpServletRequest req, HttpServletResponse res) {
        try {
            String deviceToken = req.getHeader("deviceToken");
            int userIdHeader = Integer.parseInt(req.getHeader("userId"));
            User userExist = userRepository.findById(userIdHeader);
            AuthenticationToken authenticationTokenOfDevice = authenticationTokenRepository.findByUserIdAndDeviceToken(userIdHeader, deviceToken);
            String oldPwHashFromDb = userExist.getPassword();

            String oldPwFromRequest = user.getPassword();
            if (oldPwFromRequest == null || oldPwFromRequest.trim() == "")
                return new ErrorHttpResponse(false, 400, "INVALID_PASSWORD",
                        "Invalid password", res);

            if (!bCryptPasswordEncoder.matches(oldPwFromRequest, oldPwHashFromDb)) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "INCORRECT_PASSWORD",
                        "Incorrect password", res);
                return response;
            }

            Zxcvbn passwordCheck = new Zxcvbn();
            Strength strength = passwordCheck.measure((String) user.getNewPassword());

            if (strength.getScore() < 1) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "PASSWORD_TOO_WEAK", "Password is too weak",
                        res);
                return response;
            }

            String newPwHash = bCryptPasswordEncoder.encode(user.getNewPassword());
            userExist.setPassword(newPwHash);
            userRepository.save(userExist);

            //delete all authenticationToken of other devices once password change
            authenticationTokenRepository.deleteByUserId(userIdHeader);

            // create new token following the success update
            String token = GenerateJWT.runChangePw(userExist.getId(), userExist, deviceToken, authenticationTokenOfDevice.getDeviceType(), true, authenticationTokenRepository, env);

            // prepare data for http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(userExist);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);
            ((ObjectNode) rootNode).put("token", token);
            ((ObjectNode) rootNode).put("user", node);

            HttpResponse response = new SuccessHttpResponse(true, 200, "Password has been successfully updated", res,
                    rootNode);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse changeAvatar(String file, HttpServletResponse res, HttpServletRequest req) throws IOException {
        if (file == null) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "EMPTY_FILE", "File is empty", res);
            return response;
        }

        String deviceToken = req.getHeader("deviceToken");
        int id = Integer.parseInt(req.getHeader("userId"));
        User user = userRepository.findById(id);
        String oldAvatar = user.getAvatar();
        String objectKey = oldAvatar == null ? "" : oldAvatar.substring(user.getAvatar().lastIndexOf("/") + 1);

        byte[] bI = org.apache.commons.codec.binary.Base64.decodeBase64(file);
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bI));

        File tmpFile = new File(System.getProperty("java.io.tmpdir") + "avatar_" + user.getId() + ".png");

        ImageIO.write(img, "png", tmpFile);

        String s3Url = storageService.store(tmpFile, res, s3Dir);
        if (s3Url == null) {
            HttpResponse response = new ErrorHttpResponse(false, 501, "CANNOT_STORE_IMAGE", "Cannot store image", res);
            return response;
        }
        try {
            user.setAvatar(s3Url);

            userRepository.save(user);

            storageService.deleteOne(objectKey, s3Dir, res);

            // create new token following the success update
            String token = GenerateJWT.run(user.getId(), user, deviceToken, authenticationTokenRepository);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(user);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);
            ((ObjectNode) rootNode).put("user", node);
            ((ObjectNode) rootNode).put("token", token);

            HttpResponse response = new SuccessHttpResponse(true, 200, "Avatar has been successfully updated", res,
                    rootNode);
            return response;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }

    }

    public HttpResponse register(User user, HttpServletResponse res) {
        try {
            // Lookup user in database by e-mail
            User userExists = userRepository.findByEmail(user.getEmail());

            if (userExists != null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "EMAIL_EXISTED", "Email is already existed",
                        res);
                return response;
            }

            Zxcvbn passwordCheck = new Zxcvbn();
            Strength strength = passwordCheck.measure((String) user.getPassword());

            if (strength.getScore() < 1) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "PASSWORD_TOO_WEAK", "Password is too weak",
                        res);
                return response;
            }

            // new user so we create user and send confirmation e-mail
            // hashed password
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            // Generate random 6-number string token for confirmation link
            user.setActivationToken(getRandomNumberString());

            user.setActivationTokenExpiry(datetime(3));

            Group group = groupRepository.findByName("user");
            user.setGroup(group);

            userRepository.save(user);

//            Email.sesActivationEmail(user, emailService);
            emailObj.activationEmail(user);

            // prepare data for success http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("email", user.getEmail());

            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "An account activation email has been sent to your email : " + user.getEmail(), res, rootNode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse processActivate(User user, HttpServletResponse res) {
        try {
            String email = user.getEmail();
            String password = user.getPassword();
            String activationCode = user.getActivationToken();

            User userDb = userRepository.findByEmail(email);

            HttpResponse userCheck = verifyAccToActivate(userDb, password, res);
            if (!userCheck.getSuccess())
                return userCheck;

            if (!activationCode.equals(userDb.getActivationToken())) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "INCORRECT_ACTIVATION_CODE",
                        "The activation code is incorrect", res);
                return response;
            }

            // Check if activation token is expired
            String expiryTimeString = userDb.getActivationTokenExpiry();
            String currentTimeString = datetime(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date expiryTime = sdf.parse(expiryTimeString);
            Date currentTime = sdf.parse(currentTimeString);

            if (expiryTime.compareTo(currentTime) < 0) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "TOKEN_EXPIRED",
                        "The activation token is expired", res);
                return response;
            }

            // Set user to activated
            userDb.setActivated(1);

            // Set user gender to none
            userDb.setGender(0);

            //set default username
            String defaultUserName = userNamePrefix + (Integer.parseInt(numberGenUserName) + userDb.getId());
            userDb.setUsername(defaultUserName);

            //set default avatar
            userDb.setAvatar(avatarLocation);

            // Save user
            userRepository.save(userDb);

            // prepare data for http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("email", userDb.getEmail());

            HttpResponse response = new SuccessHttpResponse(true, 200, "Your account has been activated!", res,
                    rootNode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse requestActivate(User user, HttpServletResponse res) {
        try {
            String email = user.getEmail();
            String password = user.getPassword();
            User userDb = userRepository.findByEmail(email);

            HttpResponse userCheck = verifyAccToActivate(userDb, password, res);
            if (!userCheck.getSuccess())
                return userCheck;

            // Generate random 6-number string token for confirmation link
            userDb.setActivationToken(getRandomNumberString());

            userDb.setActivationTokenExpiry(datetime(3));

            userRepository.save(userDb);

            // activationEmail(userDb);
//            Email.sesActivationEmail(userDb, emailService);
            emailObj.activationEmail(userDb);

            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "An account activation email has been sent to your email :" + user.getEmail(), res, null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }

    }

    @SuppressWarnings("deprecation")
    public HttpResponse adminRegister(UserGroup user, HttpServletResponse res) {
        // Lookup user in database by e-mail
        User userExists = userRepository.findByEmail(user.getEmail());

        Zxcvbn passwordCheck = new Zxcvbn();
        Strength strength = passwordCheck.measure((String) user.getPassword());

        if (user.getPassword().length() < 6) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "PASSWORD_TOO_WEAK", "Password is too weak", res);
            return response;
        }

        if (userExists != null) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "EMAIL_EXISTED", "Email is already existed", res);
            return response;

        }

        User userEntity = new User();

        try {
            userEntity.setEmail(user.getEmail());
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setUsername(user.getUsername());
            userEntity.setPhone(user.getPhone());
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            userEntity.setPassword(hashedPassword);

            // Generate random 6-number string token for confirmation link
            userEntity.setActivationToken(getRandomNumberString());
            userEntity.setActivationTokenExpiry(datetime(3));

            Group group = groupRepository.findById(user.getGroupId());
            if (group == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_GROUP", "Cannot find group",
                        res);
                return response;
            }
            userEntity.setGroup(group);
            userEntity.setActivated(1);

            // save user to db
            userRepository.save(userEntity);
        } catch (Exception e) {
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }

        // prepare data for success http response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(userEntity);

        JsonNode node = mapper.valueToTree(userNonSecuredInfo);
        ((ObjectNode) rootNode).put("user", node);
        ((ObjectNode) rootNode).put("groupId", user.getGroupId());

        HttpResponse response = new SuccessHttpResponse(true, 200, "User detail info", res, rootNode);
        return response;
    }

    public HttpResponse checkAdminAccess(HttpServletRequest request, HttpServletResponse response) {
        try {
            int userId = Integer.parseInt(request.getHeader("userId"));
            User user = userRepository.findById(userId);
            if (user == null)
                return new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "account does not exist", response);
            if (user.getGroup().getId() != 3 && user.getGroup().getId() != 1)
                return new ErrorHttpResponse(false, 400, "NO_PERMISSION", "Account has no permission to access admin site", response);

            // prepare data for success http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("group", user.getGroup().getName());

            return new SuccessHttpResponse(true, 200, "Access allowed", response, rootNode);
        } catch (Exception error) {
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", response);
        }
    }


    public HttpResponse verifyAccToActivate(User userDb, String password, HttpServletResponse res) {

        if (userDb == null) {
            HttpResponse response = new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "Account does not exist",
                    res);
            return response;
        }

        if (!bCryptPasswordEncoder.matches(password, userDb.getPassword())) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "INCORRECT_PASSWORD", "Password is incorrect",
                    res);
            return response;
        }

        // Check if user is activated
        if (userDb.getActivated() == 1) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "ACCOUNT_ALREADY_ACTIVATED",
                    "Your account is already activated", res);
            return response;
        }

        return new SuccessHttpResponse(true, 200, "", null, null);
    }

    public HttpResponse adminSeachUser(String keyword, int page, int limit, HttpServletResponse res) {
        try {
            Pageable pageable = PageRequest.of(page, limit);
            List<User> users = userRepository.findUserByKeyword(keyword.toLowerCase(), pageable);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            List<UserNonSecuredInfo> userNonSecuredInfos = users.stream().map(UserNonSecuredInfo::new).collect(Collectors.toList());
            JsonNode node = mapper.valueToTree(userNonSecuredInfos);
            ((ObjectNode) rootNode).put("users", node);
            ((ObjectNode) rootNode).put("totalRecordNumber", userRepository.countUserByKeyword(keyword));

            return new SuccessHttpResponse(true, 200, "User search result", res, rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }

    }

    public HttpResponse logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getHeader("userId"));
            User user = userRepository.findById(id);

            String deviceToken = request.getHeader("deviceToken");

            AuthenticationToken userToken = authenticationTokenRepository.findByUserIdAndDeviceToken(id, deviceToken);
            authenticationTokenRepository.delete(userToken);

            return new SuccessHttpResponse(true, 200, "Successfully logout", response, null);
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse res = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", response);
            return res;
        }
    }

    public HttpResponse forgetPasswordRequest(User user, HttpServletResponse res) {
        try {
            String email = user.getEmail();

            User userExist = userRepository.findByEmail(email);

            if (userExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "Account does not exist",
                        res);
                return response;
            }

            String confirmationToken = TokenGenerating.getRandomNumberString();
            userExist.setPwConfirmationToken(confirmationToken);
            userExist.setPwConfirmationTokenExpiry(datetime(3));

            userRepository.save(userExist);

            // send email with confirmation code
//            Email.sesConfirmationPwChangeEmail(userExist, emailService);
            emailObj.confirmationPwChangeEmail(userExist);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("email", user.getEmail());

            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "A password change confirmation email has been sent to your email : " + user.getEmail(), res,
                    rootNode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse forgetPasswordVerify(User user, HttpServletResponse res) {
        String email = user.getEmail();
        String pwConfirmationToken = user.getPwConfirmationToken();

        User userExist = userRepository.findByEmail(email);
        if (userExist == null) {
            HttpResponse response = new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "Account does not exist",
                    res);
            return response;
        }

        String pwConfirmationTokenExist = userExist.getPwConfirmationToken();
        if (!pwConfirmationToken.equals(pwConfirmationTokenExist)) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "INVALID_CONFIRMATION_TOKEN",
                    "The confirmation token is invalid", res);
            return response;
        }

        String expiryTimeString = userExist.getPwConfirmationTokenExpiry();
        String currentTimeString = datetime(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expiryTime;
        Date currentTime;

        try {
            expiryTime = sdf.parse(expiryTimeString);
            currentTime = sdf.parse(currentTimeString);
        } catch (ParseException e) {
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }

        if (expiryTime.compareTo(currentTime) < 0) {
            HttpResponse response = new ErrorHttpResponse(false, 400, "TOKEN_EXPIRED",
                    "The confirmation token is expired", res);
            return response;
        }

        return new SuccessHttpResponse(true, 200, "Successfully verify confirmation token", null, null);
    }

    public HttpResponse changePasswordWhenForget(User user, HttpServletRequest req, HttpServletResponse res) {
        try {
            String email = user.getEmail();
            String pwConfirmationToken = user.getPwConfirmationToken();

            if (user.getPassword() == null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "PASSWORD_EMPTY", "Password is empty", res);
                return response;
            }

            if (user.getEmail() == null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "EMAIL_EMPTY", "Email is empty", res);
                return response;
            }

            if (user.getPwConfirmationToken() == null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "CONFIRMATION_TOKEN_EMPTY",
                        "Password confirmation token is null", res);
                return response;
            }

            User userExist = userRepository.findByEmail(email);
            if (userExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "Account does not exist",
                        res);
                return response;
            }

            String pwConfirmationTokenExist = userExist.getPwConfirmationToken();
            if (!pwConfirmationToken.equals(pwConfirmationTokenExist)) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "INVALID_CONFIRMATION_TOKEN",
                        "The confirmation token is invalid", res);
                return response;
            }

            String expiryTimeString = userExist.getPwConfirmationTokenExpiry();
            String currentTimeString = datetime(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date expiryTime;
            Date currentTime;

            try {
                expiryTime = sdf.parse(expiryTimeString);
                currentTime = sdf.parse(currentTimeString);
            } catch (ParseException e) {
                HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
                return response;
            }

            if (expiryTime.compareTo(currentTime) < 0) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "TOKEN_EXPIRED",
                        "The confirmation token is expired", res);
                return response;
            }

            Zxcvbn passwordCheck = new Zxcvbn();
            Strength strength = passwordCheck.measure((String) user.getPassword());

            if (strength.getScore() < 1) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "PASSWORD_TOO_WEAK", "Password is too weak",
                        res);
                return response;
            }

            String newPwHash = bCryptPasswordEncoder.encode(user.getPassword());
            userExist.setPassword(newPwHash);

            userRepository.save(userExist);

            //delete all authenticationToken of all devices once password change
            authenticationTokenRepository.deleteByUserId(userExist.getId());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("email", user.getEmail());

            return new SuccessHttpResponse(true, 200, "Successfully change password", res, rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse getUserChatInfo(HttpServletRequest req, HttpServletResponse res) {
        try {
            int id = Integer.parseInt(req.getHeader("userId"));
            User user = userRepository.findById(id);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            return new SuccessHttpResponse(true, 200, "UserChat info", res, rootNode);

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }


    public HttpResponse getAllUserWithBasicInfo(Integer page, Integer limit, HttpServletResponse res) {
        try {
            Pageable pageable = PageRequest.of(page, limit);
            List<User> users = userRepository.findAllWithBasicInfo(pageable);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            List<UserNonSecuredInfo> userNonSecuredInfos = new ArrayList<>();

            if(!users.isEmpty())
                userNonSecuredInfos = users.stream().map(UserNonSecuredInfo::new).collect(Collectors.toList());

            JsonNode node = mapper.valueToTree(userNonSecuredInfos);

            ((ObjectNode) rootNode).put("totalRecordNumber", userRepository.countAll());
            ((ObjectNode) rootNode).put("users", node);

            return new SuccessHttpResponse(true, 200, "Users info", res, rootNode);

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }

    public HttpResponse requestChangeGroup(UserGroupChange userGroupChange, HttpServletRequest req, HttpServletResponse res){
        try {
            User user = userRepository.findById(Integer.parseInt(req.getHeader("userId")));

            ResponseBody responseBody = new ResponseBody();
            responseBody.put("email", user.getEmail());

            if (userGroupChange.getGroupId() == null) {
                return new ErrorHttpResponse(false, 400, "GROUP_ID_NOT_EXIST", "Group Id does not exist", res);
            }

            if(userGroupChange.getGroupId() == 4) {
                Group group = groupRepository.findById(4);
                if (group == null)
                    return new ErrorHttpResponse(false, 404, "GROUP_NOT_EXIST", "Group does not exist", res);
                user.setGroup(group);

                return new SuccessHttpResponse(true, 200,
                        "Your account has been upgraded into Recruiter level", res, responseBody.get());
            }

//            Email.sendGroupChangeRequestConfirmation(userGroupChange, user, emailService);
            emailObj.sendGmailGroupChangeRequestConfirmation(userGroupChange, user);

            return new SuccessHttpResponse(true, 200,
                    "Your upgrade account request has been received and request information has been sent to your email: " + user.getEmail(), res, responseBody.get());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }

    public HttpResponse authenticate(HttpServletRequest req, HttpServletResponse res) {
        User user = userRepository.findById(Integer.parseInt(req.getHeader("userId")));


        return new SuccessHttpResponse(true, 200, "Successfully authenticate", res, null);
    }
}
