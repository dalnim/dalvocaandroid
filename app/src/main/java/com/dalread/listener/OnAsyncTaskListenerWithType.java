
package com.dalread.listener;

import com.dalread.asyntask.OnAsyncTaskListener;

public interface OnAsyncTaskListenerWithType extends OnAsyncTaskListener {
    void onInitAsyncTask(int searchType);
}
