package com.whh.tallynote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whh.tallynote.R;
import com.whh.tallynote.greendao.User;

import java.util.List;

/**
 * Created by wuhuihui on 2017/6/23.
 */
public class UserAdapter extends BaseAdapter {

    private Activity activity;
    private List<User> users;

    public UserAdapter(Activity activity, List<User> users) {
        this.activity = activity;
        this.users = users;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.user_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.age = (TextView) convertView.findViewById(R.id.age);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前对象
        final User user = users.get(position);
        viewHolder.name.setText(user.getUserId() + ": " + user.getName() + "  管理员ID：" + user.getAdminId());
        viewHolder.age.setText(user.getAge());

        return convertView;
    }

    class ViewHolder {
        TextView name, age;
    }
}
