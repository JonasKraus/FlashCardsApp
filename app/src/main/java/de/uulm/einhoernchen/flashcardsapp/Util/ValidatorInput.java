package de.uulm.einhoernchen.flashcardsapp.Util;

import android.text.Html;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
     * Sets the message and a text color if default is not enough
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-24
     *
     * @param radioGroup
     * @param msg
     * @param color
     */
    private static void setErrorTextAndColor (RadioGroup radioGroup, String msg, String color) {

        if (color == null) {

            ((RadioButton) radioGroup.getChildAt(0)).setError(msg);
        } else {

            ((RadioButton) radioGroup.getChildAt(0)).setError(Html.fromHtml("<font color='" + color + "'>" + msg + "</font>"));
        }

        radioGroup.requestFocus();
    }

    /**
     * Clears the message and a text
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-24
     *
     * @param radioGroup
     */
    private static void removeError (RadioGroup radioGroup) {

            ((RadioButton) radioGroup.getChildAt(0)).setError(null);

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
     * @param radioGroup
     * @return
     */
    public static boolean hasCheck(RadioGroup radioGroup) {

        boolean isValid = true;

        String msg = Globals.getContext().getString(R.string.error_field_required);


        if (radioGroup.getCheckedRadioButtonId() == -1) {

            setErrorTextAndColor(radioGroup, msg, ValidatorInput.WHITE);

            isValid = false;
        } else {

            removeError(radioGroup);
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
