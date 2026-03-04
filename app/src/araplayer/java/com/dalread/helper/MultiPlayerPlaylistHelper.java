package com.dalread.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerPlaylistModel;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 멀티플레이어 전용 플레이리스트 헬퍼. SQLite(MultiPlayerDatabase)만 사용. Realm 미사용.
 */
public class MultiPlayerPlaylistHelper {
    private final Context context;
    private final MultiPlayerDatabase multiPlayerDatabase;

    public MultiPlayerPlaylistHelper(Context context, MultiPlayerDatabase multiPlayerDatabase) {
        this.context = context;
        this.multiPlayerDatabase = multiPlayerDatabase;
    }

    public boolean hasPlaylist() {
        if (multiPlayerDatabase == null) return false;
        return !multiPlayerDatabase.getAllPlaylists().isEmpty();
    }

    public boolean isAllPlaylistEmpty() {
        if (multiPlayerDatabase == null) return true;
        List<MultiPlayerPlaylistModel> list = multiPlayerDatabase.getAllPlaylists().stream()
                .filter(m -> m.getFilePathCount() > 0)
                .collect(Collectors.toList());
        return list.isEmpty();
    }

    public boolean isRandomPlayPossible(List<PlayerFileModel> fileListTotal) {
        if (!isAllPlaylistEmpty()) return true;
        if (fileListTotal == null) return false;
        return fileListTotal.stream().anyMatch(m -> m.getVideoModel() != null && m.getVideoModel().isHide());
    }

    public void createPlaylist() {
        TypeInputDialog typeInputDialog = new TypeInputDialog(context, dialogListener);
        typeInputDialog.setTitle(context.getString(R.string.type_input_dialog_title_create_playlist));
        typeInputDialog.setSubTitle(context.getString(R.string.type_input_dialog_subtitle_create_playlist));
        typeInputDialog.show();
    }

    private final BaseDialogListener dialogListener = new BaseDialogListener() {
        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {}

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            String playlistNames = (String) data;
            if (Utils.isEmpty(playlistNames)) {
                ToastUtil.getInstance(context).show(R.string.toast_create_playlist_fail_empty_name);
                return;
            }
            String[] namesArray = playlistNames.split(",");
            int failedCount = 0;
            int allCount = 0;
            for (String name : namesArray) {
                name = name.trim();
                if (Utils.isEmpty(name)) continue;
                allCount++;
                if (multiPlayerDatabase.isPlaylistNameExists(name)) {
                    failedCount++;
                } else {
                    long id = multiPlayerDatabase.insertPlaylist(name);
                    if (id < 0) failedCount++;
                }
            }
            if (failedCount > 0 && allCount == failedCount) {
                ToastUtil.getInstance(context).show(R.string.toast_create_playlist_fail_duplicated_name);
            } else {
                String message = String.format(context.getString(R.string.toast_create_playlist_success), allCount);
                if (failedCount > 0) {
                    int successfulCount = allCount - failedCount;
                    message = String.format(context.getString(R.string.toast_create_playlist_failed), allCount, successfulCount, failedCount);
                }
                ToastUtil.getInstance(context).show(message);
                dialog.dismiss();
            }
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {}

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {}

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {}
    };

