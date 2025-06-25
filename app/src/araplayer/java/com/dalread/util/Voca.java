package com.dalread.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.MinMaxSubModel;
import com.dalread.model.MinMaxSubModelBuilder;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatPlayer;

import java.io.File;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Voca extends BaseVoca {

    public static String[] getRepeatCountListenComprehensionValues() {
        return getRepeatCountValues(Constant.READ_COUNT_LISTEN_COMPREHENSION_MIN, Constant.READ_COUNT_LISTEN_COMPREHENSION_MAX);
    }

    public static String[] getPlaySubtitlesAtOnceListenComprehensionValues() {
        int count = Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_AT_ONCE_MAX - Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_AT_ONCE_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_AT_ONCE_MIN);
        }
        return result;
    }

    public static String[] getPlaySubtitlesPlayPartsListenComprehensionValues() {
        int count = Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_PLAY_PARTS_MAX - Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_PLAY_PARTS_MIN + 1;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = String.valueOf(i + Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_PLAY_PARTS_MIN);
        }
        return result;
    }

    public static String[] getRepeatCountFloatValues() {
        int count = Constant.READ_COUNT_MAX - Constant.READ_COUNT_MIN + 1;
        String[] result = new String[(count * 2) + 1];
        int index = 0;
        for (int i = count - 1; i >= 0; i--) {
            result[index] = "- " + (float) (i + Constant.READ_COUNT_MIN) / Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_INDEX;
            index++;
        }
        result[index] = "  0.0";
        index++;
        for (int i = 0; i < count; i++) {
            result[index] = "+ " + (float) (i + Constant.READ_COUNT_MIN) / Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_INDEX;
            index++;
        }
        return result;
    }

    public static VocaStudyChat createVocaStudyChat(DicModel dicModel) {
        final VocaStudyChatPlayer voca = new VocaStudyChatPlayer();
//        voca.setVocaId(dicModel.getVocaIdServer()); // Dalnim : In VocaStudyChatPlayer we need to use vocaIdServer. (If I add a new voca, vocaIdServer has the right id, vocaId still has a minus id)
        voca.setVocaId(dicModel.getVocaId());
        voca.setVocaKnow(dicModel.getVocaKnow());
        voca.setVocaKnowPronounce(dicModel.getVocaKnowPronounce());
        voca.setType(dicModel.getVocaType());
        voca.setVoca(dicModel.getVocaDisplay());
        voca.setVocaDisplay(dicModel.getVocaDisplay());
        voca.setPronounce(dicModel.getPronounce());
        voca.setMeaning(dicModel.getMeaning());
        voca.setMeaningTTS(dicModel.getMeaningTTS());
        voca.setMeaningEnglish(dicModel.getMeaningEng());
        voca.setBookmark(dicModel.getBookmark());
        voca.setJmdictMeaning(dicModel.getJmdictMeaning());
        voca.setJmdictMeaningEng(dicModel.getJmdictMeaningEng());
        voca.setEvaluateVocaGrade(Constant.BASE_BLANK);
        voca.setEvaluateVocaGradeTutors(Constant.BASE_BLANK);
        voca.setPath(dicModel.getVIPath());
        voca.setHanja(dicModel.getHanja());
        voca.setDicModel(dicModel);
        return voca;
    }

    public static void setSwitchCompatSelected(SwitchCompat sc, int value) {
        sc.setChecked(value == 1);
    }

    public static int getSwitchCompatStatusByPlayDifficult(SwitchCompat sc) {
        return sc.isChecked() ? 1 : 0;
    }

    public static boolean checkArraySelected(int[] array) {
        if (array == null) return false;
        for (int i : array) {
            if (i == 1) {
                return true;
            }
        }
        return false;
    }

    public static void updateTextViewSubtitleExtension(Context context, TextView tv, String subPath) {
        if ((subPath == null) || (subPath.trim().length() == 0)) {
            tv.setVisibility(View.GONE);
            return;
        }
        SupportSubtitleFormat subtitleFormat = FileUtil.getSubtitleExtension(subPath);
        if (subtitleFormat == SupportSubtitleFormat.NONE) {
            tv.setVisibility(View.GONE);
        } else {
            int color;
            switch (subtitleFormat) {
                case SMI:
                    color = R.color.backgroundSubtitleSMI;
                    break;
                case SRT:
                    color = R.color.backgroundSubtitleSRT;
                    break;
                default:
                    color = R.color.backgroundSubtitleOther;
                    break;
            }
            tv.setVisibility(View.VISIBLE);
            tv.setBackgroundTintList(context.getColorStateList(color));
            tv.setTextColor(ContextCompat.getColor(context, R.color.color_subtitle_extension_title));
            tv.setText(" " + subtitleFormat.toString().toUpperCase() + " ");
        }
    }

    //Dalnim : When save starTime in SQLite, need to remove Delay Time and Play Before Subtitle time.
    public static long getMinSubTimeWithoutAddedValue(long playTime, VideoModel videoModel) {
//        long value = (long) (playTime - videoModel.getDelaySubtitles() - (videoModel.getPlayBeforeSubtitle() * 1000));
        //Dalnim : CC반복시 자막 끝시간만 조절했는데, 시작시간도 바뀜(닥터 후 루돌프) minSub는 PlayBeforeTime이 없기에 계산에 추가하지 않음
        long value = (long) (playTime - videoModel.getDelaySubtitles());
        return getSubtitleTimeBetweenDuration(value, videoModel.getDuration());
//        if (value < 0) {
//            value = 0;
//        } else if (value > videoModel.getDuration()) {
//            value = videoModel.getDuration();
//        }
//        return value;
    }

    public static long getMaxSubTimeWithoutAddedValue(long playTime, VideoModel videoModel) {
//        long value = (long) (playTime - videoModel.getDelaySubtitles() - (videoModel.getPlayAfterSubtitle() * 1000));
        long value = (long) (playTime - videoModel.getDelaySubtitles());
        return getSubtitleTimeBetweenDuration(value, videoModel.getDuration());

//        if (value < 0) {
//            value = 0;
//        } else if (value > videoModel.getDuration()) {
//            value = videoModel.getDuration();
//        }
//        return value;
    }

    private static long getSubtitleTimeBetweenDuration(long playTime, long duration) {
        if (playTime < 0) {
            playTime = 0;
        } else if (playTime > duration) {
            playTime = duration;
        }
        return playTime;
    }
