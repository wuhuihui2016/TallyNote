package com.fengyang.tallynote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengyang.tallynote.R;
import com.fengyang.tallynote.utils.FileUtils;
import com.fengyang.tallynote.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wuhuihui on 2017/7/5.
 */
public class FileExplorerAdapter extends BaseAdapter {

    private Context context;
    private List<File> fileList;
    public List<File> selList = new ArrayList<>();
    private boolean isSelect;

    public void setSelect(boolean select) {
        isSelect = select;
        notifyDataSetChanged();
    }

    public FileExplorerAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }


    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.file_explorer_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.info = (TextView) convertView.findViewById(R.id.info);
                viewHolder.select = (ImageView) convertView.findViewById(R.id.select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final File file = fileList.get(position);
            viewHolder.name.setText(file.getName());
            String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(file.lastModified()));
            viewHolder.info.setText(FileUtils.FormatFileSize(FileUtils.getFileSize(file)) + " | " + time);

            if (isSelect) {
                viewHolder.select.setVisibility(View.VISIBLE);
                if (selList.contains(file))
                    viewHolder.select.setImageResource(R.drawable.file_selected);
                else viewHolder.select.setImageResource(R.drawable.file_unselect);
            } else viewHolder.select.setVisibility(View.GONE);

        } catch (Exception e) {
            LogUtils.e("FileExplorerAdapter-getView", e.toString());
            e.printStackTrace();
        }

        return convertView;
    }


    /**
     * @param file
     * @return void
     * @Title: setSelected
     * @Description: TODO 设置选取与舍弃
     * @author wuhuihui
     * @date 2016年6月15日 上午11:01:04
     */
    public void setSelected(File file) {
        if (selList.contains(file)) selList.remove(file);
        else selList.add(file);
        notifyDataSetChanged();
        LogUtils.i("setSelected", selList.toString());
    }

    /**
     * @return void
     * @Title: selectAll
     * @Description: TODO 全选
     * @author wuhuihui
     * @date 2016年6月15日 上午11:05:14
     */
    public void selectAll(boolean isAll) {
        selList.clear();
        if (isAll) selList.addAll(fileList);
        else selList.clear();
        notifyDataSetChanged();
        LogUtils.i("selectAll", selList.toString());
    }

    class ViewHolder {
        TextView name, info;
        ImageView select;
    }
}