package com.whh.tallynote.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.utils.Base64Utils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.MyClickListener;
import com.whh.tallynote.utils.DialogUtils;
import com.whh.tallynote.utils.ExcelUtils;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class NotePadAdapter extends BaseAdapter {

    private Activity activity;
    private List<NotePad> notePads;
    private boolean isShowTag;

    public NotePadAdapter(Activity activity, List<NotePad> notePads, boolean isShowTag) {
        this.activity = activity;
        this.notePads = notePads;
        this.isShowTag = isShowTag;
    }

    @Override
    public int getCount() {
        return notePads.size();
    }

    @Override
    public Object getItem(int position) {
        return notePads.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.notepad_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.words = (TextView) convertView.findViewById(R.id.words);
            viewHolder.image_layout = (LinearLayout) convertView.findViewById(R.id.image_layout);
            viewHolder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
            viewHolder.imageView2 = (ImageView) convertView.findViewById(R.id.imageView2);
            viewHolder.imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);
            viewHolder.del = (TextView) convertView.findViewById(R.id.del);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final NotePad notePad = notePads.get(position);
        viewHolder.time.setText(DateUtils.diffTime(notePad.getTime()));
        String content;
        if (isShowTag) {
            content = "[" + NotePad.getTagList().get(notePad.getTag()) + "] " + notePad.getWords();
        } else {
            content = notePad.getWords();
        }
        viewHolder.words.setText(content);
        if (notePad.getImgCount() == 0) {
            viewHolder.image_layout.setVisibility(View.GONE);
        } else {
            viewHolder.image_layout.setVisibility(View.VISIBLE);
            SoftReference<Bitmap> bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg1()));
            viewHolder.imageView1.setImageBitmap(bitmapRef.get());
            if (notePad.getImgCount() > 1) {
                bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg2()));
                viewHolder.imageView2.setImageBitmap(bitmapRef.get());
            }
            if (notePad.getImgCount() > 2) {
                bitmapRef = new SoftReference<>(Base64Utils.decode2Bitmap(notePad.getImg3()));
                viewHolder.imageView3.setImageBitmap(bitmapRef.get());
            }
        }
        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                        "删除", new MyClickListener() {
                            @Override
                            public void onClick() {
                                MyApp.notePadDBHandle.delNotePad(notePad);
                                ExcelUtils.exportNotePad(null);
                                notePads.remove(notePad);
                                notifyDataSetChanged();
                            }
                        }, "取消", new MyClickListener() {
                            @Override
                            public void onClick() {
                            }
                        });
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView time, words, del;
        LinearLayout image_layout;
        ImageView imageView1, imageView2, imageView3;
    }
}
