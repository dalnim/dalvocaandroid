package com.dalread.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.dalread.R;
import com.dalread.util.MediaFolderUtil;

import java.util.ArrayList;
import java.util.List;

public class AraMultiChoiceDialog {
    public interface OnSelectionListener {
        void onSelection(List<String> selectedFolders);
    }
    public static void showMediaFolderSelectionDialog(Context context, final OnSelectionListener listener) {
        String[] allItems = MediaFolderUtil.getAllMediaFolders(context);
        final boolean[] checkedItems = MediaFolderUtil.getCheckedItems(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        AlertDialog dialog = builder.setTitle(R.string.dialog_title_select_media_folder_for_video)
                .setMultiChoiceItems(allItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        checkedItems[indexSelected] = isChecked;
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<String> selectedFolders = new ArrayList<>();
                        String[] mediaFolders = allItems;
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedFolders.add(mediaFolders[i]);
                            }
                        }
                        MediaFolderUtil.saveSelectedFolders(context, selectedFolders);
                        if (listener != null) {
                            listener.onSelection(selectedFolders);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnDismissListener(null);
        dialog.show();
    }
}
