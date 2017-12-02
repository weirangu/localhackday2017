package com.example.localhackday2017;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by zhuowei on 2017-12-02.
 */

public class TagsEditText extends TokenCompleteTextView<String> {

    public TagsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String defaultObject(String completionText) {
        return completionText;
    }
    @Override
    protected View getViewForObject(String tagName) {
        TextView textView = new TextView(getContext());
        textView.setText(tagName);
        textView.setBackgroundColor(0xffdddddd);
        return textView;
    }
}
