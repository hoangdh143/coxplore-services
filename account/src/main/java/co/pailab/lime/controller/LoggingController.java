package co.pailab.lime.controller;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/logs")
public class LoggingController {
    private LoggingService loggingService;

    @Autowired
    public LoggingController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @PreAuthorize("hasAccessRight('module_logging', 'read')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse getLogs(HttpServletRequest req, HttpServletResponse res) {
        return loggingService.getLast1000Lines(res);
    }

    @PreAuthorize("hasAccessRight('module_logging', 'read')")
    @RequestMapping(value = "/traffic", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse getRequestResponseOnly(HttpServletRequest req, HttpServletResponse res) {
        return loggingService.getRequestResponseOnly(res);
    }

    @PreAuthorize("hasAccessRight('module_logging', 'read')")
    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        return loggingService.getFullLogs();
    }

}
