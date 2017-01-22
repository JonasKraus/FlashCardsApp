package de.uulm.einhoernchen.flashcardsapp.Util;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by jonas-uni on 21.08.2016.
 */
public class ProcessorDate {

    /**
     * Takes a Sting and returns its Date
     *
     * @deprecated
     * @param dateString
     * @return
     */
    @Nullable
    public static Date stringToDate(String dateString) {

        if (dateString == "" || dateString == null) {
            return null;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
        try {
            Date date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
        }


        return null;
    }

    public static String formatDate(Date date) {

        String myString = DateFormat.getDateInstance().format(date);

        Log.d("dateFormatted", myString);
        return  myString;
    }

    @Nullable
    public static Date stringToDateDb(String dateString) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            Date date = formatter.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Error", e.toString());
        }


        return null;
    }


    /**
     * Converts millis to hours minutes and seconds
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-22
     *
     * @param millis
     * @return
     */
    public static String convertMillisToHMS(long millis) {

        String converted = String.format(Locale.getDefault(), "%d hrs, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );

        return converted;
    }
}
