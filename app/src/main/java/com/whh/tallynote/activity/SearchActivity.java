package com.whh.tallynote.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.whh.tallynote.MyApp;
import com.whh.tallynote.R;
import com.whh.tallynote.adapter.SearchNoteAdapter;
import com.whh.tallynote.base.BaseActivity;
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

import butterknife.BindView;

/**
 * 搜索
 * Created by wuhuihui on 2017/8/11.
 */
public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";

    @BindView(R.id.return_btn)
    public ImageButton return_btn;
    @BindView(R.id.searchView)
    public CustomSearchView searchView;
    @BindView(R.id.search_btn)
    public Button search_btn;
    @BindView(R.id.listView)
    public ListView listView;
    @BindView(R.id.emptyView)
    public TextView emptyView;

    private List<SearchNote> searchNotes = new ArrayList<>();

    @Override
    protected void initBundleData(Bundle bundle) {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initView() {
        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setVisibility(View.GONE);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    protected void initEvent() {

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
        List<DayNote> dayNotes = MyApp.dayNoteDBHandle.getDayNotes();
        if (dayNotes.size() > 0) {
            for (int i = 0; i < dayNotes.size(); i++) {
                if (dayNotes.get(i).getRemark().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.DAY, dayNotes.get(i)));
                }
            }
        }

        //历史日账检索
        List<DayNote> dayNotess4History = MyApp.dayNoteDBHandle.getDayNotes4History();
        if (dayNotess4History.size() > 0) {
            for (int i = 0; i < dayNotess4History.size(); i++) {
                if (dayNotess4History.get(i).getRemark().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.DAY, dayNotess4History.get(i)));
                }
            }
        }

        //备忘录检索
        List<MemoNote> memoNotes = MyApp.memoNoteDBHandle.getMemoNotes();
        if (memoNotes.size() > 0) {
            for (int i = 0; i < memoNotes.size(); i++) {
                if (memoNotes.get(i).getContent().contains(key)) {
                    searchNotes.add(new SearchNote(ContansUtils.MEMO, memoNotes.get(i)));
                }
            }
        }

        //记事本检索
        List<NotePad> notePads = MyApp.notePadDBHandle.getNotePads();
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
            listView.setEmptyView(emptyView);
        }

    }
}
