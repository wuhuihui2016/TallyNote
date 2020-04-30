package com.whh.tallynote.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static final String dirPath = Environment.getExternalStorageDirectory() + "/ATallyNote/";//项目根目录
    public static final String excelPath = dirPath + "/excel/";//excel根目录

    public static final String logPath = dirPath + "/crash/";//log根目录


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
     * 获取log文件夹
     *
     * @return
     */
    public static File getLogDir() {
        if (isSDCardAvailable()) {
            return new File(logPath);
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
        if (file.exists() && file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
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
        if (!excelDir.exists()) return null;
        final File files[] = excelDir.listFiles();
        if (files.length == 0) return null;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith(ContansUtils.tallynote_file)) {
                file = files[i];
            }
            break;
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
        Uri uri = FileUtils.setFileProvider(activity, share, file);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("*/*"); //此处可发送多种文件
        activity.startActivity(Intent.createChooser(share, "发送文件"));
    }

    /**
     * 判读版本是否在7.0以上,7.0以上需要增加fileprovider
     *
     * @param intent
     */
    public static Uri setFileProvider(Context context, Intent intent, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //判读版本是否在7.0以上,7.0以上需要增加fileprovider
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build());
            uri = FileProvider.getUriForFile(context, "com.whh.tallynote.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 上传文件到上传到微信收藏(仅将tally_note文件上传)
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
                    DialogUtils.showMsgDialog(activity, "文件不存在~");
                    return;
                }

                Uri uri = FileUtils.setFileProvider(activity, share, file);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("*/*"); //此处可发送多种文件

                //查找手机中是否已安装微信APP
                String name = "com.tencent.mm";
                if (isInstalledAPP(activity, name)) {
                    share.setPackage(name);
                } else {
                    DialogUtils.showMsgDialog(activity, "没有找到微信~");
                    return;
                }

                activity.startActivity(Intent.createChooser(share, "上传文件"));
            } else {
                DialogUtils.showMsgDialog(activity, "当前没有WIFI，不能上传~");
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


    /**
     * 读取本地文本文件
     *
     * @param fileName
     * @return
     */
    public static String readTextFile(String fileName) {
        String result = null;
        try {
            File file = new File(fileName);
            int length = (int) file.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(file);
            fin.read(buff);
            fin.close();
            result = new String(buff, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "没有找到指定文件");
        }
        return result;
    }

    /**
     * 文件导出时清除旧文件
     *
     * @param type
     */
    public static void clearOldExcelFile(int type) {
        backupTallyNoteFile();
        File excelDir = FileUtils.getExcelDir();
        if (!excelDir.exists()) return;
        final File files[] = excelDir.listFiles();
        if (files.length == 0) return;
        for (int i = 0; i < files.length; i++) {
            if (type == ContansUtils.DAY) {
                if (files[i].getName().contains(ContansUtils.day_file)) files[i].delete();
            } else if (type == ContansUtils.DAY_HISTORY) {
                if (files[i].getName().contains(ContansUtils.day_history_file)) files[i].delete();
            } else if (type == ContansUtils.MONTH) {
                if (files[i].getName().contains(ContansUtils.month_file)) files[i].delete();
            } else if (type == ContansUtils.INCOME) {
                if (files[i].getName().contains(ContansUtils.income_file)) files[i].delete();
            } else if (type == ContansUtils.MEMO) {
                if (files[i].getName().contains(ContansUtils.memo_file)) files[i].delete();
            } else if (type == ContansUtils.NOTEPAD) {
                if (files[i].getName().contains(ContansUtils.notepad_file)) files[i].delete();
            } else {
                if (files[i].getName().contains(ContansUtils.tallynote_file)) files[i].delete();
            }
        }
    }

    /**
     * 备份总帐本文件
     */
    public static void backupTallyNoteFile() {
        try {
            if(getTallyNoteFile() == null) return;
            String tallyNoteFileName = getTallyNoteFile().getName();

            String dirPath = excelPath + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            File source = new File(excelPath + tallyNoteFileName);
            File dest = new File(dirPath + tallyNoteFileName);
            InputStream input = new FileInputStream(source);
            OutputStream output = new FileOutputStream(dest);
            byte[] buf = new byte[1024 * 5];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
