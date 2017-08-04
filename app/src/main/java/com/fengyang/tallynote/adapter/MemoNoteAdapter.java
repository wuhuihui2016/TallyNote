package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.utils.DateUtils;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class MemoNoteAdapter extends BaseAdapter {

    private Activity activity;
    private List<MemoNote> memoNotes;

    public MemoNoteAdapter(Activity activity, List<MemoNote> memoNotes) {
        this.activity = activity;
        this.memoNotes = memoNotes;
    }


    @Override
    public int getCount() {
        return memoNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return memoNotes.get(position);
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
            viewHolder.content = (TextView) convertView.findViewById(R.id.words);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final MemoNote memoNote = memoNotes.get(position);
        viewHolder.time.setText(DateUtils.diffTime(memoNote.getTime()));
        viewHolder.content.setText(memoNote.getContent());

        return convertView;
    }

    class ViewHolder {
        TextView time, content;
    }
}
