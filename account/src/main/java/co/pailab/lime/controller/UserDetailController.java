package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.service.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@RestController
public class UserDetailController {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailController.class);

    private UserService userService;

    @Autowired
    public UserDetailController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse findById(HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.findById(req, res);

        logger.info("User find User Detail by Id: {}", req.getHeader("userId"));
        logRequestResponse(logger, req, response);

        return response;
    }

    @RequestMapping(value = "/admin/accounts", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_user', 'read')")
    public HttpResponse getAll(@RequestParam int page, @RequestParam int limit, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.getAll(page, limit, res);

        logger.info("Admin list user: {}", req.getHeader("userId"));
        logRequestResponse(logger, req, response);

        return response;
    }

    @RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse adminFindUserById(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.adminFindUserById(id, res);

        logger.info("Admin find User Detail by Id: {}", id);
        logRequestResponse(logger, req, response);

        return response;
    }

    @PreAuthorize("hasAccessRight('module_user', 'read')")
    @RequestMapping(value = "/admin_access", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse checkAdminAccess(HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.checkAdminAccess(req, res);

        logger.info("Check admin access of user " + req.getHeader("userId"));
        logRequestResponse(logger, req, response);

        return response;
    }


    @RequestMapping(value = "/admin/accounts/search", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse search(@RequestParam String keyword, @RequestParam int page, @RequestParam int limit, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.adminSeachUser(keyword, page, limit, res);

        logger.info("Admin search user - adminId " + req.getHeader("userId"));
        logRequestResponse(logger, req, response);

        return response;
    }

    @RequestMapping(value = "/admin/accounts/basic", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_user', 'read')")
    public HttpResponse getAllWithBasicInfo(@RequestParam int page, @RequestParam int limit, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = userService.getAllUserWithBasicInfo(page, limit, res);

        logger.info("Admin list users with basic info");
        logRequestResponse(logger, req, response);

        return response;
    }
}
