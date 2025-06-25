package com.dalread.activity;

import android.os.Bundle;

import com.dalread.base.BasePlayListByTtsActivity;
import com.dalread.composition.AraConvVocaKnowActivity;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.DLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayListByTtsActivity extends BasePlayListByTtsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ArrayList<IVocaFullPlayTTSItem> getFullItems(List<IVocaCoreItem> vocaCoreItemList) {
        List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaCoreItemList);
        return getFullItemsByInputOrder(iVocaFullPlayTTSItemList, vocaCoreItemList);
    }

    private ArrayList<IVocaFullPlayTTSItem> getFullItemsByInputOrder(List<IVocaFullPlayTTSItem> iVocaFullPlayTTSItemList, List<IVocaCoreItem> vocaCoreItemList) {
        Map<String, IVocaFullPlayTTSItem> mapVoca = iVocaFullPlayTTSItemList.stream()
                .collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        for(IVocaCoreItem item : vocaCoreItemList) {
            if (mapVoca.containsKey(item.getVIVocaTypeId())) {
                resultList.add(mapVoca.get(item.getVIVocaTypeId()));
            }
        }
        return (ArrayList<IVocaFullPlayTTSItem>) resultList;
    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            stopList();
        }
    }
}
