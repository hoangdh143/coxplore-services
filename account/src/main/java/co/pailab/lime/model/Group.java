package co.pailab.lime.model;

import co.pailab.lime.helper.validation.constraint.XssValidationConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Group")
@Table(name = "`group`")
public class Group {
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    protected List<GroupModulePermission> groupModulePermissions = new ArrayList<>();
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<User> users = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name", nullable = false, unique = true)
    @XssValidationConstraint
    @Length(max = 50)
    private String name;

    @Column(name = "description")
    @XssValidationConstraint
    private String description;

    @CreationTimestamp
    private Timestamp created_at;

    @UpdateTimestamp
    private Timestamp updated_at;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GroupModulePermission> getGroupModulePermissions() {
        return groupModulePermissions;
    }

    public void setGroupModulePermissions(List<GroupModulePermission> groupModulePermissions) {
        this.groupModulePermissions = groupModulePermissions;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
