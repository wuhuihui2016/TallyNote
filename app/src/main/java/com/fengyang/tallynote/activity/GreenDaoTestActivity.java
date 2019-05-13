package com.fengyang.tallynote.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fengyang.tallynote.MyApplication;
import com.fengyang.tallynote.R;
import com.fengyang.tallynote.adapter.UserAdapter;
import com.fengyang.tallynote.greendao.User;
import com.fengyang.tallynote.greendao.UserDao;
import com.fengyang.tallynote.utils.LogUtils;

import java.util.List;

/**
 * GreenDao增删改查操作
 * Created by wuhuihui on 2019/5/13.
 */
public class GreenDaoTestActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "GreenDaoTestTag";

    private ListView listView;

    private List<User> users;
    private UserAdapter userAdapter;

    private UserDao userDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView("GreenDao数据库", R.layout.activity_user_list);

        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.emptyView));

        userDao = MyApplication.daoSession.getUserDao();

        loadUsers();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        LogUtils.i(TAG, v.getId() + "");
        switch (v.getId()) {
            case R.id.add:
                userDao.insert(new User(null, "whh0", "whh00"));  //增
                userDao.insertInTx();
                break;
            case R.id.delete:
                users = userDao.loadAll();
                if (users.size() > 0) {
                    userDao.deleteByKey(users.get(0).getId()); //删

                    //删除名字为whh的数据
//                    userDao.queryBuilder().where(UserDao.Properties.Name.eq("whh"))
//                            .buildDelete().executeDeleteWithoutDetachingEntities();
                } else Toast.makeText(activity, "没有可删除的数据啦~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.alter:
                if (users.size() > 0) {
                    userDao.update(new User(users.get(0).getId(), "whh update", "whh11")); //改

                    //把名为whh修改成名为wuhh的数据
//                    User user = userDao.queryBuilder().where(UserDao.Properties.Name.eq("whh")).build().unique();
//                    if (user != null) {
//                        user.setName("wuhh");
//                        userDao.update(user);
//                    }
                } else Toast.makeText(activity, "没有可修改的数据啦~", Toast.LENGTH_SHORT).show();
                break;
            case R.id.loadAll: //查
//                users = userDao.loadAll(); //查询所有数据

//                //查询数据中名字为whh的数据列表
//                users = userDao.queryBuilder().where(UserDao.Properties.Name.eq("whh")).list();

//                //查询数据中名字为whh且以Age为升序的数据列表
//                users = userDao.queryBuilder().where(UserDao.Properties.Name.eq("whh"))
//                        .orderDesc(UserDao.Properties.Age).list();

//                //查询数据中名字为whh且Age小于等于10的数据列表
//                users = userDao.queryBuilder().where(UserDao.Properties.Name.eq("whh"),
//                        UserDao.Properties.Age.le("10")).list();

                users = userDao.queryBuilder().limit(5).offset(1).list(); //查询所有数据仅返回5条数据并跳过第一条数据

                int count = userDao.queryBuilder().list().size(); //返回所有数据的个数
                break;
        }

        loadUsers();
    }

    /**
     * 刷新显示数据
     */
    private void loadUsers() {
        LogUtils.i(TAG, "loadUsers");
        users = userDao.loadAll();
        LogUtils.i(TAG, users.toString());
        userAdapter = new UserAdapter(activity, users);
        listView.setAdapter(userAdapter);
    }
}
