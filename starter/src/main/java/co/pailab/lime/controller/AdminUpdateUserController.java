package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ValidationErrorHttpResponse;
import co.pailab.lime.helper.ValidationErrorProcess;
import co.pailab.lime.model.UserNonSecuredInfo;
import co.pailab.lime.service.AdminUpdateUserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@RestController
public class AdminUpdateUserController {
    private static final Logger logger = LoggerFactory.getLogger(UserUpdateController.class);
    private AdminUpdateUserService adminUpdateUserService;

    @Autowired
    public AdminUpdateUserController(AdminUpdateUserService adminUpdateUserService) {
        this.adminUpdateUserService = adminUpdateUserService;
    }

    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_user', 'update')")
    public HttpResponse adminUpdate(@PathVariable("id") int id, @Valid @RequestBody UserNonSecuredInfo user, BindingResult bindingResult, HttpServletRequest req,
                                    HttpServletResponse res) throws Exception {
        logger.info("Admin update User Info - admin Id: {}", req.getHeader("userId"));
        // catch if any binding error
        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);

        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_USER_INFO", "Invalid field(s)",
                    res, bindingError);
            logger.warn("Invalid User Info");
            logRequestResponse(logger, req, response);
            return response;
        }

        HttpResponse response = adminUpdateUserService.adminUpdate(id, user, req, res);
        logRequestResponse(logger, req, response);
        return response;
    }
}
