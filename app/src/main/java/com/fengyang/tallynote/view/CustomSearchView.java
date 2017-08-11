package com.fengyang.tallynote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fengyang.tallynote.R;


public class CustomSearchView extends RelativeLayout {

	public EditText edit;
	public ImageButton clear;

	public CustomSearchView(Context context) {
		super(context);
	}

	public CustomSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
		edit = (EditText) findViewById(R.id.edit);
		clear = (ImageButton) findViewById(R.id.clear);
	}

	//设置输入hint
	public void setHint(String hint) { 
		edit.setHint(hint);
	} 
	//设置输入字体大小
	public void setEditTextSize(float size) { 
		edit.setTextSize(size);
	} 
	//设置输入内容
	public void setText(String text) {
		edit.setText(text);
	} 
	//获取输入内容
	public String getText() {
		return edit.getText().toString();
	} 

}

