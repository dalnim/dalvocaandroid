package com.dalread.activity;

import android.os.Bundle;

import com.dalread.base.BasePlayListByTtsActivity;
import com.dalread.component.Toolbar;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.ArrayList;
import java.util.List;

public class PlayListByTtsActivity extends BasePlayListByTtsActivity {
    @Override
    protected Toolbar getToolbar() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaCoreItemList);
        return getFullItemsByInputOrder(iVocaFullPlayTTSItemList, vocaCoreItemList);
    }

}
