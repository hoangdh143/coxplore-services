package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.service.AdminDeleteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@RestController
public class AdminDeleteUserController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDeleteUserController.class);

    private AdminDeleteUserService adminDeleteUserService;

    @Autowired
    public AdminDeleteUserController(AdminDeleteUserService adminDeleteUserService) {
        this.adminDeleteUserService = adminDeleteUserService;
    }

    @PreAuthorize("hasAccessRight('module_user', 'delete')")
    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public HttpResponse deleteUser(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        logger.info("Delete User id {}", id);

        HttpResponse response = adminDeleteUserService.deleteById(id, res);
        logRequestResponse(logger, req, response);
        return response;
    }
}
