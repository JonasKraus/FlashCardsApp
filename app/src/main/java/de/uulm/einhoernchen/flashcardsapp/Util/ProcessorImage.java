package de.uulm.einhoernchen.flashcardsapp.Util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteBitmap;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteUser;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteImage;
import de.uulm.einhoernchen.flashcardsapp.R;

public class ProcessorImage {

    public static final int COLOR_MIN = 0x00;
    public static final int COLOR_MAX = 0xFF;

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    public static String getPath(final Context context, final Uri uri) {

        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * http://stackoverflow.com/questions/8471226/how-to-resize-image-bitmap-to-a-given-size
     *
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());

        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    /**
     * Scales down the image to a maximal height
     *
     * @param realImage
     * @param maxImageHeight
     * @param filter
     * @return
     */
    public static Bitmap scaleDownMaxHeight(Bitmap realImage, float maxImageHeight, boolean filter) {

        float ratio = (float) maxImageHeight / realImage.getHeight();

        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);

        return newBitmap;
    }


    /**
     * Saves a bitmap to the storage
     *
     * @param bitmap
     * @return
     */
    public static File savebitmap(Bitmap bitmap, final Long userId, String appendix) {

        if (appendix == null) {
            appendix = "_flashcards_profile.png";
        }

        if (!appendix.contains(".png")) {
            appendix += ".png";
        }

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/flashcards";

        //PermissionManager.verifyStoragePermissionsWrite(this);

        OutputStream outStream = null;

        File file = new File(extStorageDirectory, userId + appendix);

        if (file.exists()) {

            file.delete();
            file = new File(extStorageDirectory, userId + appendix);
        }

        final String fileName = file.getName();

        try {
            outStream = new FileOutputStream(file);

            // Scale bitmap to right size
            bitmap = ProcessorImage.scaleDownMaxHeight(bitmap, 1080, true);

            // Now compress it
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outStream);

            outStream.flush();
            outStream.close();

            // Don't upload the file to the server if its not the logged in user image
            if (userId != Globals.getDb().getLoggedInUser().getId()) return file;

            AsyncPostRemoteImage asyncPostRemoteImage = new AsyncPostRemoteImage(new AsyncPostRemoteImage.AsyncPostRemoteImageResponse() {

                @Override
                public void processFinished(String mediaUri) {

                    // save as avatar
                    if (fileName.contains("_flashcards_profile")) {

                        Globals.getDb().saveAvatar(userId, mediaUri);
                        JSONObject jsonObjectUser = new JSONObject();

                        try {

                            jsonObjectUser.put(JsonKeys.USER_AVATAR, mediaUri);

                            jsonObjectUser.put(JsonKeys.USER_ID, userId);


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                        // Update the user on server
                        AsyncPatchRemoteUser asyncPatchRemoteUser = new AsyncPatchRemoteUser(jsonObjectUser);
                        asyncPatchRemoteUser.execute(userId);
                    }

                }
            });
            asyncPostRemoteImage.execute(extStorageDirectory + "/" + userId + appendix);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    /**
     * Checks if image exists locally otherwise downloads and saves it
     *
     * @param url
     * @param imageView
     * @param id
     * @param appendix
     */
    public static BitmapDrawable download(String url, ImageView imageView, long id, String appendix) {


        // DEfault setting avatar
        if (appendix == null) {
            appendix = "_flashcards_profile.png";
        }

        if (!appendix.contains(".png")) {
            appendix += ".png";
        }

        //PermissionManager.verifyStoragePermissionsWrite((Activity) context);
        File sd =  Environment.getExternalStorageDirectory();

        File folder = new File(sd + "/flashcards");
        boolean success = true;

        Bitmap bitmap = null;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success) {

            appendix = appendix.contains(".png") ? appendix : appendix + ".png";

            File image = new File(sd+"/flashcards", id + appendix);

            if (image.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                imageView.setImageBitmap(bitmap);

            } else {

                AsyncGetRemoteBitmap task = new AsyncGetRemoteBitmap(imageView, id, appendix);

                // TODO Connectivity check
                task.execute(url);
            }
        }

        return new BitmapDrawable(Globals.getContext().getResources(), bitmap);
    }


    /**
     * generates a random image
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-17
     *
     * @param context
     * @return
     */
    public static Bitmap generateImage(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.profileimage);
        //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
        return applyFleaEffect(bitmap);
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    /**
     * Applies random pattern to bitmap
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-17
     *
     * @param source
     * @return
     */
    public static Bitmap applyFleaEffect(Bitmap source) {

        if (source == null) return textAsBitmap("no bitmap found", 10, Color.RED);
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }


    public static String getImageUriFake(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, null, null);
        return path;
    }

    public static String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return getRealPathFromURI(Uri.parse(path), inContext);
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}


