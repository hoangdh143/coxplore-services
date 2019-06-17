package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ValidationErrorHttpResponse;
import co.pailab.lime.helper.ValidationErrorProcess;
import co.pailab.lime.model.Group;
import co.pailab.lime.service.GroupService;
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
@RequestMapping("/groups")
public class GroupController {
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PreAuthorize("hasAccessRight('module_group', 'create')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public HttpResponse create(@Valid @RequestBody Group group, BindingResult bindingResult, HttpServletRequest req,
                               HttpServletResponse res) {
        logger.info("Create new Group");

        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);
        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_GROUP_INFO",
                    "Invalid field(s)", res, bindingError);
            logger.warn("Invalid Group Info");
            logRequestResponse(logger, req, response);
            return response;
        }

        HttpResponse response = groupService.create(group, res);
        logRequestResponse(logger, req, response);
        return response;
    }

    @PreAuthorize("hasAccessRight('module_group', 'update')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public HttpResponse update(@PathVariable("id") int id, @Valid @RequestBody Group group, BindingResult bindingResult,
                               HttpServletRequest req, HttpServletResponse res) {
        logger.info("Update Group Id: {}", id);

        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);
        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_GROUP_INFO",
                    "Invalid field(s)", res, bindingError);
            logger.warn("Invalid Group Info");
            logRequestResponse(logger, req, response);
            return response;
        }

        HttpResponse response = groupService.update(id, group, res);
        logRequestResponse(logger, req, response);
        return response;

    }

    @PreAuthorize("hasAccessRight('module_group', 'delete')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public HttpResponse delete(@PathVariable("id") int id, HttpServletRequest req,
                               HttpServletResponse res) {
        HttpResponse response = groupService.delete(id, res);
        logger.info("Delete Group Id: {}", id);
        logRequestResponse(logger, req, response);
        return response;
    }

    @PreAuthorize("hasAccessRight('module_group', 'read')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse findById(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = groupService.findById(id, res);
        logger.info("Find Group by Id: {}", id);
        logRequestResponse(logger, req, response);
        return response;
    }

    @PreAuthorize("hasAccessRight('module_group', 'read')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse findAll(@RequestParam int page, @RequestParam int limit, HttpServletRequest req, HttpServletResponse res) {
        int userId = Integer.parseInt(req.getHeader("userId"));
        HttpResponse response = groupService.findAll(page, limit, res);
        logger.info("Admin Find All Group - admin id", userId);
        logRequestResponse(logger, req, response);
        return response;
    }
}
