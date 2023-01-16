package it.itsar.tomeandroid;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LeggiScrivi {
    public static Boolean scriviLocale(File dir, String filename, String text) throws IOException {
        File file = new File(dir, filename);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(text.getBytes());
            stream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String leggiLocale(File dir, String filename) throws IOException {
        File file = new File(dir, filename);
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try (FileInputStream in = new FileInputStream((file))){
            int a = in.read(bytes);
            Log.d("result","a" + a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(bytes);
    }
}
