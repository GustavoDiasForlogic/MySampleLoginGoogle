package net.forlogic.samplelogingoogle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public final class Utils {

    private Utils() {
    }

    public static Bitmap getBitmapFromURL(String urlPath) {
        InputStream is = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            is = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(is);

            return image;

        } catch (MalformedURLException e) {
            e.printStackTrace(System.err);
            return null;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;

        } finally {

            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
