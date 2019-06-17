package co.pailab.lime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable
public class UserNonSecuredInfo extends User {
    private int userTotalPointNumber;
    private int userRankNumber;
    private String groupName;
    private int groupId;
    private int activatedStatus;

    public UserNonSecuredInfo() {
    }

    public UserNonSecuredInfo(User user) {
        super(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getAvatar(), user.getPhone(), user.getAddress(), user.getDateOfBirth(), user.getWeight(),
                user.getHeight(), user.getGender(),
                user.getGroup(), user.getActivated());
    }

    public UserNonSecuredInfo(User user, Boolean rankRequired) {
        super(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getUsername(),
                user.getAvatar(), user.getPhone(), user.getAddress(), user.getDateOfBirth(), user.getWeight(),
                user.getHeight(), user.getGender(),
                user.getGroup(), user.getActivated());
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public int getActivated() {
        return activated;
    }

    @JsonIgnore
    public String getActivationTokenExpiry() {
        return activationTokenExpiry;
    }

    @JsonIgnore
    public String getActivationToken() {
        return activationToken;
    }

    @JsonIgnore
    public String getPwConfirmationToken() {
        return activationToken;
    }

    @JsonIgnore
    public String getPwConfirmationTokenExpiry() {
        return activationToken;
    }

    @JsonIgnore
    public Group getGroup() {
        return group;
    }

    @JsonIgnore
    public String getDeviceToken() {
        return deviceToken;
    }


    public int getUserTotalPointNumber() {
        return userTotalPointNumber;
    }

    public void setUserTotalPointNumber(int userTotalpointNumber) {
        this.userTotalPointNumber = userTotalpointNumber;
    }

    public int getUserRankNumber() {
        return userRankNumber;
    }

    public void setUserRankNumber(int userRankNumber) {
        this.userRankNumber = userRankNumber;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupNameAndGroupId() {
        if (this.getGroup() != null) {
            this.groupName = this.group.getName();
            this.groupId = this.group.getId();
        }

    }

    public int getGroupId() {
        return groupId;
    }


    public int getActivatedStatus() {
        return activatedStatus;
    }

    public void setActivatedStatus() {
        this.activatedStatus = this.activated;
    }
}
