package co.pailab.lime.model;

import co.pailab.lime.helper.validation.constraint.XssValidationConstraint;

public class SocialAccessToken {
    @XssValidationConstraint
    private String accessToken;

    private String introduceCode;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIntroduceCode() {
        return introduceCode;
    }

    public void setIntroduceCode(String introduceCode) {
        this.introduceCode = introduceCode;
    }
}
