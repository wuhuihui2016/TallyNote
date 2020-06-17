package com.whh.tallynote.view;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.whh.tallynote.utils.LogUtils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义数字输入框
 */
public class DigitEditText extends EditText {

    public DigitEditText(Context context) {
        super(context);
    }

    public DigitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DigitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //android:inputType="numberDecimal" 输入数字
        setSingleLine(true); //单行输入
        //过滤空格
        InputFilter filter_space = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" "))
                    return "";
                else return null;
            }
        };
        //过滤特殊符号
        InputFilter filter_speChat = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                String speChat = "[`~!@#_$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）— +|{}【】‘；：”“’。，、？-]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(charSequence.toString());
                if (matcher.find()) return "";
                else return null;
            }
        };
        setFilters(new InputFilter[]{filter_space, filter_speChat});
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    removeTextChangedListener(this);//移除输入监听
                    String str = s.toString();

                    String split = "\\.";
                    if (str.split(split).length == 2 && str.split(split)[1].length() > 2) { //小数点后面只允许输入2位数，
                        str = str.split(split)[0] + "." + str.split(split)[1].substring(0, 2);
                    }
                    LogUtils.e("whh0617", "str=" + str);
                    DecimalFormat df = new DecimalFormat("0.00");
                    str = df.format(Double.parseDouble(str));
                    LogUtils.e("whh0617", "format str=" + str);
                    setText(str);
                    setSelection(str.length());
                    addTextChangedListener(this);
                }
            }
        });
    }
}