    public void showDeletePlaylistDialog() {
        List<MultiPlayerPlaylistModel> models = multiPlayerDatabase.getAllPlaylists();
        if (Utils.isEmpty(models)) return;
        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title_choose_to_delete))
                .setMultiChoiceItems(items, checkedItems, (dialog, indexSelected, isChecked) -> checkedItems[indexSelected] = isChecked)
                .setPositiveButton(R.string.delete, (dialog, id) -> {
                    List<MultiPlayerPlaylistModel> toDelete = new ArrayList<>();
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) toDelete.add(models.get(i));
                    }
                    for (MultiPlayerPlaylistModel model : toDelete) {
                        multiPlayerDatabase.deletePlaylist(model.getId());
                    }
                    if (!toDelete.isEmpty()) {
                        ToastUtil.getInstance(context).show(R.string.toast_selected_videos_removed);
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_no_selected_videos_to_remove);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    public void refreshFilePathsInPlaylist() {
        if (multiPlayerDatabase != null) {
            multiPlayerDatabase.refreshFilePathsInTables();
        }
    }

    public void deleteSelectedItemsFromPlaylist(List<PlayerFileModel> selectedVideos, List<MultiPlayerPlaylistModel> selectedPlaylists) {
        if (multiPlayerDatabase == null || selectedVideos == null || selectedPlaylists == null) return;
        for (PlayerFileModel fileModel : selectedVideos) {
            String path = fileModel.getPath();
            if (path == null) continue;
            for (MultiPlayerPlaylistModel playlist : selectedPlaylists) {
                multiPlayerDatabase.deletePlaylistItem(playlist.getId(), path);
            }
        }
    }

    public boolean deleteSelectedItemFromAllPlaylists(String filePath) {
        if (multiPlayerDatabase == null || filePath == null) return false;
        multiPlayerDatabase.deletePlaylistItemByFilePath(filePath);
        return true;
    }

    public void addSelectedItemsInPlaylist(List<PlayerFileModel> selectedVideos, PlaylistSelectionCallback callback) {
        List<MultiPlayerPlaylistModel> models = multiPlayerDatabase == null ? new ArrayList<>() : multiPlayerDatabase.getAllPlaylists();
        if (Utils.isEmpty(models)) return;
        List<String> videoPaths = new ArrayList<>();
        for (PlayerFileModel v : selectedVideos) {
            if (v != null && v.getVideoModel() != null && v.getVideoModel().getPath() != null) {
                videoPaths.add(v.getVideoModel().getPath());
            }
        }
        if (videoPaths.isEmpty()) return;
        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title))
                .setMultiChoiceItems(items, checkedItems, (dialog, indexSelected, isChecked) -> checkedItems[indexSelected] = isChecked)
                .setPositiveButton(R.string.add, (dialog, id) -> {
                    boolean added = false;
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            multiPlayerDatabase.addFilePathsToPlaylist(models.get(i).getId(), videoPaths);
                            added = true;
                        }
                    }
                    if (added) {
                        ToastUtil.getInstance(context).show(R.string.toast_selected_videos_added_to_playlist);
                    } else {
                        ToastUtil.getInstance(context).show(R.string.toast_need_to_select_videos_to_add_playlist);
                    }
                    if (callback != null) callback.onPlaylistsSelected(new ArrayList<>());
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    if (callback != null) callback.onPlaylistsSelected(new ArrayList<>());
                })
                .create()
                .show();
    }

    public void selectPlaylists(boolean isSingleChoice, boolean showEmptyListToo, int resIdPositive, int resIdNegative, PlaylistSelectionCallback callback) {
        List<MultiPlayerPlaylistModel> modelsTemp = multiPlayerDatabase == null ? new ArrayList<>() : multiPlayerDatabase.getAllPlaylists();
        if (!showEmptyListToo) {
            modelsTemp = modelsTemp.stream().filter(m -> m.getFilePathCount() > 0).collect(Collectors.toList());
        }
        if (Utils.isEmpty(modelsTemp)) {
            if (callback != null) callback.onPlaylistsSelected(new ArrayList<>());
            return;
        }
        final List<MultiPlayerPlaylistModel> models = modelsTemp;
        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title));
        if (isSingleChoice) {
            builder.setSingleChoiceItems(items, -1, (dialog, indexSelected) -> {
                for (int i = 0; i < checkedItems.length; i++) checkedItems[i] = (i == indexSelected);
            });
        } else {
            builder.setMultiChoiceItems(items, checkedItems, (dialog, indexSelected, isChecked) -> checkedItems[indexSelected] = isChecked);
        }
        builder.setPositiveButton(resIdPositive, (dialog, id) -> {
            List<MultiPlayerPlaylistModel> selected = new ArrayList<>();
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) selected.add(models.get(i));
            }
            if (isSingleChoice && selected.isEmpty() && !models.isEmpty()) {
                selected.add(models.get(0));
            }
            if (selected.isEmpty()) {
                ToastUtil.getInstance(context).show(R.string.toast_no_playlist_selected);
            } else if (callback != null) {
                callback.onPlaylistsSelected(selected);
            }
        });
        builder.setNegativeButton(resIdNegative, (dialog, id) -> {
            if (callback != null) callback.onPlaylistsSelected(new ArrayList<>());
        });
        builder.create().show();
    }

    public interface PlaylistSelectionCallback {
        void onPlaylistsSelected(List<MultiPlayerPlaylistModel> selectedPlaylists);
    }
}
