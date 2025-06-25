package com.dalread.composition;

import android.app.Activity;

import com.dalread.database.sqlite.model.DicModel;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.util.DLog;

import java.util.ArrayList;
import java.util.List;

public class PlayTTS extends AbstractPlayTTS{
    public PlayTTS(Activity activity) {
        super(activity);
    }

    //Don't delete before testing AraPlayer.
    //This is for AraPlayer
//    public void preparePlayPlayer(List<DicModel> list,
//                                  boolean isPlayingMulti) {
    public void preparePlayPlayer(List<DicModel> list) {
        DLog.d("", "preparePlayPlayer - size=" + list.size());
        playTTSHelper.setAraPlayer(true);
        playTTSHelper.setIncludeMeaning(sharedPreferences.getIncludeMeaning());
        playTTSHelper.setIncludeMotherTongueSubtitle(sharedPreferences.getIncludeMotherTongueSubtitle());
        playTTSHelper.setTotalCountToPlayVoiceOrTts(Integer.parseInt(sharedPreferences.getReadCount()));
        ArrayList<IVocaFullPlayTTSItem> vocas = new ArrayList<>();
        vocas.addAll(list);
        preparePlayVoca(vocas, true);
    }
}
