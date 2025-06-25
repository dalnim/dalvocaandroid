package com.dalread.activity;

import com.dalread.helper.AraMultiPlayerBillingClientHelper;
import com.dalread.helper.AraPlayerBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.util.AppFlavorUtil;

public class InAppPointListActivity extends InAppPointListParentActivity {
    protected BillingClientHelper getBillingClientHelper() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            return AraMultiPlayerBillingClientHelper.getInstance(this);
        } else {
            return AraPlayerBillingClientHelper.getInstance(this);
        }
    }
}
