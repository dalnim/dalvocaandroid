package com.dalread.listener;

import com.dalread.model.HanjaItem;
import com.dalread.model.RubyTextModel;

public interface OnRubyWordClickListener {

    void onRubyWordClick(RubyTextModel rubyTextModel);
    void onRubyWordDoubleClick(RubyTextModel rubyTextModel);
}
