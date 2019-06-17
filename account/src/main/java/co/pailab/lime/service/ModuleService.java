package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.GroupModulePermission;
import co.pailab.lime.model.Module;
import co.pailab.lime.repository.GroupModulePermissionRepository;
import co.pailab.lime.repository.ModuleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class ModuleService {
    private ModuleRepository moduleRepository;
    private GroupModulePermissionRepository groupModulePermissionRepository;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository,
                         GroupModulePermissionRepository groupModulePermissionRepository) {
        this.moduleRepository = moduleRepository;
        this.groupModulePermissionRepository = groupModulePermissionRepository;
    }

    public Module findByName(String name) {
        return moduleRepository.findByName(name);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse create(Module module, HttpServletResponse res) {
        try {
            Module moduleExist = findByName(module.getName());

            // check if module exists
            if (moduleExist != null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "MODULE_ALREADY_EXISTED",
                        "Module " + module.getName() + " has been already existed!", res);
                return response;
            }

            // save new module to db
            moduleExist = moduleRepository.save(module);

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(moduleExist);
            ((ObjectNode) rootNode).put("module", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "Module " + moduleExist.getName() + " has been successfully created", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse update(int id, Module module, HttpServletResponse res) {
        try {
            Module moduleExist = moduleRepository.findById(id);
            if (moduleExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "MODULE_NOT_FOUND",
                        "Module " + module.getName() + " not found", res);
                return response;
            }

            // set values for moduleExist
            if (module.getName() != null)
                moduleExist.setName(module.getName());
            if (module.getDescription() != null)
                moduleExist.setDescription(module.getDescription());

            // save new module to db
            moduleExist = moduleRepository.save(moduleExist);

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(moduleExist);
            ((ObjectNode) rootNode).put("module", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "Module " + moduleExist.getName() + " has been successfully updated", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse delete(int id, HttpServletResponse res) {
        try {
            Module moduleExist = moduleRepository.findById(id);

            // check if module exists
            if (moduleExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "GROUP_NOT_FOUND", "Group not found", res);
                return response;
            }

            // check if any permission of the module
            List<GroupModulePermission> groupModulePermissions = groupModulePermissionRepository.findByModuleId(id);
            if (groupModulePermissions.size() > 0) {
                HttpResponse response = new ErrorHttpResponse(false, 400,
                        "CANNOT_DELETE_MODULE_AUTHORIZED_FOR_EXIST_GROUP",
                        "Cannot delete module has been authorized for existed group", res);
                return response;
            }

            // delete module
            moduleRepository.delete(moduleExist);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200, "Module has been successfully deleted", res,
                    null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse findById(int id, HttpServletResponse res) {
        try {
            Module moduleExist = moduleRepository.findById(id);

            // check if module exists
            if (moduleExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "MODULE_NOT_FOUND", "Module not found", res);
                return response;
            }

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(moduleExist);
            ((ObjectNode) rootNode).put("module", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200, "Module Detail Info", res, rootNode);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }
}
