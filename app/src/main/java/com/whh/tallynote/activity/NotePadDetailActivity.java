package com.whh.tallynote.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.base.BaseActivity;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.utils.Base64Utils;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.SoftReference;

import butterknife.BindView;

/**
 * 记事本详情
 * Created by wuhuihui on 2017/8/02.
 */
public class NotePadDetailActivity extends BaseActivity {

    @BindView(R.id.tag)
    public TextView tag;
    @BindView(R.id.time)
    public TextView time;
    @BindView(R.id.words)
    public TextView words;
    @BindView(R.id.image_layout)
    public LinearLayout image_layout;
    @BindView(R.id.imageView1)
    public ImageView imageView1;
    @BindView(R.id.imageView2)
    public ImageView imageView2;
    @BindView(R.id.imageView3)
    public ImageView imageView3;

    private NotePad notePad;

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView("记事本", R.layout.activity_notepad_detail);
    }

    @Override
    protected void initView() {
        notePad = (NotePad) getIntent().getSerializableExtra("notepad");

        if (notePad != null) {
            tag.setText(NotePad.getTagList().get(notePad.getTag()));
            time.setText(DateUtils.showTime4Detail(notePad.getTime()));
            words.setText(notePad.getWords());

            if (notePad.getImgCount() == 0) {
                image_layout.setVisibility(View.GONE);
            } else {
                image_layout.setVisibility(View.VISIBLE);
                SoftReference<Bitmap> bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg1()));
                imageView1.setImageBitmap(bitmapRef.get());
                if (notePad.getImgCount() > 1) {
                    bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg2()));
                    imageView2.setImageBitmap(bitmapRef.get());
                }
                if (notePad.getImgCount() > 2) {
                    bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg3()));
                    imageView3.setImageBitmap(bitmapRef.get());
                }
            }

            setRightImgBtnListener(R.drawable.note_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                            "删除", new MyClickListener() {
                                @Override
                                public void onClick() {
                                    MyApp.notePadDBHandle.delNotePad(notePad);
                                    ExcelUtils.exportNotePad(null);
                                    EventBus.getDefault().post(ContansUtils.ACTION_NOTE);
                                    finish();

                                }
                            }, "取消", new MyClickListener() {
                                @Override
                                public void onClick() {
                                }
                            });
                }
            });

        } else finish();

    }

    @Override
    protected void initEvent() {

    }

}
