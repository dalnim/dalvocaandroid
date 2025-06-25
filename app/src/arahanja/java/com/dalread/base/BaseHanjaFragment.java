package com.dalread.base;

import android.view.View;

import com.dalread.activity.HanjaSentenceInfoActivity;
import com.dalread.activity.HanjaWordInfoActivity;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_SENTENCE;

public class BaseHanjaFragment extends BaseFragment{
    @Override
    protected View getContentView() {
        return null;
    }

    protected void openHanjaInfoView(Object object) {
        if (object == null)
            return;

        if (object instanceof DIC_HANJA) {
            openNewScreen(
                    HanjaWordInfoActivity.createIntent(getActivity(), (DIC_HANJA) object)
            );
        } else if (object instanceof DIC_HANJA_SENTENCE) {
            openNewScreen(
                    HanjaSentenceInfoActivity.createIntent(getActivity(), (DIC_HANJA_SENTENCE) object)
            );
        }
    }
}
