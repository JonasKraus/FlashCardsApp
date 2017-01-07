package de.uulm.einhoernchen.flashcardsapp.Util;

import android.text.Html;
import android.webkit.URLUtil;
import android.widget.EditText;

import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.07
 */

public class ValidatorInput {


    private static final String WHITE = Globals.getContext().getString(R.string.white);


    /**
     * Sets the message and a text color if default is not enough
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param editText
     * @param msg
     * @param color
     */
    private static void setErrorTextAndColor (EditText editText, String msg, String color) {

        if (color == null) {

            editText.setError(msg);
        } else {

            editText.setError(Html.fromHtml("<font color='" + color + "'>" + msg + "</font>"));
        }

        editText.requestFocus();
    }


    /**
     * Checks if a editText has a valid uri string otherwise it shows some error message
     * Gives the message a new color
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param editText
     * @return
     */
    public static boolean isValidUri(EditText editText, String color) {

        boolean isValid = true;

        String msg = Globals.getContext().getString(R.string.error_invalid_uri);

        String uri = editText.getText().toString();

        if (uri != null && !uri.equals("")) {

            if (!URLUtil.isValidUrl(uri)) {

                setErrorTextAndColor(editText, msg, color);

                isValid = false;
            }
        }

        return isValid;
    }


    /**
     * Checks if a editText has a valid uri string otherwise it shows some error message
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param editText
     * @return
     */
    public static boolean isValidUri(EditText editText) {

        boolean isValid = true;

        String msg = Globals.getContext().getString(R.string.error_invalid_uri);

        String uri = editText.getText().toString();

        if (uri != null && !uri.equals("")) {

            if (!URLUtil.isValidUrl(uri)) {

                setErrorTextAndColor(editText, msg, ValidatorInput.WHITE);

                isValid = false;
            }
        }

        return isValid;
    }


    /**
     * Checks if a editText is not empty otherwise it shows some error message
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param editText
     * @return
     */
    public static boolean isNotEmpty(EditText editText) {

        boolean isValid = true;

        String msg = Globals.getContext().getString(R.string.error_field_required);

        String text = editText.getText().toString();

        if (text == null || text.equals("")) {

            setErrorTextAndColor(editText, msg, ValidatorInput.WHITE);

            isValid = false;
        }

        return isValid;
    }


    /**
     * Checks if a editText is not empty otherwise it shows some error message
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param editText
     * @return
     */
    public static boolean isNotEmpty(EditText editText, String color) {

        boolean isValid = true;

        String msg = Globals.getContext().getString(R.string.error_field_required);

        String text = editText.getText().toString();

        if (text == null || text.equals("")) {

            setErrorTextAndColor(editText, msg, color);

            isValid = false;
        }

        return isValid;
    }
}
