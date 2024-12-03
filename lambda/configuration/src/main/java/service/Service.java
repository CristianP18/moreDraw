package service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import configuration.Message;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service {
    public static final Message MESSAGE = new Message();

    public static final Integer SIZE_MIN = 1, SIZE_MAX = 255,
            SIZE_STATE_UF = 2, SIZE_ZIP = 8, SIZE_MIN_ADDRESS_NUMBER = 1,
            SIZE_MIN_FEDERAL_TAX_CNPJ = 14,  SIZE_MAX_FEDERAL_TAX_CNPJ = 18,
            SIZE_MIN_FEDERAL_TAX_CPF = 11, SIZE_MAX_FEDERAL_TAX_CPF = 14,
            SIZE_MAX_STATE_TAX = 9, SIZE_PREFIX_COMPANY = 3,
            SIZE_TRACKING_CODE = 16, SIZE_MIN_BAR_CODE = 9,
            SIZE_MIN_PASSWORD = 6, SIZE_MAX_RG = 11,
            SIZE_MIN_LICENSE_PLATE = 7, SIZE_MAX_LICENSE_PLATE = 11,
            SIZE_MIN_IP_ADDRESS = 7, SIZE_MAX_IP_ADDRESS = 20,
            SIZE_MIN_IP_PORT = 1, SIZE_MAX_IP_PORT = 5;

    public static Boolean isValidEmail(String input) {
        if(input == null)
            return false;

        String regex =
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

        Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    public static Boolean isValidPhone(String input){
        if(input == null)
            return false;
        try{
            String newPhoneNumber = input;
            if(input.length() == 10 || input.length() == 11){
                newPhoneNumber = "55".concat(input);
            }
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(newPhoneNumber, "BR");

            return numberUtil.isValidNumber(phoneNumber);
        }
        catch (NumberParseException e) {
            return false;
        }
    }

    public static Boolean isValidFederalTaxCPF(String cpf) {
        if (cpf.equals("00000000000") || cpf.equals("11111111111") ||
                cpf.equals("22222222222") || cpf.equals("33333333333") ||
                cpf.equals("44444444444") || cpf.equals("55555555555") ||
                cpf.equals("66666666666") || cpf.equals("77777777777") ||
                cpf.equals("88888888888") || cpf.equals("99999999999") ||
                cpf.length() < SIZE_MIN_FEDERAL_TAX_CPF ||
                cpf.length() > SIZE_MAX_FEDERAL_TAX_CPF ||
                cpf == null)
            return false;

        char dig10, dig11;
        int sum, i, result, num, weight;

        try {
            sum = 0;
            weight = 10;
            for (i = 0; i < 9; i++) {
                num = cpf.charAt(i) - 48;
                sum = sum + (num * weight);
                weight = weight - 1;
            }

            result = 11 - (sum % 11);
            if ((result == 10) || (result == 11))
                dig10 = '0';
            else
                dig10 = (char)(result + 48);

            sum = 0;
            weight = 11;
            for(i = 0; i < 10; i++) {
                num = cpf.charAt(i) - 48;
                sum = sum + (num * weight);
                weight = weight - 1;
            }

            result = 11 - (sum % 11);
            if ((result == 10) || (result == 11))
                dig11 = '0';
            else
                dig11 = (char)(result + 48);

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException e) {
            return false;
        }
    }

    public static Boolean isValidFederalTaxCNPJ(String cnpj) {
        if (cnpj.equals("00000000000000") || cnpj.equals("11111111111111") ||
                cnpj.equals("22222222222222") || cnpj.equals("33333333333333") ||
                cnpj.equals("44444444444444") || cnpj.equals("55555555555555") ||
                cnpj.equals("66666666666666") || cnpj.equals("77777777777777") ||
                cnpj.equals("88888888888888") || cnpj.equals("99999999999999") ||
                cnpj.length() < SIZE_MIN_FEDERAL_TAX_CNPJ ||
                cnpj.length() > SIZE_MAX_FEDERAL_TAX_CNPJ ||
                cnpj == null)
            return false;

        char dig13, dig14;
        int sum, i, result, num, weight;

        try {
            sum = 0;
            weight = 2;
            for (i = 11; i >= 0; i--) {
                num = cnpj.charAt(i) - 48;
                sum = sum + (num * weight);
                weight = weight + 1;
                if (weight == 10)
                    weight = 2;
            }

            result = sum % 11;
            if ((result == 0) || (result == 1))
                dig13 = '0';
            else dig13 = (char) ((11 - result) + 48);

            sum = 0;
            weight = 2;
            for (i = 12; i >= 0; i--) {
                num = cnpj.charAt(i) - 48;
                sum = sum + (num * weight);
                weight = weight + 1;
                if (weight == 10)
                    weight = 2;
            }

            result = sum % 11;
            if ((result == 0) || (result == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - result) + 48);

            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException e) {
            return false;
        }
    }

    public static Boolean isValidEntity(String input, int sizeMin, int sizeMax) {
        if(input == null)
            return false;

        else return input.length() >= sizeMin && input.length() <= sizeMax;
    }

    public static Boolean isValidInteger(Integer input) {
        if(input == null)
            return true;

        else return input >= 0;
    }

    public static Boolean isValidDouble(Double input) {
        if(input == null)
            return true;

        else return input >= 0.0;
    }

    public static Double format2f(Double number){
        return Double.parseDouble(String.format("%.2f", number));
    }


}


