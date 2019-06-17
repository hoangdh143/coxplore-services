package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ValidationErrorHttpResponse;
import co.pailab.lime.helper.ValidationErrorProcess;
import co.pailab.lime.model.GroupModulePermissionToCreate;
import co.pailab.lime.service.GroupModulePermissionService;
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
@RequestMapping("/permissions")
public class GroupModulePermissionController {
    private static final Logger logger = LoggerFactory.getLogger(GroupModulePermissionController.class);

    private GroupModulePermissionService groupModulePermissionService;

    @Autowired
    public GroupModulePermissionController(GroupModulePermissionService groupModulePermissionService) {
        this.groupModulePermissionService = groupModulePermissionService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_permission', 'create') AND hasAccessRight('module_module', 'read') AND hasAccessRight('module_group', 'read')")
    public HttpResponse create(@Valid @RequestBody GroupModulePermissionToCreate groupModulePermission,
                               BindingResult bindingResult, HttpServletRequest request, HttpServletResponse res) {
        logger.info("Create new Group module permission");
        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);

        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_USER_INFO", "Invalid field(s)",
                    res, bindingError);
            logger.warn("Invalid User Info");
            logRequestResponse(logger, request, response);
            return response;
        }

        HttpResponse response = groupModulePermissionService.create(groupModulePermission, res);
        logRequestResponse(logger, request, response);
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_permission', 'read') AND hasAccessRight('module_module', 'read') AND hasAccessRight('module_group', 'read')")
    public HttpResponse findById(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = groupModulePermissionService.findById(id, res);
        logger.info("Find Group modul permission by Id: {}", id);
        logRequestResponse(logger, req, response);
        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @PreAuthorize("hasAccessRight('module_permission', 'delete') AND hasAccessRight('module_module', 'read') AND hasAccessRight('module_group', 'read')")
    public HttpResponse deleteById(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = groupModulePermissionService.deleteById(id, res);
        logger.info("Delete Group module permission Id: {}", id);
        logRequestResponse(logger, req, response);
        return response;
    }
}
