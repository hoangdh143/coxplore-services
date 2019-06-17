package co.pailab.lime.model;

import co.pailab.lime.helper.validation.constraint.DateValidationConstraint;
import co.pailab.lime.helper.validation.constraint.PhoneValidationConstraint;
import co.pailab.lime.helper.validation.constraint.XssValidationConstraint;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class UserGroupChange {
    @PhoneValidationConstraint
    @XssValidationConstraint
    private String phone;

    @XssValidationConstraint
    @Length(max = 3000)
    private String experience;

    @XssValidationConstraint
    @Length(max = 3000)
    private String selfDescription;

    @XssValidationConstraint
    @Length(max = 100)
    @DateValidationConstraint
    protected String dateOfBirth;

    @XssValidationConstraint
    @Length(max = 80)
    private String firstName = "";

    @XssValidationConstraint
    @Length(max = 80)
    private String lastName = "";

    @XssValidationConstraint
    @Length(max = 200)
    private String title;

    private int careerId;

    private int jobId;

    @XssValidationConstraint
    @Length(max = 200)
    private String careerName;

    @XssValidationConstraint
    @Length(max = 200)
    private String jobName;

    @Min(2)
    @Max(5)
    private Integer groupId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSelfDescription() {
        return selfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        this.selfDescription = selfDescription;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCareerId() {
        return careerId;
    }

    public void setCareerId(int careerId) {
        this.careerId = careerId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getCareerName() {
        return careerName;
    }

    public void setCareerName(String careerName) {
        this.careerName = careerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
