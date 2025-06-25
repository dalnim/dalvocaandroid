package com.dalread.activity;

import com.dalread.helper.AraHanjaBillingClientHelper;
import com.dalread.helper.BillingClientHelper;

public class InAppPointListActivity extends InAppPointListParentActivity {
    protected BillingClientHelper getBillingClientHelper() {
        return AraHanjaBillingClientHelper.getInstance(this);
    }
}
