package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UserProfileController {
    private UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @RequestMapping(value = "/accounts/profile_status", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse profileCompleteStatus(HttpServletRequest req, HttpServletResponse res) {
        return userProfileService.profileCompleteStatus(req, res);
    }
}
