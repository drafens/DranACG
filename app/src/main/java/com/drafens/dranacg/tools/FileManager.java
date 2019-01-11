package com.drafens.dranacg.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.drafens.dranacg.error.MyFileWriteException;
import com.drafens.dranacg.error.MyJsonEmptyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager {
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/drafens/";

    public static void putPreferences(String name, String data, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, data);
        editor.apply();
    }

    public static void putPreferences(String name, int data, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, data);
        editor.apply();
    }

    public static void putPreferences(String name, boolean data, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, data);
        editor.apply();
    }

    static String getPreferenceStr(String name, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getString(name, "");
    }

    public static int getPreferenceInt(String name, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getInt(name, 0);
    }

    public static boolean getPreferenceBol(String name, Context context){
        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getBoolean(name, false);
    }

    static void writeFiles(String fileName, String data) throws MyFileWriteException {
        String catalog = "files/";
        try {
            File files = new File(PATH + catalog);
            if(!files.exists()) {
                boolean flag = files.mkdirs();
                if(!flag){
                    throw new MyFileWriteException();
                }
            }
            File file = new File(PATH + catalog + fileName);
            if (!file.exists()){
                if(!file.createNewFile()){
                    throw new MyFileWriteException();
                }
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyFileWriteException();
        }
    }

    static String readFiles(String fileName) throws MyJsonEmptyException {
        String path = PATH + fileName;
        String string;
        try{
            FileInputStream inputStream = new FileInputStream(path);
            int length = inputStream.available();
            byte [] buffer = new byte[length];
            int flag = inputStream.read(buffer);
            if (flag == -1){
                throw new MyJsonEmptyException();
            }
            string = new String(buffer, "UTF-8");
            inputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
            throw new MyJsonEmptyException();
        }
        return string;
    }
}
