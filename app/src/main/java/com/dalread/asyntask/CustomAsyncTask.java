package com.dalread.asyntask;

import android.content.Context;
import android.os.AsyncTask;

import com.dalread.listener.OnAsyncTaskListenerWithType;

public class CustomAsyncTask extends AsyncTask<Long, Void, Object> {

    private Context context = null;
    private int searchType = 0;
    private Object data = null;
    com.dalread.asyntask.OnAsyncTaskListener listener = null;
    private final String TAG = this.getClass().getName();
    private boolean isLoading;

    void init(Context context, com.dalread.asyntask.OnAsyncTaskListener listener, Object data, int searchType, boolean isLoading) {
        this.context = context;
        this.listener = listener;
        this.data = data;
        this.searchType = searchType;
        this.isLoading = isLoading;
    }

    public CustomAsyncTask(Context context, com.dalread.asyntask.OnAsyncTaskListener listener, Object data) {
        this(context, listener, data, 0);
    }

    public CustomAsyncTask(Context context, com.dalread.asyntask.OnAsyncTaskListener listener, Object data, int searchType) {
        init(context, listener, data, searchType, true);
        if (listener != null && isLoading) {
            if (listener instanceof OnAsyncTaskListenerWithType) {
                ((OnAsyncTaskListenerWithType) listener).onInitAsyncTask(searchType);
            } else {
                listener.onInitAsyncTask();
            }
        }
    }

    public CustomAsyncTask(Context context, com.dalread.asyntask.OnAsyncTaskListener listener, Object data, int searchType, boolean isLoading) {
        init(context, listener, data, searchType, isLoading);
        if (listener != null && isLoading) {
            if (listener instanceof OnAsyncTaskListenerWithType) {
                ((OnAsyncTaskListenerWithType) listener).onInitAsyncTask(searchType);
            } else {
                listener.onInitAsyncTask();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onPrevExecuteAsyncTask();
        }
    }

    @Override
    protected Object doInBackground(Long... params) {
        if (listener == null) {
            return null;
        }
        return listener.onPostExecuteAsyncTask(searchType, data);
    }

    @Override
    protected void onPostExecute(Object resultData) {
        if (listener != null) {
            listener.onFinishAsyncTask(searchType, resultData, data);
        }
        super.onPostExecute(resultData);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
