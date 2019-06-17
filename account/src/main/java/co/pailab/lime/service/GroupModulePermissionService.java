package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.Group;
import co.pailab.lime.model.GroupModulePermission;
import co.pailab.lime.model.GroupModulePermissionToCreate;
import co.pailab.lime.model.Module;
import co.pailab.lime.repository.GroupModulePermissionRepository;
import co.pailab.lime.repository.GroupRepository;
import co.pailab.lime.repository.ModuleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class GroupModulePermissionService {
    private GroupModulePermissionRepository groupModulePermissionRepository;
    private GroupRepository groupRepository;
    private ModuleRepository moduleRepository;

    @Autowired
    public GroupModulePermissionService(GroupModulePermissionRepository groupModulePermissionRepository, GroupRepository groupRepository, ModuleRepository moduleRepository) {
        this.groupModulePermissionRepository = groupModulePermissionRepository;
        this.groupRepository = groupRepository;
        this.moduleRepository = moduleRepository;
    }

    public HttpResponse findById(int id, HttpServletResponse res) {
        try {
            GroupModulePermission groupModulePermission = groupModulePermissionRepository.findById(id);
            if (groupModulePermission == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_PERMISSION", "Cannot find permission of the group", res);
                return response;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("groupId", groupModulePermission.getGroup().getId());
            ((ObjectNode) rootNode).put("groupName", groupModulePermission.getGroup().getName());
            ((ObjectNode) rootNode).put("moduleName", groupModulePermission.getModule().getName());
            ((ObjectNode) rootNode).put("permission", groupModulePermission.getPermission());

            HttpResponse response = new SuccessHttpResponse(true, 200, "Permission info", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse create(GroupModulePermissionToCreate groupModulePermission, HttpServletResponse res) {
        try {
            int groupId = groupModulePermission.getGroupId();
            Group group = groupRepository.findById(groupId);
            if (group == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_GROUP", "Cannot find group", res);
                return response;
            }

            int moduleId = groupModulePermission.getModuleId();
            Module module = moduleRepository.findById(moduleId);
            if (module == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_MODULE", "Cannot find module", res);
                return response;
            }

            String permission = groupModulePermission.getPermission();

            GroupModulePermission groupModulePermissionDb;
            groupModulePermissionDb = groupModulePermissionRepository.findByGroupIdAndModuleIdAndPermission(groupId, moduleId, permission);

            if (groupModulePermissionDb != null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "PERMISSION_ALREADY_EXIST", "Permission has been already existed", res);
                return response;
            }

            groupModulePermissionDb = new GroupModulePermission();

            //set values for object permission before save to db
            groupModulePermissionDb.setGroup(group);
            groupModulePermissionDb.setModule(module);
            groupModulePermissionDb.setPermission(permission);

            //save permission to db
            groupModulePermissionDb = groupModulePermissionRepository.save(groupModulePermissionDb);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("id", groupModulePermissionDb.getId());
            ((ObjectNode) rootNode).put("groupId", groupId);
            ((ObjectNode) rootNode).put("groupName", group.getName());
            ((ObjectNode) rootNode).put("moduleName", module.getName());
            ((ObjectNode) rootNode).put("permission", permission);

            HttpResponse response = new SuccessHttpResponse(true, 200, "Permission has been successfully created", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse deleteById(int id, HttpServletResponse res) {
        try {

            GroupModulePermission groupModulePermissionDb = groupModulePermissionRepository.findById(id);

            if (groupModulePermissionDb == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "CANNOT_FIND_PERMISSION", "Cannot find permission", res);
                return response;
            }

            groupModulePermissionRepository.delete(groupModulePermissionDb);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();

            ((ObjectNode) rootNode).put("groupId", groupModulePermissionDb.getGroup().getId());
            ((ObjectNode) rootNode).put("groupName", groupModulePermissionDb.getGroup().getName());
            ((ObjectNode) rootNode).put("moduleName", groupModulePermissionDb.getModule().getName());
            ((ObjectNode) rootNode).put("permission", groupModulePermissionDb.getPermission());

            HttpResponse response = new SuccessHttpResponse(true, 200, "Permission has been successfully deleted", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }
}
