package co.pailab.lime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.List;

public class UserBasicInfo extends User {
    public UserBasicInfo(User user) {
        super(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getAvatar(),
                user.getEmail()
        );
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonIgnore
    public String getDateOfBirth() {
        return dateOfBirth;
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

    @JsonIgnore
    public Timestamp getUpdated_at() {
        return updated_at;
    }

    @JsonIgnore
    public Timestamp getCreated_at() {
        return created_at;
    }

    @JsonIgnore
    public double getHeight() {
        return height;
    }

    @JsonIgnore
    public double getWeight() {
        return weight;
    }

    @JsonIgnore
    public String getAddress() {
        return address;
    }

    @JsonIgnore
    public String getPhone() {
        return phone;
    }


}
