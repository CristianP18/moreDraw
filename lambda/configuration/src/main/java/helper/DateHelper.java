package helper;

import org.joda.time.DateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateHelper {
    public static final SimpleDateFormat sdfCompact = new SimpleDateFormat(
            "yyyyMMddHHmmssSSSS", new Locale("pt", "br"));
    public static final SimpleDateFormat sdfWithSlash = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm", new Locale("pt", "br"));
    public static final SimpleDateFormat sdfDefault =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("pt", "br"));
    public static final SimpleDateFormat sdfDefaultWithoutTimezone =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat sdfDateLabel =
            new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "br"));
    public static final SimpleDateFormat sdfHourLabel =
            new SimpleDateFormat("HH:mm:ss", new Locale("pt", "br"));

    public static final SimpleDateFormat sdfPeriod =
            new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000Z", new Locale("pt", "br"));
    private static DateHelper INSTANCE;

    public static DateHelper getInstance() {
        if(INSTANCE == null)
            INSTANCE = new DateHelper();
        return INSTANCE;
    }

    public static Date now(){
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3:00");
        TimeZone.setDefault(timeZone);

        Calendar calendar = GregorianCalendar.getInstance(timeZone);

        return new Date(calendar.getTimeInMillis() - 10800000);
    }

    public static String nowString() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-3:00");
        TimeZone.setDefault(timeZone);

        Calendar calendar = GregorianCalendar.getInstance(timeZone);
        Date date = new Date(calendar.getTimeInMillis());

        sdfDefault.setTimeZone(timeZone);

        return sdfDefault.format(date);
    }

    public static Date convertStringToDate(String dateString){
        Date date;
        try {
            date = sdfDefaultWithoutTimezone.parse(dateString);
        } catch (ParseException e) {
            date = null;
        }

        return date;
    }

    public static String formatDateYear(String dateString) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("pt", "br"));
            Date date = parser.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Janeiro é 0, então adicionamos 1
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return String.format("%04d-%02d-%02d", year, month, day);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // ou lidar com o erro conforme necessário
        }
    }

    public static Date minusThreeDays(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(3);
        return dateTime.toDate();
    }


    public static Date minusSevenDays(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusDays(7);
        return dateTime.toDate();
    }

    public static Date minusOneMonth(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusMonths(1);
        return dateTime.toDate();
    }

    public static Date firstDayOfMonth(){
        DateTime dateTime = new DateTime();
        DateTime hours = dateTime.hourOfDay().getDateTime();
        DateTime firstDay = dateTime.withDayOfMonth(1).minusHours(hours.getHourOfDay()-1);

        return firstDay.toDate();
    }

    public static Date minusThreeMonths(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.minusMonths(3);
        return dateTime.toDate();
    }

    public static Date plusOneDay(String timestamp){
        DateTime dateTime = new DateTime(timestamp);
        dateTime = dateTime.plusDays(1);

        return dateTime.toDate();
    }

    public static Date plusTenDays(String timestamp){
        DateTime dateTime = new DateTime(timestamp);
        dateTime = dateTime.plusDays(10);

        return dateTime.toDate();
    }

    public static String correctCreatedMobile(String created){
        if (created.length() == 29){
            String left = created.substring(0, 23);
            return left.concat("-0300");
        }
        return created;
    }

    public static String correctHoursAndMinutes(String date){
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            // nao possui Horas, minutos e segundos
            if(date.length() == 11){
                LocalDateTime localDateTime = LocalDateTime.parse(date + "00:00:00");

                return localDateTime.format(formatter);
            }

            // nao possui Segundos
            if(date.length() == 16){
                LocalDateTime localDateTime = LocalDateTime.parse(date + ":00");

                return localDateTime.format(formatter);
            }

            // completo, possui Z
            if(date.length() == 24){
                String dateWithoutZ = date.replace("Z", "");
                LocalDateTime localDateTime = LocalDateTime.parse(dateWithoutZ);
                return localDateTime.format(formatter);
            }

            return null;
        }
        catch (Exception e){
            return null;
        }
    }
}
