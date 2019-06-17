package co.pailab.lime.service;

import co.pailab.lime.helper.Email;
import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpRequest.HttpRequest;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.Group;
import co.pailab.lime.model.User;
import co.pailab.lime.model.UserNonSecuredInfo;
import co.pailab.lime.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AdminUpdateUserService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private AuthenticationTokenRepository authenticationTokenRepository;
    private EmailService emailService;
    private Email emailObj;
    public AdminUpdateUserService(
            UserRepository userRepository,
            GroupRepository groupRepository,
            AuthenticationTokenRepository authenticationTokenRepository,
            EmailService emailService,
            Email emailObj
    ) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.authenticationTokenRepository = authenticationTokenRepository;
        this.emailService = emailService;
        this.emailObj = emailObj;
    }

    public HttpResponse adminUpdate(int userId, UserNonSecuredInfo user, HttpServletRequest request, HttpServletResponse response) {
        try {
            User userExist = userRepository.findById(userId);

            // check if user exist via id
            if (userExist == null)
                return new ErrorHttpResponse(false, 404, "ACCOUNT_NOT_EXIST", "account does not exist", response);

            String oldGroupName = userExist.getGroup().getName();
            String newGroupName = null;
            Integer oldGroupId = userExist.getGroup().getId();
            Integer newGroupId = null;
            Boolean newGroup = false;
            if (user.getGroupId() != 0) {
                if (userExist.getActivated() == 0)
                    return new ErrorHttpResponse(false, 400, "INACTIVE_USER", "Cannot change group for inactivated user", response);

                Group group = groupRepository.findById(user.getGroupId());
                if (group == null)
                    return new ErrorHttpResponse(false, 404, "GROUP_NOT_EXIST", "Group does not exist", response);

                userExist.setGroup(group);
                newGroup = true;
            }

            if (user.getActivatedStatus() < 2)
                userExist.setActivated(user.getActivatedStatus());

            // update user into db
            userRepository.save(userExist);

            //delete all authenticationToken of other devices once account inactive change
            if (userExist.getActivated() == 0)
                authenticationTokenRepository.deleteByUserId(userId);
            if(oldGroupId != user.getGroupId()){
                newGroupName = userExist.getGroup().getName();
                newGroupId = userExist.getGroup().getId();
                emailObj.sendGmailGroupChangeApproval(userExist, oldGroupName, newGroupName);
            }

            // prepare data for http response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            UserNonSecuredInfo userNonSecuredInfo = new UserNonSecuredInfo(userExist);
            JsonNode node = mapper.valueToTree(userNonSecuredInfo);
            ((ObjectNode) rootNode).put("user", node);

            // success http response
            return new SuccessHttpResponse(true, 200, "Account has been successfully updated", response,
                    rootNode);

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", response);
        }
    }
}
