package com.dalread.activity;

import android.os.Bundle;

import com.dalread.base.BasePlayListByTtsActivity;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class PlayListByTtsActivity extends BasePlayListByTtsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        return (ArrayList<IVocaFullPlayTTSItem>) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOCA_PLAYLIST);
    }

}
