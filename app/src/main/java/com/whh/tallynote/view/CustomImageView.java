package com.whh.tallynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.whh.tallynote.R;
import com.whh.tallynote.utils.MyClickListener;

/**
 * 添加图片的自定义View
 * 用于新建记事本增删图片
 * Created by wuhuihui on 2021/9/18.
 */
public class CustomImageView extends RelativeLayout {

    private ImageView imageView, clear;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.image_view, this, true);
        imageView = (ImageView) findViewById(R.id.imageView);
        clear = (ImageView) findViewById(R.id.clear);
    }

    /**
     * 设置 imageView 的点击事件
     *
     * @param listener
     */
    public void setImageViewOnClick(MyClickListener listener) {
        if (listener == null) return;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick();
            }
        });
    }

    /**
     * 设置 imageView 的默认显示图片
     */
    public void setImageViewDefault() {
        imageView.setImageResource(R.drawable.camera);
    }

    /**
     * 设置 imageView 的无图片显示
     */
    public void setImageViewNull() {
        imageView.setImageBitmap(null);
        clear.setVisibility(View.GONE);
    }

    /**
     * 设置 imageView 的图像显示，并将清除按钮可见，点击清除将清除图像
     *
     * @param bitmap
     * @param clearListener
     */
    public void setImageBitmap(Bitmap bitmap, MyClickListener clearListener) {
        imageView.setImageBitmap(bitmap);
        clear.setVisibility(View.VISIBLE);
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                clear.setVisibility(View.GONE);
                clearListener.onClick();
            }
        });
    }
}

