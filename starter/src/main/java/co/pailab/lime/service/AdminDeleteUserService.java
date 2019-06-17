package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.User;
import co.pailab.lime.model.UserNonSecuredInfo;
import co.pailab.lime.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class AdminDeleteUserService {
    static final int ADMIN_GROUP_ID = 1;
    private UserRepository userRepository;


    public AdminDeleteUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("deprecation")
    public HttpResponse deleteById(int userId, HttpServletResponse res) {
        try {
            User user = userRepository.findById(userId);

            if (user == null) {
                return new ErrorHttpResponse(false, 404, "USER_NOT_FOUND", "User Id not found", res);
            }

            if (user.getGroup() != null && user.getGroup().getId() == ADMIN_GROUP_ID) {
                return new ErrorHttpResponse(false, 400, "CAN NOT DELETE AN ADMIN", "Can not delete an Admin", res);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(user);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);

            ((ObjectNode) rootNode).put("user", node);


            userRepository.deleteById(userId);

            return new SuccessHttpResponse(true, 200, "User info", res, rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }
    }
}
