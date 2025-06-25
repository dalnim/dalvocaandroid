package com.dalread.base;

import android.view.View;

import com.dalread.activity.IctTermInfoActivity;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;

public class BaseIctTermFragment extends BaseFragment{
    @Override
    protected View getContentView() {
        return null;
    }

    protected void openHanjaInfoView(Object object) {
        if (object == null)
            return;

        if (object instanceof DIC_ICT_TERM) {
            openNewScreen(
                    IctTermInfoActivity.createIntent(getActivity(), (DIC_ICT_TERM) object)
            );
        }
    }
}
