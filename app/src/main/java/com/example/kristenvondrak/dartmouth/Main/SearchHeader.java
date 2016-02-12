package com.example.kristenvondrak.dartmouth.Main;

import android.widget.EditText;

import java.util.List;

/**
 * Created by kristenvondrak on 2/7/16.
 */
public interface SearchHeader {

    void onSearchClick();
    void onCancelSearchClick();
    void onSearchEditTextChanged(String text, int start, int before);
    void onEnterClick();
    void onClearSearchClick();
}
