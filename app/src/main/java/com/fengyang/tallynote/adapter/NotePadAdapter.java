package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.utils.DateUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class NotePadAdapter extends BaseAdapter {

    private Activity activity;
    private List<NotePad> notePads;

    public NotePadAdapter(Activity activity, List<NotePad> notePads) {
        this.activity = activity;
        this.notePads = notePads;
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
            viewHolder.tag = (TextView) convertView.findViewById(R.id.tag);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.words = (TextView) convertView.findViewById(R.id.words);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final NotePad notePad = notePads.get(position);
        viewHolder.time.setText(DateUtils.diffTime(notePad.getTime()));
        viewHolder.tag.setText(NotePad.getTagList().get(notePad.getTag()));
        viewHolder.words.setText(notePad.getWords());

        return convertView;
    }

    class ViewHolder {
        TextView time, tag, words;
    }
}