//    //Dalnim added : need to remove duplicate code with getStartTime
//    //Dalnim : When AraPlayer repeats dialogs, then need to get getPlayBeforeSubtitle/getPlayAfterSubtitle time. But just finding subtitles to mvoe doesn't need them
//    public static long getStartTimeWithoutBeforeTime(DicModel item, VideoModel videoModel) {
//        long startTime = 0;
//        long value = 0;
//        if (item != null) {
//            startTime = item.getStartTime();
//        }
//        value += startTime + videoModel.getDelaySubtitles();
//        if (value > videoModel.getDuration()) {
//            value = startTime;
//        }
//        if (value < 0) {
//            value = startTime < 0 ? 0 : startTime;
//        }
//        return value;
//    }
//    //Dalnim added : need to remove duplicate code with getEndTime
//    //Dalnim : When AraPlayer repeats dialogs, then need to get getPlayBeforeSubtitle/getPlayAfterSubtitle time. But just finding subtitles to mvoe doesn't need them
//    public static long getEndTimeWithoutAfterTime(DicModel item, VideoModel videoModel) {
//        long endTime = 0;
//        long value = 0;
//        if (item != null) {
//            endTime = item.getEndTime();
//        }
//        value += endTime + videoModel.getDelaySubtitles();
//        if (value < 0) {
//            value = endTime;
//        } else if (value > videoModel.getDuration()) {
//            value = videoModel.getDuration();
//        }
//        if (value < 0) {
//            value = endTime < 0 ? 0 : endTime;
//        }
//        return value;
//    }

