package de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.12.09
 */

public class AsyncGetRemoteBitmap extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private final WeakReference<ImageView> imageViewReference;
    private final long id;
    private final String appendix;

    public AsyncGetRemoteBitmap(ImageView imageView, long id, String appendix) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.id = id;
        this.appendix = appendix;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Bitmap bitmap = downloadBitmap(params[0]);
        ProcessorImage.savebitmap(bitmap, id, appendix);
        return bitmap;
    }


    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                Log.d("set bitmap", "");
            }
        }
    }

    static Bitmap downloadBitmap(String url) {

        if (url.contains("youtube")) {
            String[] parts = url.split("=");
            url = "https://img.youtube.com/vi/" + parts[1] + "/0.jpg";
        }

        Log.d("back call to", url);

        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest); // TODO Connectivity check
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {

                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
}
