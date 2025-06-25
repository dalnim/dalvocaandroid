
package com.dalread.asyntask;



public interface OnAsyncTaskListener {
	void onInitAsyncTask();
	void onPrevExecuteAsyncTask();
	Object onPostExecuteAsyncTask(int searchType, Object data);
	void onFinishAsyncTask(int searchType, Object resultData, Object data);
}
