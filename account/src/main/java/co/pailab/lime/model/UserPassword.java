package co.pailab.lime.model;

public class UserPassword extends User {
    private String newPassword;

    public UserPassword() {

    }

    public UserPassword(String password, String newPassword) {
        super(password);
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
