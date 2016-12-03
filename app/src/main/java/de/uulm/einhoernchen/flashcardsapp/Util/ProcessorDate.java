package de.uulm.einhoernchen.flashcardsapp.Util;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
}
