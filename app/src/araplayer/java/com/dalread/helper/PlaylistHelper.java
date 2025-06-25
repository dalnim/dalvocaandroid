package com.dalread.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.EnumType;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.manager.PlaylistManager;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.util.DLog;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class PlaylistHelper {
    private Context context;

    public PlaylistHelper(Context context) {
        this.context = context;
    }

    public boolean hasPlaylist() {
        return PlaylistManager.hasPlaylist();
    }
    public boolean isAllPlaylistEmpty() {
        return PlaylistManager.isAllPlaylistEmpty();
    }

    public boolean isRandomPlayPossible(List<PlayerFileModel> fileListTotal) {
        boolean result = false;
        if (!isAllPlaylistEmpty()) {
            result = true;
        } else {
            List<PlayerFileModel> list = fileListTotal.stream()
                    .filter(model -> model.getVideoModel().isHide())
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                result = true;
            }
        }
        return result;
    }

    public void createPlaylist() {
        TypeInputDialog typeInputDialog = new TypeInputDialog(context, dialogListener);
        typeInputDialog.setTitle(context.getString(R.string.type_input_dialog_title_create_playlist));
        typeInputDialog.setSubTitle(context.getString(R.string.type_input_dialog_subtitle_create_playlist));
        typeInputDialog.show();
//        Utils.showSoftKeyboard(context);
    }

    private BaseDialogListener dialogListener = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerOk");
            String playlistNames = (String) data;

            if (Utils.isEmpty(playlistNames)) {
                ToastUtil.getInstance(context).show(R.string.toast_create_playlist_fail_empty_name);
            } else {
                String[] namesArray = playlistNames.split(",");
                int failedCount = 0;
                int allCount = 0;
                StringJoiner joiner = new StringJoiner(",");
                for (String name : namesArray) {
                    name = name.trim();
                    if (!Utils.isEmpty(name)) {
                        allCount++;
                        PlaylistModel model = PlaylistModel.createNewPlaylist(name);
                        if (model == null) {
                            failedCount++;
                            joiner.add(name);
                        }
                    }
                }

                if ((failedCount > 0) && (allCount == failedCount)) {
                    ToastUtil.getInstance(context).show(R.string.toast_create_playlist_fail_duplicated_name);
                } else {
                    String message = String.format(context.getString(R.string.toast_create_playlist_success), allCount);
                    if (failedCount > 0) {
                        int successfulCount = allCount - failedCount;
                        message = String.format(context.getString(R.string.toast_create_playlist_failed), allCount, successfulCount, failedCount);
                    }
                    ToastUtil.getInstance(context).show(message);

                    dialog.dismiss();
                    //생성실패한 이름은 보여줄려고 했는데, 이미 있어서 실패한거랑 구분하기 힘들어서 안한다.
//                    DialogUtil.showCopyTextDialog(context, "생성실패한 플레이 리스트", joiner.toString());
                }
            }
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerCancel");
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            DLog.d("", "onBaseDialogListenerClick");
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
            DLog.d("", "onBaseDialogListenerClickMulti");
        }
    };

    public void showDeletePlaylistDialog() {
        List<PlaylistModel> models = PlaylistManager.getAllPlaylistModels();
        if (Utils.isEmpty(models)) {
            return;
        }
        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        List<Integer> preCheckedIndexes = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
            checkedItems[i] = preCheckedIndexes.contains(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title_choose_to_delete))
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        checkedItems[indexSelected] = isChecked;
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<PlaylistModel> selectedModels = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedModels.add(models.get(i));
                            }
                        }
                        boolean isDeleteSuccess = false;
                        for (PlaylistModel model : selectedModels) {
                            DLog.d("", "Selected model: " + model.getName());
                            PlaylistManager.deletePlaylistModelById(model.getPlayListId());
                            isDeleteSuccess = true;
                        }
                        if (isDeleteSuccess) {
                            ToastUtil.getInstance(context).show(R.string.toast_selected_videos_removed);
                        } else {
                            ToastUtil.getInstance(context).show(R.string.toast_no_selected_videos_to_remove);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    public void refreshFilePathsInPlaylist() {
        List<PlaylistModel> playListModels = PlaylistManager.getAllPlaylistModels();
        for (PlaylistModel playlistModel : playListModels) {
            playlistModel.refreshFilePaths();
        }
    }
    public void deleteSelectedItemsFromPlaylist(List<PlayerFileModel> selectedVideos, List<PlaylistModel> selectedPlaylists) {
        for (PlayerFileModel fileModel : selectedVideos) {
            for (PlaylistModel playlistModel : selectedPlaylists) {
                playlistModel.removeFilePath(fileModel.getPath());
            }
        }
    }
    public boolean deleteSelectedItemFromAllPlaylists(String filePath) {
        boolean result = false;
        List<PlaylistModel> playListModels = PlaylistManager.getAllPlaylistModels();
        for (PlaylistModel playlistModel : playListModels) {
            playlistModel.removeFilePath(filePath);
            result = true;
        }
        return result;
    }
    public void addSelectedItemsInPlaylist(List<PlayerFileModel> selectedVideos) {
        List<PlaylistModel> models = PlaylistManager.getAllPlaylistModels();
        if (Utils.isEmpty(models)) {
            return;
        }
        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
        }
        List<String> videoPaths = new ArrayList<>();
        selectedVideos.forEach(video -> {
            videoPaths.add(video.getVideoModel().getPath());
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title))
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        checkedItems[indexSelected] = isChecked;
                    }
                })
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<PlaylistModel> selectedModels = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedModels.add(models.get(i));
                            }
                        }
                        boolean result = false;
                        List<PlaylistModel> beforeModels = PlaylistManager.getAllPlaylistModels();
                        beforeModels.forEach(model -> {
                            DLog.d("", model.getName());
                            DLog.d("", model.getFilePaths().toString());
                        });
                        for (PlaylistModel model : selectedModels) {
                            DLog.d("", "Selected model: " + model.getName());
                            model.addFilePaths(videoPaths);
                            PlaylistManager.savePlaylistModel(model);
                            result = true;
                        }
                        List<PlaylistModel> afterModels = PlaylistManager.getAllPlaylistModels();
                        afterModels.forEach(model -> {
                            DLog.d("", model.getName());
                            DLog.d("", model.getFilePaths().toString());
                        });

                        if (result) {
                            ToastUtil.getInstance(context).show(R.string.toast_selected_videos_added_to_playlist);
                        } else {
                            ToastUtil.getInstance(context).show(R.string.toast_need_to_select_videos_to_add_playlist);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    public void selectPlaylists(boolean isSingleChoice, boolean showEmptyListToo, int resIdPositive, int resIdNegative, PlaylistSelectionCallback callback) {
        List<PlaylistModel> modelsTemp = PlaylistManager.getAllPlaylistModels();

        if (!showEmptyListToo) {
            modelsTemp = modelsTemp.stream()
                    .filter(model -> model.getFilePathCount() > 0)
                    .collect(Collectors.toList());
        }

        if (Utils.isEmpty(modelsTemp)) {
            if (callback != null) {
                callback.onPlaylistsSelected(new ArrayList<>());
            }
            return;
        }

        List<PlaylistModel> models = modelsTemp;

        String[] items = new String[models.size()];
        boolean[] checkedItems = new boolean[models.size()];
        for (int i = 0; i < models.size(); i++) {
            items[i] = models.get(i).getName() + " (" + models.get(i).getFilePathCount() + ")";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MultiMultiChoiceDialog);
        builder.setTitle(context.getString(R.string.playlist_dialog_title));

        if (isSingleChoice) {
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected) {
                    // 단일 선택 시 checkedItems 배열을 업데이트하지 않음
                    // 사용자 인터페이스에 대한 처리는 setPositiveButton에서 진행
                    checkedItems[indexSelected] = true;
                }
            });
        } else {
            builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                    checkedItems[indexSelected] = isChecked;
                }
            });
        }

        builder.setPositiveButton(resIdPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                List<PlaylistModel> selectedModels = new ArrayList<>();
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        selectedModels.add(models.get(i));
                    }
                }
                if (isSingleChoice && selectedModels.isEmpty()) {
                    //만약 아무것도 선택을 안했으면 첫번째걸 넣어준다. (하나만 고르는거라서 첫번째가 디폴트로 선택되어 있다)
                    selectedModels.add(models.get(0));
                }
                if (selectedModels.isEmpty()) {
                    ToastUtil.getInstance(context).show(R.string.toast_no_playlist_selected);
                } else {
                    if (callback != null) {
                        callback.onPlaylistsSelected(selectedModels);
                    }
                }
            }
        })
        .setNegativeButton(resIdNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (callback != null) {
                    callback.onPlaylistsSelected(new ArrayList<>()); // 빈 리스트 반환
                }
            }
        })
        .create()
        .show();
    }

    public interface PlaylistSelectionCallback {
        void onPlaylistsSelected(List<PlaylistModel> selectedPlaylists);
    }
}
