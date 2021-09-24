package com.whh.tallynote.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.utils.AppManager;
import com.whh.tallynote.utils.Base64Utils;
import com.whh.tallynote.utils.BitmapUtils;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;
import com.whh.tallynote.utils.FileUtils;
import com.whh.tallynote.utils.ToastUtils;
import com.whh.tallynote.view.CustomImageView;
import com.whh.tallynote.view.FlowLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * 新建记事本
 * Created by wuhuihui on 2017/8/02.
 */
public class NewNotePadActivity extends BaseActivity {

    @BindView(R.id.note_tagTv)
    public TextView note_tagTv;
    @BindView(R.id.words_numTv)
    public TextView words_numTv;
    @BindView(R.id.flowLayout)
    public FlowLayout flowLayout;
    @BindView(R.id.imageView1)
    public CustomImageView imageView1;
    @BindView(R.id.imageView2)
    public CustomImageView imageView2;
    @BindView(R.id.imageView3)
    public CustomImageView imageView3;
    @BindView(R.id.wordsEt)
    public EditText wordsEt;
    @BindView(R.id.right_btn)
    public Button right_btn;
    private List<CustomImageView> imageViewList = new ArrayList<>();

    private int tag;
    private String tagStr;

    private final int requestCameraCode = 100;
    private final int requestGalleryCode = 101;
    private String filePath;
    private List<String> imgFilePaths = new ArrayList<>();

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("想写点什么...", R.layout.activity_notepad);
    }

    @Override
    protected void initView() {

        flowLayout.removeAllViews();//避免多次执行后出现重复多余View

        final List<String> tagList = NotePad.getTagList();
        note_tagTv.setText("【" + tagList.get(0) + "】");
        for (int i = 0; i < tagList.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.tag_view, null);
            TextView tagView = (TextView) view.findViewById(R.id.tagView);
            tagView.setText(tagList.get(i));
            final int finalI = i;
            tagView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < flowLayout.getChildCount(); i++) {
                        TextView textView = (TextView) flowLayout.getChildAt(i);
                        textView.setTextColor(Color.BLACK);
                    }
                    TextView textView = (TextView) flowLayout.getChildAt(finalI);
                    textView.setTextColor(Color.RED);
                    tag = finalI;
                    tagStr = tagList.get(finalI);
                    note_tagTv.setText("【" + tagStr + "】");
                }
            });
            flowLayout.addView(view);
        }

        //图片选择View
        imageViewList.clear();
        imageViewList.add(imageView1);
        imageViewList.add(imageView2);
        imageViewList.add(imageView3);
        setImageViewOnClick();
    }

    @Override
    protected void initEvent() {
        wordsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                words_numTv.setText(s.length() + " 字");
            }

            @Override
            public void afterTextChanged(Editable s) {
                words_numTv.setText(s.length() + " 字");
                if (s.length() > 0) right_btn.setEnabled(true);
                else right_btn.setEnabled(false);
            }
        });

        setRightBtnListener("发表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = wordsEt.getText().toString();
                if (!TextUtils.isEmpty(words)) {
                    NotePad notePad = new NotePad(tag, words.trim(), DateUtils.formatDateTime());
                    if (imgFilePaths.size() > 0) {
                         //记事本增加图片属性，并保存于数据库
                        notePad.setImgCount(imgFilePaths.size());
                        for (int i = 0; i < imgFilePaths.size(); i++) {
                            SoftReference<Bitmap> bitmapRef = new SoftReference<>(BitmapFactory.decodeFile(imgFilePaths.get(i)));
                            byte[] bytes = BitmapUtils.bitmapToByte(bitmapRef.get());
                            bytes = Base64Utils.encode(bytes);
                            if (i == 0) notePad.setImg1(bytes);
                            if (i == 1) notePad.setImg2(bytes);
                            if (i == 2) notePad.setImg3(bytes);
                        }
                    }
                    MyApp.notePadDBHandle.saveNotePad(notePad);
                    ToastUtils.showSucessLong(activity, "发表成功！");
                    ExcelUtils.exportNotePad(null);
                    if (getIntent().hasExtra("list")) {
                        EventBus.getDefault().post(ContansUtils.ACTION_NOTE);
                    } else {
                        AppManager.transfer(activity, List4NotePadActivity.class);
                    }
                    finish();
                } else ToastUtils.showToast(context, false, "请输入内容！");
            }
        });
    }

    /**
     * 让末位 imageView
     * 从相册或拍照获取图片
     */
    private void setImageViewOnClick() {
        int size = imgFilePaths.size();
        if (size >= 3) return;
        imageViewList.get(size).setImageViewDefault();
        imageViewList.get(size).setImageViewOnClick(new MyClickListener() {
            @Override
            public void onClick() {
                RxPermissions rxPermissions = new RxPermissions(activity);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isGranted) throws Exception {
                                if (isGranted) {
                                    DialogUtils.showMsgDialog(activity, "选择上传图片",
                                            "相册", new MyClickListener() {
                                                @Override
                                                public void onClick() { //打开相册
                                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                    intent.setType("image/*");
                                                    startActivityForResult(intent, requestGalleryCode);
                                                }
                                            }, "拍照", new MyClickListener() {
                                                @Override
                                                public void onClick() {
                                                    filePath = DateUtils.formatDate4fileName() + "_camera.jpg";
                                                    File notePadImgDir = new File(FileUtils.notePadTakeImg);
                                                    if (!notePadImgDir.exists()) notePadImgDir.mkdirs();
                                                    File file = new File(notePadImgDir, filePath);
                                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                                    startActivityForResult(intent, requestCameraCode);
                                                }
                                            });
                                } else {
                                    ToastUtils.showToast(activity, true, "相机权限未打开，请检查后重试~");
                                }
                            }
                        });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContansUtils.pauseCheckBack = true; //避免在调用系统相机返回时还需验证密码，暂停判断前后台的切换
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCameraCode) {
                if (new File(filePath).exists() && !imgFilePaths.contains(filePath))
                    imgFilePaths.add(filePath); //拍照返回的图片文件
            } else { //相册选择的图片文件
                handleImage(data);
            }
            showImgFiles();
        }
    }

    /**
     * 更新选择的图片并显示界面
     */
    private void showImgFiles() {
        int size = imgFilePaths.size();
        for (int i = 0; i < imageViewList.size(); i++) {
            if (i < size) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFilePaths.get(i));
                int finalI = i;
                imageViewList.get(i).setImageBitmap(bitmap, new MyClickListener() {
                    @Override
                    public void onClick() {
                        imgFilePaths.remove(finalI);
                        showImgFiles();
                    }
                });
            } else {
                imageViewList.get(i).setImageViewNull();
            }
        }
        if (size < 3) setImageViewOnClick();
    }

    /**
     * 处理从相册选择的图片
     *
     * @param data
     */
    @TargetApi(19)
    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        if (new File(imagePath).exists() && !imgFilePaths.contains(imagePath))
            imgFilePaths.add(imagePath); //从相册选择的图片
    }

    /**
     * 获取图片路径
     *
     * @param uri
     * @param selection
     * @return
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onDestroy() {
        //清除用于新建记事本的临时图片
        FileUtils.delete(new File(FileUtils.notePadTakeImg));
        super.onDestroy();
    }
}
