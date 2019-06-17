package co.pailab.lime.model;
public class Credential {
    private String email;
    private String password;
    private int activated;

    public Credential(String email, String password, int activated) {
        this.email = email;
        this.password = password;
        this.activated = activated;
    }

    public Credential() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }
}