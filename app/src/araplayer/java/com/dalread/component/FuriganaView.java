package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.dalread.util.Constant;

/**
 * Created by Akira on 2016/06/24.
 */
public class FuriganaView extends BaseFuriganaView {

    public FuriganaView(Context context) {
        super(context);
        initialize(context, null);
    }

    public FuriganaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public FuriganaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void settingForSubtitleInTableView(boolean displayPronunciation, int showAsteriskToDisplay, float fontSize) {
        setTutor(true); // If I set this true, AraPlaer make room above a word to dispaly meaning even if there is no meaning. 이걸 true로 하면 뜻을 표시 안해도 단어 앞뒤에 뜻의 길이만큼 간격을 많이 비워둔다.
        setDisplayPronunciation(false);
        setIsShowFurigana(Constant.SHOW_FURIGANA_OFF);
        setHideSubtitleFromAsterisk(true);
        setShowAsterisk(showAsteriskToDisplay);
        resetText();
        setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        setIsWord(false);
        setSetColorOnSubtitle(false);
    }

    public void settingForSubtitleOnFullScreen() {
        setMinLines(2);
        setTextCenterHorizontal(true);
        setTutor(true); //If I make this to false, then I can see hurigana over subtitle, but the looking was bad so I make it true not to show the hurigana.
        setShowOutline(true);
        setBaseContent(true);
        setHideSubtitleFromAsterisk(false);
    }

    public void settingForDifficutlWords(boolean displayPronunciation, int showAsteriskToDisplay, float fontSize) {
        setTutor(true);
        setDisplayPronunciation(false);
//        setIsShowFurigana(Constant.SHOW_FURIGANA_ALL); //이게 있으면 줄간격이 넓어진다.
        setTextCenterHorizontal(true);
        setHideSubtitleFromAsterisk(false);
        setShowAsterisk(showAsteriskToDisplay);
        resetText();
        setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        setIsWord(false);
    }
}