//    public static long getStartTime(DicModel item, VideoModel videoModel) {
//        long startTime = 0;
//        long value = 0;
//        if (item != null) {
//            startTime = item.getStartTime();
//        }
//        //Dalnim : When get starTime from SQLite, need to add Delay Time and Play Before Subtitle time.
//        //start time is long type and millisecond, but getPlayBeforeSubtitle is float type and not millisecond, it's second.
//        value += startTime + videoModel.getDelaySubtitles() + (videoModel.getPlayBeforeSubtitle() * 1000);;
//        if (value > videoModel.getDuration()) {
//            value = startTime;
//        }
//        if (value < 0) {
//            value = startTime < 0 ? 0 : startTime;
//        }
//        return value;
//    }
//    public static long getEndTime(DicModel item, VideoModel videoModel) {
//        long endTime = 0;
//        long value = 0;
//        if (item != null) {
//            endTime = item.getEndTime();
//        }
//        value += endTime + videoModel.getDelaySubtitles() + (videoModel.getPlayAfterSubtitle() * 1000);
//        if (value < 0) {
//            value = endTime;
//        } else if (value > videoModel.getDuration()) {
//            value = videoModel.getDuration();
//        }
//        if (value < 0) {
//            value = endTime < 0 ? 0 : endTime;
//        }
//        return value;
//    }

    public static MinMaxSubModel getMinMaxSubTime(DicModel dicModel, VideoModel videoModel) {
        long startTime = dicModel.getStartTime();
        long endTime = dicModel.getEndTime();
        long duration = videoModel.getDuration();
        int delaySubtitle = videoModel.getDelaySubtitles();
        int playBeforeSubtitle = (int) (videoModel.getPlayBeforeSubtitle() * 1000);
        int playAfterSubtitle = (int) (videoModel.getPlayAfterSubtitle() * 1000);
        return getMinMaxSubTime(startTime, endTime, duration, delaySubtitle, playBeforeSubtitle, playAfterSubtitle);
    }

    public static MinMaxSubModel getMinMaxSubTime(long startTime, long endTime, long duration, int delaySubtitle, int playBeforeSubtitle, int playAfterSubtitle) {
        long minSub = getSubtitleTimeBetweenDuration(startTime + delaySubtitle, duration);
        long maxSub = getSubtitleTimeBetweenDuration(endTime + delaySubtitle, duration);
        //자막시간조절시 시간을 마이너스로 하면(delaySubtitle을 너무 많이 왼쪽으로 주면) maxSub도 0이 되어서 노래가 안나옴. (isSubtitleEndTimeExceedDelaySubtitle가 계속 true가 됨. postion이 항상 maxSub(0)보다 커지기 때문)
        //이 문장이 있으면 아무리 delaySubtitle를 많이 줘도 항상 노래가 처음부터 시작한다.
        if (maxSub <= 0) {
            if (endTime > duration) {
                maxSub = duration;
            } else {
                maxSub = endTime;
            }
        }
        long minSubNoExtraTime = getSubtitleTimeBetweenDuration(startTime, duration);
        long maxSubNoExtraTime = getSubtitleTimeBetweenDuration(endTime, duration);
        long minSubWithAllExtraTime =  getSubtitleTimeBetweenDuration(startTime + delaySubtitle + playBeforeSubtitle, duration);
        long maxSubWithAllExtraTime =  getSubtitleTimeBetweenDuration(endTime + delaySubtitle + playAfterSubtitle, duration);

        return MinMaxSubModelBuilder.aMinMaxSubModel()
                .withMinSub(minSub)
                .withMaxSub(maxSub)
                .withMinSubNoExtraTime(minSubNoExtraTime)
                .withMaxSubNoExtraTime(maxSubNoExtraTime)
                .withMinSubWithAllExtraTime(minSubWithAllExtraTime)
                .withMaxSubWithAllExtraTime(maxSubWithAllExtraTime)
                .build();
    }

    public static long getSubtitleTime(long duration, long time, int extraValue) {
        long value = 0;
        value += time + extraValue;
        if (value > duration) {
            value = time;
        }
        if (value < 0) {
            value = time < 0 ? 0 : time;
        }
        return value;
    }
    public static long getSubtitleTimeWithDelaySubtitleTime(long time, VideoModel videoModel) {
        int extraTime = videoModel.getDelaySubtitles();
        return getSubtitleTime(videoModel.getDuration(), time, extraTime);
    }
