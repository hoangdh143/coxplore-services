package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.User;
import co.pailab.lime.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserProfileService {
    private UserRepository userRepository;

    @Value("${numberGenUserName}")
    private String numberGenUserName;

    @Value("${userNamePrefix}")
    private String userNamePrefix;

    @Value("${totalProfileFields}")
    private String totalProfileFields;

    @Autowired
    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HttpResponse profileCompleteStatus(HttpServletRequest req, HttpServletResponse res) {
        try {
            int id = Integer.parseInt(req.getHeader("userId"));
            User user = userRepository.findById(id);
            List<Integer> completedProfiles = new ArrayList();
            List<Integer> incompletedProfiles = new ArrayList();
            String defaultUserName = userNamePrefix + (Integer.parseInt(numberGenUserName) + user.getId());
            if (user != null) {
//			1: email,
//			2: username,
//			3: dateOfBirth,
//			4: firstName,
//			5: lastName,
//			6: gender,
//			7: weight,
//			8: height,
//			9: education,
//			10: address,
//			11: avatar,
//			12: mbtiTest,
//			13: careerPick
                completedProfiles.add(1);
                if (user.getUsername().equals(defaultUserName) || user.getUsername().trim().equals(""))
                    incompletedProfiles.add(2);
                else completedProfiles.add(2);
                if (user.getDateOfBirth() == null || user.getDateOfBirth().trim().equals(""))
                    incompletedProfiles.add(3);
                else completedProfiles.add(3);
                if (user.getFirstName() == null || user.getFirstName().trim().equals("")) incompletedProfiles.add(4);
                else completedProfiles.add(4);
                if (user.getLastName() == null || user.getLastName().equals("")) incompletedProfiles.add(5);
                else completedProfiles.add(5);
                if (user.getGender() == 0) incompletedProfiles.add(6);
                else completedProfiles.add(6);
                if (user.getWeight() == 0) incompletedProfiles.add(7);
                else completedProfiles.add(7);
                if (user.getHeight() == 0) incompletedProfiles.add(8);
                else completedProfiles.add(9);
                if (user.getAddress() == null || user.getDateOfBirth().trim().equals("")) incompletedProfiles.add(10);
                else completedProfiles.add(10);
                if (user.getAvatar() == null || user.getAvatar().trim().equals("")) incompletedProfiles.add(11);
                else completedProfiles.add(11);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("totalProfileFields", totalProfileFields);

            JsonNode node = mapper.valueToTree(completedProfiles);
            ((ObjectNode) rootNode).put("completedProfileFields", node);

            JsonNode node1 = mapper.valueToTree(incompletedProfiles);
            ((ObjectNode) rootNode).put("incompletedProfileFields", node1);


            return new SuccessHttpResponse(true, 200, "Profile completion status", res, rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
        }

    }

}
