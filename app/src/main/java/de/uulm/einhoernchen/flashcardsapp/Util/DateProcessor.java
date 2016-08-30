package de.uulm.einhoernchen.flashcardsapp.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jonas-uni on 21.08.2016.
 */
public class DateProcessor {

    /**
     * Takes a Sting and returns its Date
     *
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = null;
        try {
            if (dateString == null || dateString == "") {
                dateString = "2016-08-21 13:42:30 UTC";
            }
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();

            String dateInString = "31-08-1982 10:20:56";
            try {
                date = sdf.parse(dateInString);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return date;
    }
}
