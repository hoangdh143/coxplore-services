package co.pailab.lime.service;

import co.pailab.lime.helper.ErrorHttpResponse;
import co.pailab.lime.helper.HttpResponse;
import co.pailab.lime.helper.SuccessHttpResponse;
import co.pailab.lime.model.Group;
import co.pailab.lime.model.GroupBasicInfo;
import co.pailab.lime.model.Module;
import co.pailab.lime.model.ModulePermission;
import co.pailab.lime.repository.GroupModulePermissionRepository;
import co.pailab.lime.repository.GroupRepository;
import co.pailab.lime.repository.ModuleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private GroupRepository groupRepository;
    private GroupModulePermissionRepository groupModulePermissionRepository;
    private ModuleRepository moduleRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository,
                        GroupModulePermissionRepository groupModulePermissionRepository,
                        ModuleRepository moduleRepository) {
        this.groupRepository = groupRepository;
        this.groupModulePermissionRepository = groupModulePermissionRepository;
        this.moduleRepository = moduleRepository;
    }

    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @SuppressWarnings("deprecation")
    public HttpResponse create(Group group, HttpServletResponse res) {
        try {
            Group groupDb = findByName(group.getName());

            // check if group exists
            if (groupDb != null) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "GROUP_ALREADY_EXISTED",
                        "Group " + group.getName() + " has been already existed!", res);
                return response;
            }

            // save new group into db
            groupDb = groupRepository.save(group);

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(groupDb);
            ((ObjectNode) rootNode).put("group", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "Group " + groupDb.getName() + " has been successfully created", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse update(int id, Group group, HttpServletResponse res) {
        try {
            Group groupExist = groupRepository.findById(id);

            // check if group exists
            if (groupExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "GROUP_NOT_FOUND",
                        "Group " + group.getName() + " not found", res);
                return response;
            }

            // set values for object groupExist
            if (group.getName() != null)
                groupExist.setName(group.getName());
            if (group.getDescription() != null)
                groupExist.setDescription(group.getDescription());

            // save updated info of group into db
            Group groupDb = groupRepository.save(groupExist);

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(groupDb);
            ((ObjectNode) rootNode).put("group", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "Group " + groupDb.getName() + " has been successfully updated", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse delete(int id, HttpServletResponse res) {
        try {
            Group groupExist = groupRepository.findById(id);

            // check if group exists
            if (groupExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "GROUP_NOT_FOUND", "Group not found", res);
                return response;
            }

            // not allow delete admin group
            if (groupExist.getName().equals("admin")) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "CANNOT_DELETE_ADMIN_GROUP",
                        "Cannot delete admin group", res);
                return response;
            }

            // check if any user belongs to the group
            if (groupExist.getUsers().size() > 0) {
                HttpResponse response = new ErrorHttpResponse(false, 400, "CANNOT_DELETE_GROUP_OF_EXIST_USER",
                        "Cannot delete group of exist user", res);
                return response;
            }

            // delete all permissions of the group
            groupModulePermissionRepository.deleteByGroupId(id);

            // delete group
            groupRepository.delete(groupExist);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200,
                    "Group " + groupExist.getName() + " has been successfully deleted", res, null);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    @SuppressWarnings("deprecation")
    public HttpResponse findById(int id, HttpServletResponse res) {
        try {
            Group groupExist = groupRepository.findById(id);

            // check if group exists
            if (groupExist == null) {
                HttpResponse response = new ErrorHttpResponse(false, 404, "GROUP_NOT_FOUND", "Group not found", res);
                return response;
            }

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            JsonNode node = mapper.valueToTree(groupExist);
            ((ObjectNode) rootNode).put("group", node);

            // httpResponse
            HttpResponse response = new SuccessHttpResponse(true, 200, "Group Detail Info", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }

    public HttpResponse findAll(int page, int limit, HttpServletResponse res) {
        try {
            Pageable pageable = PageRequest.of(page, limit);
            List<Group> groups = groupRepository.findAllByOrderById(pageable);

            // prepare json data for response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            List<GroupBasicInfo> groupBasicInfos = groups.stream().map(group -> {
                List<Integer> moduleIds = groupModulePermissionRepository.listModuleOfAGroup(group.getId());
                List<ModulePermission> modulePermissions = groupModulePermissionRepository.listModuleOfAGroup(group.getId()).stream()
                        .map(moduleId -> {
                            Module module = moduleRepository.findById(moduleId);
                            List<String> permissions = groupModulePermissionRepository.findByGroupIdAndModuleId(group.getId(), moduleId)
                                    .stream().map(groupModulePermission -> groupModulePermission.getPermission()).collect(Collectors.toList());
                            return new ModulePermission(module, permissions);
                        }).collect(Collectors.toList());

                return new GroupBasicInfo(group, modulePermissions);
            }).collect(Collectors.toList());
            JsonNode node = mapper.valueToTree(groupBasicInfos);
            ((ObjectNode) rootNode).put("groups", node);
            ((ObjectNode) rootNode).put("totalRecordNumber", groupRepository.countAll());

            // httpResponsea
            HttpResponse response = new SuccessHttpResponse(true, 200, "Group Detail Info", res, rootNode);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            HttpResponse response = new ErrorHttpResponse(false, 500, "SYSTEM_ERROR", "An error occurs", res);
            return response;
        }
    }
}
