package com.dalread.base;

import android.view.View;

public interface BaseDialogListener {
	abstract void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data);
	abstract void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data);
	abstract void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data);
	abstract void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data);
	abstract void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data);
}
