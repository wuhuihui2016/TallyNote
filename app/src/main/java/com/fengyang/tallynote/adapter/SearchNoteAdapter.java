package com.fengyang.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.model.DayNote;
import com.fengyang.tallynote.model.MemoNote;
import com.fengyang.tallynote.model.NotePad;
import com.fengyang.tallynote.model.SearchNote;
import com.fengyang.tallynote.utils.ContansUtils;
import com.fengyang.tallynote.utils.DateUtils;

import java.util.List;

/**
 * 搜索显示
 * Created by wuhuihui on 2017/6/23.
 */
public class SearchNoteAdapter extends BaseAdapter {

    private Activity activity;
    private List<SearchNote> searchNotes;

    public SearchNoteAdapter(Activity activity, List<SearchNote> searchNotes) {
        this.activity = activity;
        this.searchNotes = searchNotes;
    }


    @Override
    public int getCount() {
        return searchNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return searchNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.search_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.type = (ImageView) convertView.findViewById(R.id.type);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final SearchNote searchNote = searchNotes.get(position);
        if (searchNote.getType() == ContansUtils.DAY) {
            viewHolder.type.setImageResource(R.drawable.day_record);
            DayNote dayNote = (DayNote) searchNote.getObject();
            viewHolder.info.setText(DayNote.getUserTypeStr(dayNote.getUseType()) + dayNote.getRemark());
            viewHolder.time.setText(DateUtils.showTime4Detail(dayNote.getTime()));

        } else if (searchNote.getType() == ContansUtils.MEMO) {
            viewHolder.type.setImageResource(R.drawable.memo);
            MemoNote memoNote = (MemoNote) searchNote.getObject();
            viewHolder.info.setText(memoNote.getContent());
            if (memoNote.getStatus() == MemoNote.OFF) viewHolder.info.append("\n[已完成]");
            viewHolder.time.setText(DateUtils.showTime4Detail(memoNote.getTime()));

        } else {
            viewHolder.type.setImageResource(R.drawable.notepad);
            NotePad notePad = (NotePad) searchNote.getObject();
            viewHolder.info.setText("【" + NotePad.getTagList().get(notePad.getTag()) + "】\n" + notePad.getWords());
            viewHolder.time.setText(DateUtils.showTime4Detail(notePad.getTime()));
        }


        return convertView;
    }

    class ViewHolder {
        ImageView type;
        TextView info,time;
    }
}
