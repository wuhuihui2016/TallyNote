package com.fengyang.tallynote.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wuhuihui on 2017/3/24.
 */
public class FileUtils {

    public static final String dirPath = Environment.getExternalStorageDirectory()+ "/tallyNote/";//项目根目录

    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 创建项目文件目录
     */
    public static void createDirPath() {
        if (isSDCardEnable()) {
            File dirFile = new File(dirPath);
            if (! dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
     }


    /**
     * 获取SD卡的剩余容量 单位byte
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = stat.getAvailableBlocks();
            // 获取单个数据块的大小（byte）
            long blockSize = stat.getBlockSize();
            return blockSize * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 创建app根目录
     * @return
     */
    public static File getDirFile (String dirPath) {
        File dirPathFile = new File(dirPath);
        if (! dirPathFile.exists())  createDirs(dirPath);
        return dirPathFile;
    }

    /**
     * 创建目录
     * @param dirPath
     */
    public static void createDirs(String dirPath) {
        File dir = new File(dirPath);
        if (dirPath != null && ! dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 文件是否存在
     * @param file
     * @return
     */
    public static boolean isFileExist(File file) {
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 下载文件
     * @param urlStr 下载url路径
     * @return
     */
    public static StringBuffer downLoad(String urlStr){
        StringBuffer sb = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //			conn.setConnectTimeout(2000);
            //			conn.setReadTimeout(3000);
            InputStream input = conn.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            String line = null;
            sb = new StringBuffer();
            while((line = in.readLine()) != null){
                sb.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;

    }

    //保存文件
    public static boolean saveFile(String fileUrl, String fileName){

        try {
            StringBuffer sb = downLoad(fileUrl);
            //保存文件
            FileOutputStream outStream = new FileOutputStream(fileName,true);
            OutputStreamWriter writer = new OutputStreamWriter(outStream,"utf-8");
            writer.write(sb.toString());
            writer.flush();
            writer.close();//关闭
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //读取文件
    public static String readFile(String fileName){

        File file = new File(fileName);
        if(file.exists()){
            InputStream inputStream = null ;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(new File(fileName)));
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);

                return new String(bytes);

            } catch (Exception e) {
            }finally{
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else return null;

        return null;

    }

    //下载图片到指定路径
    public static void downLoadImage(final String url, final String fileName, final onSucessCallback callback) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    LogUtils.i("downLoadImage", "downLoadImage");
                    File file = null;
                    Bitmap bitmap = null;
                    file = new File(fileName);
                    InputStream is = new URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    File dir = new File(dirPath);
                    if (! dir.exists()) dir.mkdir();
                    file.createNewFile();
                    FileOutputStream filStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, filStream);
                    filStream.flush();
                    filStream.close();
                    callback.onSucess(bitmap);
                } catch (Exception e) {
                    LogUtils.e("Exception", e.toString());
                }
            }
        }.start();

    }

    /**
     * 图片下载成功回调
     */
    public interface onSucessCallback {
        void onSucess(Bitmap bitmap);
    }

    //读取图片
    public static Bitmap readImage(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(fileName, options);
    }

    /**
     * 上传多个图片到服务器
     * @param path 服务器url
     * @param fileMap 图片文件
     * @return
     */
    public static String doUpload(String path, Map<String, File> fileMap) {
        String response = "";
        HttpURLConnection conn = null;
        DataOutputStream out = null;
        InputStream is = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;

        //边界设定
        String BOUNDARY = UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINE_END = "\r\n";

        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(500 * 1000);
            conn.setReadTimeout(500 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            out = new DataOutputStream(conn.getOutputStream());

            if (fileMap != null) {
                for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                    out.writeBytes(PREFIX+BOUNDARY+LINE_END);
                    out.writeBytes("Content-Disposition: form-data; name=\"" + "fileList" + "\"; filename=\"" + entry.getValue().getName() + "\"" + LINE_END);
                    out.writeBytes("Content-Type: image/jpeg" + LINE_END);
                    out.writeBytes(LINE_END);
                    fis = new FileInputStream(entry.getValue());
                    byte[] buffer = new byte[1024*4];
                    int len = -1;
                    while ((len = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.writeBytes(LINE_END);
                }

            }
            out.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            out.flush();

            LogUtils.i("response", "iuploadgfinish");
            int responseCode = conn.getResponseCode();
            LogUtils.i("responseCode", "" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }

                response = baos.toString();

            } else {
                response  = "responseCode:" + responseCode;
            }

            LogUtils.i("response", response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (out != null)
                    out.close();
                if (baos != null)
                    baos.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.disconnect();
        }

        return response;

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
