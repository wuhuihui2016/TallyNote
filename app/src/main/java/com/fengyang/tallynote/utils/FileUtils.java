package com.fengyang.tallynote.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static final String dirPath = Environment.getExternalStorageDirectory() + "/TallyNote/";//项目根目录
    public static final String excelPath = dirPath + "/excel/";//excel根目录

    /**
     * 获取APP文件夹
     *
     * @return
     */
    public static File getAppDir() {
        if (isSDCardAvailable()) {
            return new File(dirPath);
        } else return null;
    }

    /**
     * 获取excel文件夹
     *
     * @return
     */
    public static File getExcelDir() {
        if (isSDCardAvailable()) {
            return new File(excelPath);
        } else return null;
    }

    /**
     * 创建项目文件目录
     */
    public static void createDir() {
        if (isSDCardAvailable()) {
            getAppDir().mkdirs();
            getExcelDir().mkdirs();
        }
    }

    /**
     * 判断SDCard是否可用
     *
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
                cursor.close();
            } catch (Exception e) {
                LogUtils.e(TAG + "-getPath", e.toString());
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取tally_note文件
     *
     * @return
     */
    public static File getTallyNoteFile() {
        File file = null;
        File excelDir = FileUtils.getExcelDir();
        final File files[] = excelDir.listFiles();
        for (int i = 0; i < files.length; i++)
            if (files[i].getName().contains("tally_note_")) {
                file = files[i];
            }

        return file;
    }

    /**
     * 向其他APP分享文件
     * http://blog.csdn.net/yuxiaohui78/article/details/8232402
     *
     * @param activity
     * @param file     需要分享的文件
     */
    public static void shareFile(Activity activity, File file) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        share.setType("*/*"); //此处可发送多种文件
        activity.startActivity(Intent.createChooser(share, "发送文件"));
    }

    /**
     * 上传文件到小米云盘(仅将tally_note文件上传)
     *
     * @param activity
     */
    public static void uploadFile(Activity activity) {
        try {
            if (SystemUtils.getIsWIFI(activity)) {
                Intent share = new Intent(Intent.ACTION_SEND);

                //获得需要上传的文件
                File file = getTallyNoteFile();
                if (file == null) {
                    DialogUtils.showMsgDialog(activity, "", "文件不存在~");
                    return;
                }

                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.setType("*/*"); //此处可发送多种文件

                //查找手机中是否已安装APP
                String name = "com.tencent.mm";
                if (isInstalledAPP(activity, name)) {
                    share.setPackage(name);
                } else {
                    DialogUtils.showMsgDialog(activity, "", "没有找到微信~");
                    return;
                }

                activity.startActivity(Intent.createChooser(share, "上传文件"));
            } else {
                DialogUtils.showMsgDialog(activity, "", "当前没有WIFI，不能上传~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据APP包名查找手机是否已安装该APP
     *
     * @param activity
     * @param packageName com.tencent.mm 微信,com.tencent.mobileqq QQ,com.baidu.netdisk 百度网盘
     * @return
     */
    private static boolean isInstalledAPP(Activity activity, String packageName) {
        boolean flag = false;
        List<PackageInfo> packs = activity.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if (p.packageName.equals(packageName)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 删除文件夹/文件
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
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
