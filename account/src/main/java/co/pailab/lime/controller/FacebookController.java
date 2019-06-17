package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ValidationErrorHttpResponse;
import co.pailab.lime.helper.ValidationErrorProcess;
import co.pailab.lime.model.SocialAccessToken;
import co.pailab.lime.service.FacebookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@RestController
public class FacebookController {
    private static final Logger logger = LoggerFactory.getLogger(FacebookController.class);

    private FacebookService facebookService;

    @Autowired
    public FacebookController(FacebookService facebookService) {
        this.facebookService = facebookService;
    }

    @RequestMapping(value = "/login/facebook", method = RequestMethod.POST, produces = "application/json")
    public HttpResponse processRegistration(@Valid @RequestBody SocialAccessToken socialAccessToken, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse res) throws JsonProcessingException, BindException {
        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);
        logger.info("New Facebook Login request");

        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_INFO", "Invalid field(s)", res, bindingError);
            logRequestResponse(logger, request, response);
            return response;
        }

        try {
            HttpResponse response = facebookService.registerAndLogin(socialAccessToken.getAccessToken(), socialAccessToken.getIntroduceCode(), request, res);
            logRequestResponse(logger, request, response);
            return response;
        } catch (Exception e) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res, bindingError);
            logger.warn("Error in Facebook login process. Details: {}", e);
            logRequestResponse(logger, request, response);
            return response;
        }

    }

    @GetMapping("/createFacebookAuthorization")
    public String createFacebookAuthorization(HttpServletRequest request) {
        String facebookAuthorizationURL = facebookService.createFacebookAuthorizationURL();
        logger.info("Create Facebook authorization");
        logRequestResponse(logger, request, facebookAuthorizationURL);
        return facebookAuthorizationURL;
    }

    @GetMapping("/facebook")
    public HttpResponse createFacebookAccessToken(@RequestParam("code") String code, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = facebookService.createFacebookAccessToken(code, res);
        logger.info("Create Facebook access token");
        logRequestResponse(logger, req, response);
        return response;
    }

    @GetMapping("/getFacebookInfo")
    public User getNameResponse(HttpServletRequest req) {
        User facebookInfo = facebookService.getInfo();
        logger.info("Get Facebook info");
        logRequestResponse(logger, req, facebookInfo.toString());
        return facebookInfo;
    }
}
