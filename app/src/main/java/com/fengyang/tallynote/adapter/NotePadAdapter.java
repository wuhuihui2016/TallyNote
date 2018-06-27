package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.database.NotePadDao;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.DateUtils;
import com.fengyang.tallynote.utils.DialogListener;
import com.fengyang.tallynote.utils.DialogUtils;
import com.fengyang.tallynote.utils.ExcelUtils;

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
        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.showMsgDialog(activity, "是否确定删除此条记录",
                        "删除", new DialogListener() {
                            @Override
                            public void onClick() {
                                NotePadDao.delNotePad(notePad);
                                ExcelUtils.exportNotePad(null);
                                notePads.remove(notePad);
                                notifyDataSetChanged();
                            }
                        }, "取消", new DialogListener() {
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
    }
}
