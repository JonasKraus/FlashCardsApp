package de.uulm.einhoernchen.flashcardsapp.Util;

import android.util.Log;

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
     * @deprecated
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString) {

        Log.d("process date", dateString);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date = null;
        try {
            if (dateString == null || dateString == "") {
                dateString = "2016-08-21 13:42:30 UTC";
            }
            date = sdf.parse(dateString);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Log.d("processed date", date.toString());
            return date;
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return date;
    }

    public static String formatDate(Date date) {

        String myString = DateFormat.getDateInstance().format(date);

        Log.d("dateFormatted", myString);
        return  myString;
    }
}
