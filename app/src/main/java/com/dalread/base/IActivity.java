package com.dalread.base;

import android.content.Intent;
import android.os.Bundle;

import com.dalread.network.events.ErrorEvent;
import com.dalread.network.events.SuccessEvent;

public interface IActivity {

	public void onIOnCreate(Bundle savedInstanceState);
	public void onIRestart();
	public void onIStart();
	public void onIResume();
	public void onIPause();
	public void onIStop();
	public void onIDestroy();
	public void onIActivityResult(int requestCode, int resultCode, Intent data);
	public void onIBackPressed();
	public void onErrorEvent(ErrorEvent event);
	public void onSuccessEvent(SuccessEvent event);
	public void onIUserLeaveHint();
}
