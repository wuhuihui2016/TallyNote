package com.whh.tallynote.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static byte[] compressBitmapToJpeg(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(CompressFormat.JPEG, quality, byteArrayOutputStream);
        }

        byte[] bytes = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static Bitmap byteToBitmap(byte[] bytes) {
        return bytes != null && bytes.length != 0 ? BitmapFactory.decodeByteArray(bytes, 0, bytes.length) : null;
    }

    public static String bitmapToString(Bitmap bitmap) {
        return Base64.encodeToString(bitmapToByte(bitmap), 0);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    public static Bitmap scaleImageTo(Bitmap bitmap, int sx, int sy) {
        return scaleImage(bitmap, (float) sx / (float) bitmap.getWidth(), (float) sy / (float) bitmap.getHeight());
    }

    public static Bitmap scaleImage(Bitmap bitmap, float sx, float sy) {
        if (bitmap == null) {
            return null;
        } else {
            Matrix matrix;
            (matrix = new Matrix()).postScale(sx, sy);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
    }

    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int right;
        Bitmap bmp = Bitmap.createBitmap(right = bitmap.getHeight(), height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, right, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle((float) (right / 2), (float) (height / 2), (float) (right / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bmp;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float w = 120.0F / (float) width;
        float h = 120.0F / (float) height;
        Matrix matrix;
        (matrix = new Matrix()).postScale(w, h);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return bmp;
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        } else {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    public static boolean saveBitmap(Bitmap bitmap, String filePath) {
        return saveBitmap(bitmap, new File(filePath));
    }
}
