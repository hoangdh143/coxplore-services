package co.pailab.lime.model;

public class GroupModulePermissionToCreate extends GroupModulePermission {
    private int groupId;
    private int moduleId;

    public GroupModulePermissionToCreate() {
    }

    public GroupModulePermissionToCreate(int groupId, int moduleId) {
        this.groupId = groupId;
        this.moduleId = moduleId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }
}
