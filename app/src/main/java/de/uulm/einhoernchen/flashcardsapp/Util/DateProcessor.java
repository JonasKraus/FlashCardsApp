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
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        return date;
    }
}
