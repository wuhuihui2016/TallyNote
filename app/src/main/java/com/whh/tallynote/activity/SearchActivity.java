package com.whh.tallynote.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.whh.tallynote.R;
import com.whh.tallynote.adapter.SearchNoteAdapter;
import com.whh.tallynote.database.DayNoteDao;
import com.whh.tallynote.database.MemoNoteDao;
import com.whh.tallynote.database.NotePadDao;
import com.whh.tallynote.model.DayNote;
import com.whh.tallynote.model.MemoNote;
import com.whh.tallynote.model.NotePad;
import com.whh.tallynote.model.SearchNote;
import com.whh.tallynote.utils.ContansUtils;
import com.whh.tallynote.utils.DateUtils;
import com.whh.tallynote.utils.LogUtils;
import com.whh.tallynote.utils.SystemUtils;
import com.whh.tallynote.view.CustomSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索
 * Created by wuhuihui on 2017/8/11.
 */
public class SearchActivity extends Activity {

    private static final String TAG = "SearchActivity";

    private CustomSearchView searchView;
    private Button search_btn;
    private ListView listView;

    private List<SearchNote> searchNotes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
    }

    /**
     * @param
     * @return void
     * @Title: initView 初始化界面
     * @Description: TODO
     * @author wuhuihui
     */
    private void initView() {

        findViewById(R.id.return_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setVisibility(View.GONE);

        search_btn = (Button) findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchView = (CustomSearchView) findViewById(R.id.searchView);
        searchView.setHint("搜索");
        searchView.edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    searchView.clear.setVisibility(View.VISIBLE);
                    searchView.clear.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            searchView.edit.setText("");
                            listView.setVisibility(View.GONE);
                        }
                    });
                    search_btn.setText("搜索");
                    search_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Search4Notes();
                        }
                    });
                } else {
                    searchView.clear.setVisibility(View.GONE);
                    search_btn.setText("取消");
                    search_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /**
     * 显示搜索结果
     */
    private void Search4Notes() {

        SystemUtils.hideInput(this);

        listView.setVisibility(View.VISIBLE);

        String key = searchView.getText().replaceAll(" ", "");
        LogUtils.i(TAG + "-Search4Notes", "key--" + key);

        searchNotes.clear(); //清空数据

        //日账检索
        List<DayNote> dayNotes = DayNoteDao.getDayNotes();
        if (dayNotes.size() > 0) {
            for (int i = 0; i < dayNotes.size(); i++) {
                if (dayNotes.get(i).getRemark().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.DAY, dayNotes.get(i)));
                }
            }
        }

        //历史日账检索
        List<DayNote> dayNotess4History = DayNoteDao.getDayNotes4History();
        if (dayNotess4History.size() > 0) {
            for (int i = 0; i < dayNotess4History.size(); i++) {
                if (dayNotess4History.get(i).getRemark().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.DAY, dayNotess4History.get(i)));
                }
            }
        }

        //备忘录检索
        List<MemoNote> memoNotes = MemoNoteDao.getMemoNotes();
        if (memoNotes.size() > 0) {
            for (int i = 0; i < memoNotes.size(); i++) {
                if (memoNotes.get(i).getContent().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.MEMO, memoNotes.get(i)));
                }
            }
        }

        //记事本检索
        List<NotePad> notePads = NotePadDao.getNotePads();
        if (notePads.size() > 0) {
            for (int i = 0; i < notePads.size(); i++) {
                if (notePads.get(i).getWords().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.NOTEPAD, notePads.get(i)));
                }
            }
        }

        //显示结果
        LogUtils.i(TAG, searchNotes.size() + "--" + searchNotes.toString());
        if (searchNotes.size() > 0) {

            //按时间排序，最新的在列表上面
            searchNotes = DateUtils.sortData(searchNotes);
            listView.setAdapter(new SearchNoteAdapter(this, searchNotes));

            //设置无点击效果
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    LogUtils.i(TAG, "Search4Notes-setOnItemClickListener" + position);
//                    SearchNote searchNote = searchNotes.get(position);
//
//                    if (searchNote.getType() == ContansUtils.DAY) {
//                        DayNote dayNote = (DayNote) searchNote.getObject();
//                        DialogUtils.showMsgDialog(activity, "日账\n" + DayNote.getUserType(dayNote.getUseType()) + dayNote.getRemark() );
//
//                    } else if (searchNote.getType() == ContansUtils.MEMO) {
//                        MemoNote memoNote = (MemoNote) searchNote.getObject();
//                        Intent intent = new Intent(activity, MemoNoteDetailActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("memoNote", memoNote);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//
//                    } else {
//                        NotePad notePad = (NotePad) searchNote.getObject();
//                        Intent intent = new Intent(activity, NotePadDetailActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("notepad", notePad);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    }
//                }
//            });
        } else {
            listView.setEmptyView(findViewById(R.id.emptyView));
        }

    }
}
