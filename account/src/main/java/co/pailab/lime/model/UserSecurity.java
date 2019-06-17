package co.pailab.lime.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class UserSecurity extends User {
    private static final long serialVersionUID = 1L;
    protected String email;
    protected co.pailab.lime.model.User user;
    private int id;
    private Group group;

    public UserSecurity(String username, String password, co.pailab.lime.model.User user, boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.user = user;
    }

    public UserSecurity(String username, String password, boolean enabled,
                        Collection<? extends GrantedAuthority> authorities,
                        String email,
                        int id,
                        Group group) {
        super(username, password, enabled, true, true, true, authorities);
        this.email = email;
        this.id = id;
        this.group = group;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public co.pailab.lime.model.User getUser() {
        return user;
    }

    public void setUser(co.pailab.lime.model.User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}