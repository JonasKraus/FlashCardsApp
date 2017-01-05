package de.uulm.einhoernchen.flashcardsapp.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncGetRemoteHeartbeat;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.01.05
 */
public class ProcessConnectivity {

    private static boolean isAlive;


    /**
     * check if the device has any kind internet connection
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-03
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Call this before any async task that requests the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-03
     */
    public static void isServerAlive () {

        AsyncGetRemoteHeartbeat asyncGetRemoteHeartbeat = new AsyncGetRemoteHeartbeat(new AsyncGetRemoteHeartbeat.AsyncResponseHeartbeat() {

            @Override
            public void processFinish(Boolean isAlive) {

                setAlive(isAlive);
            }
        });

        asyncGetRemoteHeartbeat.execute();
    }


    /**
     * Use this to safely check the connection
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     *
     * @param context
     * @return
     */
    public static boolean isOk(Context context) {

        isServerAlive();

        //Log.d("isOk", context.getClass().getName());

        return isNetworkAvailable(context) && ProcessConnectivity.isAlive;

    }


    /**
     * Use this to safely check the connection
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-05
     *
     * @param context
     * @return
     */
    public static boolean isOk(Context context, boolean showToastLong) {

        isServerAlive();

        //Log.d("isOk", context.getClass().getName());

        int time = showToastLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;

        boolean okNet = isNetworkAvailable(context);

        if (!okNet) {

            Toast.makeText(context, context.getText(R.string.no_connection), time).show();
        } else if (!isAlive) {

            Toast.makeText(context, context.getText(R.string.service_unavailable), time).show();
        }

        return okNet && isAlive;
    }

    private static void setAlive (boolean isAlive) {
        ProcessConnectivity.isAlive = isAlive;
    }

}
