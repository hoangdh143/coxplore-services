package co.pailab.lime.controller.storage;

import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.model.storage.Storage;
import co.pailab.lime.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/storages")
public class StorageController {
    private StorageService storageService;
    private String s3Dir = "/vitae/general";
    private String s3Prefix = "vitae/general";

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PreAuthorize("hasAccessRight('module_storage', 'create')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public HttpResponse storeMultiFile(@RequestBody Storage storage, HttpServletRequest req, HttpServletResponse res) {
        return storageService.storeMultiFile(storage.getFiles(), res, s3Dir);
    }

    @PreAuthorize("hasAccessRight('module_storage', 'read')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public HttpResponse getAllFile(@RequestParam("start") int start, @RequestParam("limit") int limit, Storage storage, HttpServletRequest req, HttpServletResponse res) {
        return storageService.getAllFile(res, s3Prefix, start, limit);
    }
}
