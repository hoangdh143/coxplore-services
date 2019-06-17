package co.pailab.lime.model;

import co.pailab.lime.helper.validation.constraint.DateValidationConstraint;
import co.pailab.lime.helper.validation.constraint.PhoneValidationConstraint;
import co.pailab.lime.helper.validation.constraint.XssValidationConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "User")
@Table(name = "user")
public class User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    protected Group group;

    @Column(name = "email", nullable = false, unique = true)
    @XssValidationConstraint
    @Email
    protected String email;

    @Column(name = "password")
    protected String password;

    @Column(name = "activated")
    protected int activated;

    @Column(name = "activation_token")
    @XssValidationConstraint
    @Length(max = 6)
    protected String activationToken;

    @Column(name = "activation_token_expiry")
    @XssValidationConstraint
    @Length(max = 100)
    protected String activationTokenExpiry;

    @Column(name = "pw_confirmation_token")
    @XssValidationConstraint
    @Length(max = 6)
    protected String pwConfirmationToken;

    @Column(name = "pw_confirmation_token_expiry")
    @XssValidationConstraint
    @Length(max = 100)
    protected String pwConfirmationTokenExpiry;

    @Column(name = "date_of_birth")
    @XssValidationConstraint
    @Length(max = 100)
    @DateValidationConstraint
    protected String dateOfBirth;

    @Transient
    protected String deviceToken;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;
    @Column(name = "phone")
    @PhoneValidationConstraint
    @XssValidationConstraint
    String phone;
    @Column(name = "address")
    @XssValidationConstraint
    @Length(max = 500) String address;
    @CreationTimestamp
    Timestamp created_at;
    @UpdateTimestamp
    Timestamp updated_at;
    @Column(name = "weight")
    double weight = 0.00;
    @Column(name = "height")
    double height = 0.00;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthenticationToken> authenticationTokens = new ArrayList<>();
    @Column(name = "first_name")
    @XssValidationConstraint
    @Length(max = 50)
    private String firstName;
    @Column(name = "last_name")
    @XssValidationConstraint
    @Length(max = 50)
    private String lastName;
    @Column(name = "username")
    @XssValidationConstraint
    @Length(max = 50)
    private String username;
    @Column(name = "avatar")
    @XssValidationConstraint
    private String avatar;
    // 0 : none, 1 : male, 2 : female, 3: others
    @Column(name = "gender")
    @Min(0)
    @Max(3)
    private Integer gender = 0;

    public User() {

    }

    public User(int id, Group group, String username, String avatar) {
        this.id = id;
        this.group = group;
        this.username = username;
        this.avatar = avatar;
    }

    public User(int id) {
        this.id = id;
    }

    public User(int id, String email, String firstName, String lastName, String username, String avatar, String phone,
                String address, String dateOfBirth, double weight, double height, Integer gender) {
        super();
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.avatar = avatar;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    public User(int id, String email, String firstName, String lastName, String username, String avatar, String phone,
                String address, String dateOfBirth, double weight, double height, Integer gender,
                Group group, int activated) {
        super();
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.avatar = avatar;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.group = group;
        this.activated = activated;
    }

    public User(String password) {
        super();
        this.password = password;

    }

    public User(int id, String email, String firstName, String lastName, String username, String avatar, String phone, String address, String dateOfBirth, double weight, double height, int gender, Group group, int activated) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.gender = gender;
        this.group = group;
        this.activated = activated;
    }

    public User(String username, String firstName, String lastName, int gender, String avatar, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.avatar = avatar;
        this.email = email;
    }

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws Exception {

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        String fullName = "";
        if (firstName != null) fullName = fullName.concat(firstName);
        if (lastName != null) fullName = fullName.concat(" " + lastName);
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int value) {
        this.activated = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getActivationTokenExpiry() {
        return activationTokenExpiry;
    }

    public void setActivationTokenExpiry(String activationTokenExpiry) {
        this.activationTokenExpiry = activationTokenExpiry;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public String getPwConfirmationToken() {
        return pwConfirmationToken;
    }

    public void setPwConfirmationToken(String pwConfirmationToken) {
        this.pwConfirmationToken = pwConfirmationToken;
    }

    public String getPwConfirmationTokenExpiry() {
        return pwConfirmationTokenExpiry;
    }

    public void setPwConfirmationTokenExpiry(String pwConfirmationTokenExpiry) {
        this.pwConfirmationTokenExpiry = pwConfirmationTokenExpiry;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

}
