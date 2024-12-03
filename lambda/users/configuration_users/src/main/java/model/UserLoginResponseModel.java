package model;


public class UserLoginResponseModel {
    private String userId;
    private String jwt;
    private Integer state;
    private String firstName;
    private String lastName;
    private String federalTax;
    private String email;
    private String mobilePhone;
    private String picUser;;
    private String disability;

    public UserLoginResponseModel(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public String getFederalTax() {
        return federalTax;
    }

    public void setFederalTax(String federalTax) {
        this.federalTax = federalTax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPicUser() {
        return picUser;
    }

    public void setPicUser(String picUser) {
        this.picUser = picUser;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }


    public UserLoginResponseModel(User user, String jwt){
        if (user != null){
            this.userId = user.getUserId();
            if(jwt != null)
                this.jwt = jwt;
            if(user.getState() != null)
                this.state = user.getState();
            if(user.getFirstName() != null)
                this.firstName = user.getFirstName();
            if(user.getLastName() != null)
                this.lastName = user.getLastName();
            if(user.getFederalTax() != null)
                this.federalTax = user.getFederalTax();
            if(user.getEmail() != null)
                this.email = user.getEmail();
            if(user.getMobilePhone() != null)
                this.mobilePhone = user.getMobilePhone();
            if(user.getPicUser() != null)
                this.picUser = user.getPicUser();
            if(user.getDisability() != null)
                this.disability = user.getDisability();
        }
    }
}
