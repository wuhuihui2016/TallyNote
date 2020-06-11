package com.whh.tallynote.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * BaseFragment
 */
public class BaseFragment extends Fragment {

    protected Activity activity;
    protected View content;//内容布局

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

}
