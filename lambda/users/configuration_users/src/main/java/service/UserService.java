package service;

import bundles.Bundle;
import com.google.firebase.messaging.FirebaseMessagingException;
import configuration.OperatorType;
import configuration.Response;
import exceptions.Conflict;
import exceptions.InvalidRelationalToken;
import exceptions.InvalidRequest;
import exceptions.NotFound;
import helper.DateHelper;
import helper.JWTHelper;
import model.*;
import repository.S3;
import repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class UserService extends Service {
    private static UserService INSTANCE;

    public static UserService getInstance() {
        if (INSTANCE == null)
            INSTANCE = new UserService();
        return INSTANCE;
    }

    public static final String USER_ORANGE =
            S3.link("docs.moreDraw.com.br/picuser", "user_orange.jpg");
    public static final String USER_GRAY =
            S3.link("docs.moreDraw.com.br/picuser", "user_gray.png");

    public User loadUserById(String userId) {
        return UserRepository.getInstance().loadUserById(userId);
    }

    public User getUserById(String userId, String locale) throws NotFound {
        User user = UserRepository.getInstance().getUserById(userId);

        if (user == null)
            throw new NotFound(Bundle.getInstance().getString("UserNotFound", locale));

        return user;
    }

    public User getUserNullById(String userId) {
        return UserRepository.getInstance().getUserById(userId);
    }

    public User getUserByVehicleId(String vehicleId) {
        return UserRepository.getInstance().getUserByVehicleId(vehicleId);
    }

    public User getUserFederalTax(String federalTax) {
        return UserRepository.getInstance().getUserFederalTax(federalTax);
    }

    public User getUserMobilePhone(String mobilePhone) {
        return UserRepository.getInstance().getUserMobilePhone(mobilePhone);
    }

    public ArrayList<User> getUserName(String firstName, String lastName) {
        return UserRepository.getInstance().getUserName(firstName, lastName);
    }

    public User getUserLogin(PostUsersLoginRequestModel request, String locale) throws NotFound {
        return UserRepository.getInstance().getUserLogin(request, locale);
    }

    public User getVerify(UsersVerifyRequestModel request, String locale) throws NotFound {
        return UserRepository.getInstance().getUserVerify(request, locale);
    }

    public void postUser(User user, String locale) throws IOException, FirebaseMessagingException {
        UserRepository.getInstance().postUser(user, locale);
    }

    public void postTempUser(User user, String locale) throws InvalidRequest {
        if (isValidFederalTaxCPF(user.getFederalTax()))
            UserRepository.getInstance().postTempUser(user);
        else
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidFederalTaxCPF", locale));
    }

    public void postUserAlreadyExists(User user, String locale) throws IOException, FirebaseMessagingException {
        UserRepository.getInstance().postUserAlreadyExists(user, locale);
    }

    public void postNewMobilePhone(User user, String locale) {
        UserRepository.getInstance().postNewMobilePhone(user, locale);
    }





    public void postBatchUser(ArrayList<User> users) throws IOException, FirebaseMessagingException {
        UserRepository.getInstance().postBatchUser(users);
    }

    public synchronized void putUser(User user) {
        UserRepository.getInstance().putUser(user);
    }

    public void putUserVerify(User user, String locale) {
        UserRepository.getInstance().putUserVerify(user, locale);
    }

    public User modifyState(User user, Integer state) {
        user.setModified(DateHelper.nowString());
        user.setState(state);
        return user;
    }



    public void sendPasswordVerificationSMS(User user, String locale) {
        UserRepository.getInstance().sendPasswordVerificationSMS(user, locale);
    }

    public void subscribe(String mobilePhone) {
        UserRepository.getInstance().subscribe(mobilePhone);
    }

    public void unsubscribe(String mobilePhone) {
        UserRepository.getInstance().unsubscribe(mobilePhone);
    }

    public Boolean isValid(User user, String locale) throws InvalidRequest {
        if (!isValidEntity(user.getFirstName(), SIZE_MIN, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidFirstName", locale));
        if (!isValidEntity(user.getLastName(), SIZE_MIN, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidLastName", locale));
        if (!isValidEntity(user.getPassword(), SIZE_MIN_PASSWORD, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidPassword", locale));
        if (!isValidFederalTaxCPF(user.getFederalTax()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidFederalTaxCPF", locale));
        if (!isValidPhone(user.getMobilePhone()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidMobilePhone", locale));
        if (!isValidEntity(user.getRg(), SIZE_MIN, SIZE_MAX_RG))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidRG", locale));
        if (!isValidEmail(user.getEmail()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidEmail", locale));
        if (!isValidPhone(user.getPhone()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidPhone", locale));

        return true;
    }

    public Boolean isValidCreate(User user, String locale) throws InvalidRequest {
        if (!isValidEntity(user.getFirstName(), SIZE_MIN, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidFirstName", locale));
        if (!isValidEntity(user.getPassword(), SIZE_MIN_PASSWORD, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidPassword", locale));
        if (!isValidFederalTaxCPF(user.getFederalTax()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidFederalTaxCPF", locale));
        if (!isValidPhone(user.getMobilePhone()))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidMobilePhone", locale));

        return true;
    }

    public Boolean isValidPassword(User user, String locale) throws InvalidRequest {
        if (!isValidEntity(user.getPassword(), SIZE_MIN_PASSWORD, SIZE_MAX))
            throw new InvalidRequest(Bundle.getInstance().getString("InvalidPassword", locale));

        return true;
    }

    public void resendSMS(User user, String locale) {
        UserRepository.getInstance().resendSMS(user, locale);
    }

    public Response<?> response(User user, String text, String type, int status) {
        MESSAGE.setStatus(status);
        MESSAGE.setType(type);
        MESSAGE.setText(text);

        if (user != null) {
            UserResponseModel content = new UserResponseModel(user);
            return Response.success(content, MESSAGE);
        }

        return Response.success(MESSAGE);
    }
    public Response<?> responseLogin(User user, String jwt, String text, String type, int status) {
        MESSAGE.setStatus(status);
        MESSAGE.setType(type);
        MESSAGE.setText(text);

        if (user != null) {
            UserLoginResponseModel content = new UserLoginResponseModel(user, jwt);
            return Response.success(content, MESSAGE);
        }

        return Response.success(MESSAGE);
    }


    public void deleteUser(User user) {
        UserRepository.getInstance().deleteUser(user);
    }

    public ArrayList<User> getUserLastName(String lastName) {
        return UserRepository.getInstance().getUserLastName(lastName);
    }

    public void saveUser(User newUser, String locale) throws IOException, FirebaseMessagingException {
        UserRepository.getInstance().postUser(newUser, locale);
    }
}