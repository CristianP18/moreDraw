package exceptions;

public class InvalidOneTimePassword extends Exception{
    public InvalidOneTimePassword(String message){
        super(message);
    }
}
