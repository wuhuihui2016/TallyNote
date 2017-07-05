package com.fengyang.tallynote.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class FileUtils {

    public static final String dirPath = Environment.getExternalStorageDirectory()+ "/TallyNote/";//项目根目录

    /**
     * 获取APP文件夹
     * @return
     */
    public static File getAppDir() {
        if (isSDCardAvailable()) {
            return new File(dirPath);
        } else return null;
    }

    /**
     * 创建项目文件目录
     */
    public static void createDir() {
        if (isSDCardAvailable()) {
            if (! getAppDir().exists()) {
                getAppDir().mkdirs();
            }
        }
    }

    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取文件路径
     *
     * @param context
     * @param uri
     * @return
     * @throws URISyntaxException
     */
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try {
                String[] projection = {"_data"};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 删除文件夹/文件
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

}