//    public static long getStartTimeWithDelaySubtitleTime(long startTime, VideoModel videoModel) {
//        int extraTime = videoModel.getDelaySubtitles();
//        return getSubtitleTimeCommon(videoModel.getDuration(), startTime, extraTime);
//    }
//    public static long getEndTimeWithDelaySubtitleTime(long endTime, VideoModel videoModel) {
//        int extraTime = videoModel.getDelaySubtitles();
//        return getSubtitleTimeCommon(videoModel.getDuration(), endTime, extraTime);
//    }
//
//    public static long getStartTimeNoExtraTime(long startTime, VideoModel videoModel) {
//        int extraValue = (int) (videoModel.getDelaySubtitles() + (videoModel.getPlayAfterSubtitle() * 1000));
//        return getSubtitleTimeCommon(videoModel.getDuration(), startTime, -1 * getExtraSubtitleTime(videoModel));
//    }
//    public static long getEndTimeNoExtraTime(long endTime, VideoModel videoModel) {
//        return getSubtitleTimeCommon(videoModel.getDuration(), endTime, -1 * getExtraSubtitleTime(videoModel));
//    }
//
//    public static long getStartTimeWithAllExtraTime(long startTime, VideoModel videoModel) {
////        int extraValue = (int) (videoModel.getDelaySubtitles() + (videoModel.getPlayAfterSubtitle() * 1000));
//        return getSubtitleTime(videoModel.getDuration(), startTime, getAllExtraSubtitleTime(videoModel));
//    }
//    public static long getEndTimeWithAllExtraTime(long endTime, VideoModel videoModel) {
////        int extraValue = (int) (videoModel.getDelaySubtitles() + (videoModel.getPlayBeforeSubtitle() * 1000));
//        return getSubtitleTime(videoModel.getDuration(), endTime, getAllExtraSubtitleTime(videoModel));
//    }
//
    public static long getSubtitleTimeWithAllExtraTime(long startTime, VideoModel videoModel) {
        return getSubtitleTime(videoModel.getDuration(), startTime, getAllExtraSubtitleTime(videoModel));
    }

    private static int getAllExtraSubtitleTime(VideoModel videoModel) {
        return (int) (videoModel.getDelaySubtitles() + (videoModel.getPlayBeforeSubtitle() * 1000));
    }

    public static int getAsteriskTitle(int showAsterisk) {
        switch (showAsterisk) {
            case Constant.SHOW_ASTERISK.SHOW_SENTENCE:
                return R.string.listen_comprehension_show_the_subtitle;
            case Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY:
                return R.string.listen_comprehension_show_difficult_words_only;
            default:
                return R.string.listen_comprehension_hide_the_subtitle;
        }
    }

    //Dalnim added : To hide known dialogs during playing.
    public static int getShowAsteriskWhenHideKnowDialogIsOn(DicModel dicModel, boolean isHideKnownDialogDuringPlaying, boolean isCCRepeatMode, int showAsterisk) {
        if (isCCRepeatMode) {
            return showAsterisk;
        } else if (isHideKnownDialogDuringPlaying &&
                VocaKnow.isKnown(dicModel.getVocaKnow())) {
            //When it's KNOW dialog
            return Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY;
        }
        return showAsterisk;
    }

    //재생중이 아닐때는(정지시) 자막 앞뒤로 가기할때 아는 자막이라도 자막을 보이게 하기위해서 isBeingPlaying를 추가함.
    public static int getShowAsteriskWhenHideKnowDialogIsOn(DicModel dicModel, boolean isHideKnownDialogDuringPlaying, boolean isCCRepeatMode, int showAsterisk, boolean isBeingPlaying) {
        if (isCCRepeatMode) {
            return showAsterisk;
        } else if (isHideKnownDialogDuringPlaying &&
                VocaKnow.isKnown(dicModel.getVocaKnow()) &&
                isBeingPlaying) {
            //When it's KNOW dialog
            return Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY;
        }
        return showAsterisk;
    }

    //Dalnim add
    public static int getDefaultIndexIfOutOfIndex(List<?> list, int index) {
        if (index > list.size())
            index = list.size() - 1;

        if (index <= 0)
            index = 0;

        return index;
    }

    //Dalnim add
    public static List<DicModel> udpateVocaKnowInSubtitleList(List<DicModel> subtitleList, IVocaBasicItem item) {
        for (DicModel dicModel : subtitleList) {
            //Subtitle can have duplicated in the list.
            if ((dicModel.getVocaType() == item.getVIVocaType()) && (dicModel.getVocaId() == item.getVIVocaId())) {
                dicModel.setVocaKnow(item.getVIVocaKnow());
                dicModel.setVocaKnowPronounce(item.getVIVocaKnowPronounce());
            }
        }
        return subtitleList;
    }

    //Dalnim add
    public static boolean isVocaKnown(int know) {
        if (know >= Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
            return true;
        return false;
    }

    //Dalnim add
    public static boolean isVocaUnknown(int know) {
        if (know < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
            return true;
        return false;
    }

    //Dalnim add
    public static boolean isVocaUnknownExceptNotRated(int know) {
        if (Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED < know && know < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
            return true;
        return false;
    }

    public static void updateBackgroundCCRepeatTuningScreen(View view, int prevNextSubtitleWidth, long subtitleTimeGap) {
        view.setBackgroundResource(getBackgroundCCRepeatTuningScreen(subtitleTimeGap));
        int minPrevNextSubtitleWidth = prevNextSubtitleWidth / 10;
        int maxPrevNextSubtitleWidth = (int) (prevNextSubtitleWidth * 2.5);
//        view.setVisibility(View.VISIBLE);
        int newPrevNextSubtitleWidth = (int) (prevNextSubtitleWidth + (subtitleTimeGap / prevNextSubtitleWidth));
        if (newPrevNextSubtitleWidth < minPrevNextSubtitleWidth) {
            newPrevNextSubtitleWidth = minPrevNextSubtitleWidth;
        } else if (newPrevNextSubtitleWidth > maxPrevNextSubtitleWidth) {
            newPrevNextSubtitleWidth = maxPrevNextSubtitleWidth;
        }
        view.getLayoutParams().width = newPrevNextSubtitleWidth;
        view.requestLayout();
    }

    public static int getBackgroundCCRepeatTuningScreen(long value) {
        if (value > 0) {
            return R.color.color_cc_repeat_tuning_screen_overlap;
        } else if (value < 0) {
            return R.color.color_cc_repeat_tuning_screen_no_overlap;
        }
        return R.color.color_cc_repeat_tuning_screen_default;
    }

    public static File getVoiceFileOnLocal(Context context, PlayerFileModel playerFileModel, File voiceFolder, String fileName, int vocaId) {
        if (voiceFolder != null) {
            String folderPath;
            if (vocaId >= 0) {
                folderPath = voiceFolder.getPath() + File.separator + BaseFileUtil.getVoiceSubFolderPath(fileName);
            } else {
                String videoPath;
                if (playerFileModel.isLocal()) {
                    videoPath = BaseStorageUtil.getLocalPath();
                } else {
                    videoPath = StorageUtil.getNetworkPath(playerFileModel);
                }
                videoPath += playerFileModel.getPath();
                folderPath = BaseStorageUtil.getFolderZip(context) + videoPath + File.separator + Constant.FILE.FOLDER_VOICE;
            }
            File folder = new File(folderPath);
            if (folder.exists() || folder.mkdirs()) {
                return new File(folder, fileName);
            }
        }
        return null;
    }

    public static void openCopySubtitleDialog(Context context, DicModel dicModel) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_dialogs);
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                0,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        String strToCopy = "";
                        switch (which) {
                            case 0:
                                strToCopy = dicModel.getVocaDisplay();
                                break;
                            case 1:
                                strToCopy = dicModel.getVIVocaMeaning(context);// dicModel.getVocaDisplay() + "\n" + dicModel.getMeaning();
                                break;
                            case 2:
                                strToCopy = dicModel.getMeaning();
                                break;
                        }
                        String toastText = getMessageInToastToShow(context, strToCopy);
                        Utils.copyToClipboard(context, strToCopy, toastText);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    public interface SubtitleVocaMeaningToCopyListener {
        void onComplete(String value);
    }
    public static void showPopupTogetSubtitleVocaMeaningToCopy(Context context, List<DicModel> dicModels, SubtitleVocaMeaningToCopyListener listener) {
        String[] displayOptions = context.getResources().getStringArray(R.array.array_choice_copy_dialogs_conversation);
        int checkedItem = SharedPreferencesDB.getInstance(context).getChoiceSubtitleVocaMeaningToCopy();
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.showWrapContentHeight(
                R.string.menu_to_copy,
                displayOptions,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        SharedPreferencesDB.getInstance(context).setChoiceSubtitleVocaMeaningToCopy(which);
                        String strToCopy = "";
                        switch (which) {
                            case 0:
                                strToCopy = getVocaDisplayString(dicModels);
                                break;
                            case 1:
                                strToCopy = getVocaDisplayMeaningString(dicModels);
                                break;
                            case 2:
                                strToCopy = getVocaDisplayOneLineAndMeaningOneLineString(dicModels);
                                break;
                            case 3:
                                strToCopy = getVocaMeaningString(dicModels);
                                break;
                        }
//                        String toastText = getMessageInToastToShow(context, strToCopy);
//                        Utils.copyToClipboard(context, strToCopy, toastText);
                        listener.onComplete(strToCopy); // notify listener with strToCopy value
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    //이건 자막이 영어 또는 한국어만 있는 경우다.
    public static String getSubtitleVocaOrMeaningToCopy(Context context, List<DicModel> dicModels) {
        return getVocaDisplayOrMeaning(dicModels);
    }

    //영어 또는 한국어 둘중에 하나만 있을때다.
    private static String getVocaDisplayOrMeaning(List<DicModel> dicModels) {
        StringJoiner sj = new StringJoiner("\n\n");
        for (DicModel dicModel : dicModels) {
            sj.add(dicModel.getVocaDisplay() + "" + dicModel.getMeaning());
        }
        return sj.toString().trim();
    }
    //이건 VocaDisplay를 한줄로 먼저 합치고 getMeaning을 한줄로 합친다.
    private static String getVocaDisplayOneLineAndMeaningOneLineString(List<DicModel> dicModels) {
        StringJoiner sjVocaDisplay = new StringJoiner("\n");
        StringJoiner sjMeaning = new StringJoiner("\n");
        for (DicModel dicModel : dicModels) {
            sjVocaDisplay.add(dicModel.getVocaDisplay());
            sjMeaning.add(dicModel.getMeaning());
        }

        return sjVocaDisplay.toString().trim() + "\n" + sjMeaning.toString().trim();
    }

    private static String getVocaDisplayMeaningString(List<DicModel> dicModels) {
        StringJoiner sj = new StringJoiner("\n\n");
        for (DicModel dicModel : dicModels) {
            sj.add(dicModel.getVocaDisplay() + "\n" + dicModel.getMeaning());
        }
        return sj.toString().trim();
    }

    private static String getVocaMeaningString(List<DicModel> dicModels) {
        StringJoiner sj = new StringJoiner("\n");
        for (DicModel dicModel : dicModels) {
            sj.add(dicModel.getMeaning());
        }
        return sj.toString().trim();
    }

    // Overloaded getDisplayString method to include only 1 previous sentence by default
    private static String getVocaDisplayString(List<DicModel> dicModels) {
        StringJoiner sj = new StringJoiner("\n");
        for (DicModel dicModel : dicModels) {
            sj.add(dicModel.getVocaDisplay());
        }
        return sj.toString().trim();
    }

    public static List<IVocaFullPlayTTSItem> convertDicModelListToIVocaFullPlayTTSItemList(List<DicModel> list, int limit) {
        return list.stream()
                .filter(item -> item instanceof IVocaFullPlayTTSItem)
                .map(item -> (IVocaFullPlayTTSItem) item)
                .limit(limit)
                .collect(Collectors.toList());
    }

}
