package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ImageUpload;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonParser;

/**
 * Created by jonas-uni on 17.08.2016.
 */
public class AsyncPostRemoteImage extends AsyncTask<String, Long, String> {

    private JSONObject jsonObject;
    private AsyncPostRemoteImageResponse delegate;
    private String result;


    public interface AsyncPostRemoteImageResponse {

        public void processFinished(String mediaUri);
    }


    /**
     * Constructs with delegate
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     */
    public AsyncPostRemoteImage(AsyncPostRemoteImageResponse delegate) {

        this.delegate = delegate;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        ImageUpload imageUpload = new ImageUpload();

        Map<String, String> paramsRequest = new HashMap<String, String>(2);
        //paramsRequest.put("foo", "hash");
        //paramsRequest.put("bar", "captiom");

        try {

            result = imageUpload.multipartRequest(Routes.URL + Routes.SLASH + Routes.UPLOAD, paramsRequest, params[0], "form-data", "file");
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        if (response != null && !response.equals("")) {

            delegate.processFinished(response);
        } else {

            Log.w("POST UPLOAD IMAGE", "FAILED");
        }

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}
