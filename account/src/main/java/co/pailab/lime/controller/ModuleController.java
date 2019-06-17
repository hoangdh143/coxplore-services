package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.ValidationErrorHttpResponse;
import co.pailab.lime.helper.ValidationErrorProcess;
import co.pailab.lime.model.Module;
import co.pailab.lime.service.ModuleService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static co.pailab.lime.helper.CustomLogger.logRequestResponse;

@RestController
@RequestMapping("/modules")

public class ModuleController {
    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);

    private ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PreAuthorize("hasAccessRight('module_module', 'create')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public HttpResponse create(@Valid @RequestBody Module module, BindingResult bindingResult, HttpServletRequest req,
                               HttpServletResponse res) {
        logger.info("Create new Module");

        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);
        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_MODULE_INFO",
                    "Invalid field(s)", res, bindingError);
            logger.warn("Invalid Module Info");
            logRequestResponse(logger, req, response);
            return response;
        }

        HttpResponse response = moduleService.create(module, res);
        logRequestResponse(logger, req, response);
        return response;

    }

    @PreAuthorize("hasAccessRight('module_module', 'update')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public HttpResponse update(@PathVariable("id") int id, @Valid @RequestBody Module module,
                               BindingResult bindingResult, HttpServletRequest req, HttpServletResponse res) {
        logger.info("Update Module Id: {}", id);

        JsonNode bindingError = ValidationErrorProcess.run(bindingResult);
        if (bindingError != null) {
            HttpResponse response = new ValidationErrorHttpResponse(false, 400, "INVALID_MODULE_INFO",
                    "Invalid field(s)", res, bindingError);
            logger.warn("Invalid Module Info");
            logRequestResponse(logger, req, response);
            return response;
        }

        HttpResponse response = moduleService.update(id, module, res);
        logRequestResponse(logger, req, response);
        return response;

    }

    @PreAuthorize("hasAccessRight('module_module', 'delete')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public HttpResponse update(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = moduleService.delete(id, res);
        logger.info("Delete module Id: {}", id);
        logRequestResponse(logger, req, response);
        return response;

    }

    @PreAuthorize("hasAccessRight('module_module', 'read')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse findById(@PathVariable("id") int id, HttpServletRequest req, HttpServletResponse res) {
        HttpResponse response = moduleService.findById(id, res);
        logger.info("Find Module by id: {}", id);
        logRequestResponse(logger, req, response);
        return response;
    }
}
