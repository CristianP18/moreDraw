package model;

import static configuration.DynamoDBIndexes.GSI_USER_FEDERAL_TAX;
import static helper.Common.*;
import static service.Service.isValidPhone;
import exceptions.InvalidAddress;
import helper.Common;
import helper.DateHelper;
import helper.GenerateCodeHelper;
import service.UserService;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

@DynamoDbBean
public class User  {
    private String userId;
    private String firstName;
    private String lastName;
    private String federalTax;
    private String rg;
    private String email;
    private String phone;
    private String mobilePhone;
    private String jwt;
    private String oneTimePassword;
    private Boolean notify;
    private Boolean verified;
    private String disability;
    private String password;
    private String locale;
    private String picUser;
    private String passwordHash; // Hash da senha
    private String role; // Papel do usuário (ex: admin, usuário)
    private Integer state;
    private String created;
    private String modified;

    public User(){}

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = { GSI_USER_FEDERAL_TAX })
    public String getFederalTax() {
        return federalTax;
    }

    public void setFederalTax(String federalTax) {
        this.federalTax = federalTax;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
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

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPicUser() {
        return picUser;
    }

    public void setPicUser(String picUser) {
        this.picUser = picUser;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }


    public User(PostUsersRequestModel request) throws IOException, InvalidAddress {
        this.userId = UUID.randomUUID().toString();
        this.firstName = removeSpecialCharacters(request.getFirstName());
        this.lastName = removeSpecialCharacters(request.getLastName());
        this.mobilePhone = removeBrackets(request.getMobilePhone());
        this.federalTax = removeDashAndDotAndSlash(request.getFederalTax());
        this.password = request.getPassword();
        this.oneTimePassword = GenerateCodeHelper.generateCode(6);
        this.picUser = UserService.USER_GRAY;
        this.notify = request.getNotify();
        this.verified = false;
        this.disability = removeSpecialCharacters(request.getDisability());
        this.state = UserState.ONLINE;

        this.created = request.getCreated() != null ? request.getCreated() : DateHelper.nowString();

    }

    public User(String federalTax, String name, String phone){
        this.userId = UUID.randomUUID().toString();
        this.federalTax = removeDashAndDotAndSlash(federalTax);
        this.created = DateHelper.nowString();

        this.verified = false;
        this.picUser = UserService.USER_ORANGE;
        this.state = UserState.OFFLINE;

        if(name != null){
            this.lastName = "";
            String[] nameSplit = removeSpecialCharacters(removeCPF(name)).split(" ");

            this.firstName = nameSplit[0].toUpperCase();

            if (nameSplit.length == 2){
                if(nameSplit[1] != null)
                    this.lastName = nameSplit[1].toUpperCase();
            }

            else if (nameSplit.length > 2){
                for(int i = 1; i < nameSplit.length; i++)
                    this.lastName = this.lastName.concat(nameSplit[i].toUpperCase() + " ");
            }
        }

        String mobilePhone = removeBrackets(phone);
        if(isValidPhone(mobilePhone))
            this.mobilePhone = mobilePhone;
    }

    public User updateTemp(String name, String phone){
        if(name != null){
            this.lastName = "";
            String[] nameSplit = removeSpecialCharacters(removeCPF(name)).split(" ");

            this.firstName = nameSplit[0].toUpperCase();

            if (nameSplit.length == 2){
                if(nameSplit[1] != null)
                    this.lastName = nameSplit[1].toUpperCase();
            }

            else if (nameSplit.length > 2){
                for(int i = 1; i < nameSplit.length; i++)
                    this.lastName = this.lastName.concat(nameSplit[i].toUpperCase() + " ");
            }
        }

        String mobilePhone = removeBrackets(phone);
        if(isValidPhone(mobilePhone))
            this.mobilePhone = mobilePhone;

        return this;
    }

    public static User update(PutUsersRequestModel request, User user){
        user.modified = DateHelper.nowString();

        if(request.getFirstName() != null){
            user.lastName = "";
            String[] nameSplit = removeSpecialCharacters(removeCPF(request.getFirstName())).split(" ");

            user.firstName = nameSplit[0].toUpperCase();

            if (nameSplit.length == 2){
                if(nameSplit[1] != null)
                    user.lastName = nameSplit[1].toUpperCase();
            }

            else if (nameSplit.length > 2){
                for(int i = 1; i < nameSplit.length; i++)
                    user.lastName = user.lastName.concat(nameSplit[i].toUpperCase() + " ");
            }
        }

        user.email = request.getEmail() != null ? request.getEmail() : user.email;
        user.phone = request.getPhone() != null ? request.getPhone() : user.phone;
        user.notify = request.getNotify() != null ? request.getNotify() : user.notify;
        user.disability = removeSpecialCharacters(request.getDisability());

        return user;
    }

    public static String removeCPF(String companyName){
        if(companyName != null){
            String regex = "\\b\\d{11}\\b";
            Pattern p = Pattern.compile(regex,
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

            return p.matcher(companyName).replaceAll("");
        }
        else
            return null;
    }
}