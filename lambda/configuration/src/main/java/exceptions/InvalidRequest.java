package exceptions;

public class InvalidRequest extends Exception{
    public InvalidRequest(String message){
        super(message);
    }
}
