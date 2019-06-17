package co.pailab.lime.model;

public class UserGroup extends User {
    private int groupId;

    public UserGroup() {

    }

    public UserGroup(int groupId) {
        super();
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
