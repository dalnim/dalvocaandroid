package com.dalread.activity;

import com.dalread.base.BasePlayListByTtsActivity;
import com.dalread.component.Toolbar;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.ArrayList;
import java.util.List;

//컴파일 에러 방지용
public class PlayListByTtsActivity extends BasePlayListByTtsActivity {
    @Override
    protected ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        return null;
    }

    @Override
    protected Toolbar getToolbar() {
        return null;
    }
}
