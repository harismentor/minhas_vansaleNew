package com.advanced.minhas.textwatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by sadiquekolakkal on 09-05-2017.
 */

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    protected TextValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString().trim();
        validate(textView, text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
}
