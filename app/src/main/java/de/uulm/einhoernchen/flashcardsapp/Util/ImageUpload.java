package de.uulm.einhoernchen.flashcardsapp.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.03.01
 */

public class ImageUpload {


    private String resultUri;

    public String multipartRequest(String urlTo, Map<String, String> parmas, String filepath, String filefield, String fileMimeType) throws Exception {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "-----" + Long.toString(System.currentTimeMillis()) + "-----";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);

            Log.d("call to ", urlTo);

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary); //multipart/form-data; boundary=----WebKitFormBoundarypTaD8abyvKn7M21c
            connection.setRequestProperty("Authorization", "Bearer " + Globals.getToken());


            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "picture" + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + "image/jpeg" + lineEnd);
            //outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);


            outputStream.writeBytes(lineEnd);



            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (connection.getResponseCode() >= 400) {
                throw new Exception("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            resultUri = JsonParser.parseImageUploadResonse(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return resultUri;
        } catch (Exception e) {

            Log.e("ERROR", e.getMessage());
        }

        return resultUri;
    }


}