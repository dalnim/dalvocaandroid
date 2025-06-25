package com.dalread.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.dalread.R;
import com.dalread.activity.DownloadSubtitlesActivity;
import com.dalread.activity.SubtitleFilesPlayerActivity;
import com.dalread.dialog.PlayerShowSubtitleOptionsDialog;
import com.dalread.model.PlayerFileModel;
import com.dalread.util.Constant;

//자막 파일을 선택하는 helper
public class SubtitleFileChooserHelper {
    public interface Listener {
        void onDetachSubtitleFile(boolean canDetach);
    }
    public enum VIEW_TYPE {
        VIDEO_INFORMATION_VIEW,
        OPTION_VIEW
    }
    private Listener listener;
    private PlayerShowSubtitleOptionsDialog playerShowSubtitleOptionsDialog;
    private SubtitleFileChooserStrategy subtitleConnectStrategy;
    private PlayerFileModel playerFileModel;
    private Context context;
    private VIEW_TYPE viewType;

    private int subPathIndex;

    public SubtitleFileChooserHelper(Context context, VIEW_TYPE viewType, Listener onPlayerSubtitleOptionsHelperListener) {
        this.context = context;
        this.viewType = viewType;
        this.listener = onPlayerSubtitleOptionsHelperListener;
        this.subtitleConnectStrategy = SubtitleFileChooserStrategyFactory.getSubtitleConnectStrategy(viewType);
       playerShowSubtitleOptionsDialog = new PlayerShowSubtitleOptionsDialog(context, onSubtitleOptionClickListener);
    }

    public void setSubPathIndex(int subPathIndex) {
        this.subPathIndex = subPathIndex;
    }

    public void setPlayerFileModel(PlayerFileModel playerFileModel) {
        this.playerFileModel = playerFileModel;
    }
    public void showSubtitleOptionsDialog() {
        playerFileModel.getVideoModel().setSubPathIndex(subPathIndex);

        playerShowSubtitleOptionsDialog.updateVisibilityDetachSubtitle(shouldShowDetachSubtitleMenu());
        playerShowSubtitleOptionsDialog.show();
    }
    private DialogInterface.OnClickListener onSubtitleOptionClickListener = (dialog, which) -> {
        switch (which) {
            case R.id.tv_select_subtitle:
                openSubtitleFiles();
                break;
            case R.id.tvDownloadSubtitle:
                openDownloadSubtitlesScreen();
                break;
            case R.id.tv_detach_subtitle:
                detachSubtitleFile(subPathIndex);
                break;
            default:
                break;
        }
    };

    private boolean shouldShowDetachSubtitleMenu() {
        return subtitleConnectStrategy.shouldShowDetachSubtitleMenu(subPathIndex, playerFileModel);
    }

    private void detachSubtitleFile(int subPathIndex) {
        boolean canDetach = subtitleConnectStrategy.detachSubtitleFile(subPathIndex, playerFileModel);
        if (listener != null) {
            listener.onDetachSubtitleFile(canDetach);
        }
    }

    private void openSubtitleFiles() {
        Intent intent = new Intent(context, SubtitleFilesPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, playerFileModel);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX, subPathIndex);
        context.startActivity(intent);
    }

    private void openDownloadSubtitlesScreen() {
        context.startActivity(new Intent(context, DownloadSubtitlesActivity.class)
                .putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel)
                .putExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX, subPathIndex));
    }
}
//
interface SubtitleFileChooserStrategy {
    boolean shouldShowDetachSubtitleMenu(int subPathIndex, PlayerFileModel playerFileModel);
    boolean detachSubtitleFile(int subPathIndex, PlayerFileModel playerFileModel);
}

class SubtitleFileChooserStrategyFactory {
    public static SubtitleFileChooserStrategy getSubtitleConnectStrategy(SubtitleFileChooserHelper.VIEW_TYPE viewType) {
        switch (viewType) {
            case VIDEO_INFORMATION_VIEW:
                return new VideoInformationViewFileChooserConnectStrategy();
            case OPTION_VIEW:
                return new OptionViewSubtitleFileChooserStrategy();
            default:
                throw new IllegalArgumentException("Invalid VIEW_TYPE value: " + viewType);
        }
    }
}


class VideoInformationViewFileChooserConnectStrategy implements SubtitleFileChooserStrategy {
    @Override
    public boolean shouldShowDetachSubtitleMenu(int subPathIndex, PlayerFileModel playerFileModel) {
        return playerFileModel.getVideoModel().hasSubPath1() || playerFileModel.getVideoModel().hasSubPath2();
    }

    @Override
    public boolean detachSubtitleFile(int subPathIndex, PlayerFileModel playerFileModel) {
        return true;
    }
}

class OptionViewSubtitleFileChooserStrategy implements SubtitleFileChooserStrategy {
    @Override
    public boolean shouldShowDetachSubtitleMenu(int subPathIndex, PlayerFileModel playerFileModel) {
        if (subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_1) {
            return playerFileModel.getVideoModel().hasSubPath1() && !playerFileModel.getVideoModel().hasSubPath2();
        } else {
            return playerFileModel.getVideoModel().hasSubPath2();
        }
    }

    @Override
    public boolean detachSubtitleFile(int subPathIndex, PlayerFileModel playerFileModel) {
        if ((subPathIndex == Constant.PLAYER.INTENT.SUBPATH_INDEX_1) && (playerFileModel.getVideoModel().hasSubPath2())) {
            return false;
        }
        return true;
    }
}

