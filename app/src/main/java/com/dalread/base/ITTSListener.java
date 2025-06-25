package com.dalread.base;

public interface ITTSListener {
	public void onTTSStart(String utteranceId);
	public void onTTSDone(String utteranceId);
	public void onTTSError(String utteranceId);
	public void onComplete(int size);
}
