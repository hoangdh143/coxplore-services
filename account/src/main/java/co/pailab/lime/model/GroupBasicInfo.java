package co.pailab.lime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class GroupBasicInfo extends Group {
    private List<ModulePermission> modulePermissions;

    public GroupBasicInfo() {
        super();
    }

    public GroupBasicInfo(Group group, List<ModulePermission> modulePermissions) {
        super(group.getId(), group.getName(), group.getDescription());
        this.modulePermissions = modulePermissions;
    }

    @JsonIgnore
    public List<User> getUsers() {
        return users;
    }

    @JsonIgnore
    public List<GroupModulePermission> getGroupModulePermissions() {
        return groupModulePermissions;
    }

    public List<ModulePermission> getModulePermissions() {
        return modulePermissions;
    }

    public void setModulePermissions(List<ModulePermission> modulePermissions) {
        this.modulePermissions = modulePermissions;
    }
}
