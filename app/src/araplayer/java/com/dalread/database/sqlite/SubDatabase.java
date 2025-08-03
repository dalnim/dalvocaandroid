package com.dalread.database.sqlite;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;
import static com.dalread.util.Constant.PLAYER.SQL.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.dalread.BuildConfig;
import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.MediaInfoModel;
import com.dalread.database.sqlite.model.SubtitleWordListModel;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.EditVoca;
import com.dalread.model.RubyTextModel;
import com.dalread.model.SubModel;
import com.dalread.model.SubtitleMeaningModel;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaTypeId;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.dalread.util.VocaListUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
//이건 나중에 다른 SubDatabase와 합쳐야 한다.
public class SubDatabase extends DicSentenceSubDatabase {

    private final String TAG = "SubDatabase";
//    private SubDatabaseHelper databaseHelper;
//    private SQLiteDatabase database;
//    private SharedPreferencesDB sharedPreferencesDB;
//    private int studyLanguage, uid;
    private static SubDatabase instance;
//    private static String subPath = Constant.BASE_BLANK;
//    private static Context context;

    public static SubDatabase getInstance(Context contextTemp, String path) {
        context = contextTemp;
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new SubDatabase(context, path);
        return instance;
    }

    public SubDatabase(Context context, String path) {
        super(context, path);
        databaseHelper = new SubDatabaseHelper(context, path);
        this.subPath = path;
        sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        studyLanguage = EnumLanguage.findByFormatApi(sharedPreferencesDB.getStudyLanguage()).getIdApi();
        uid = sharedPreferencesDB.getRealUid();


        enumStudyLanguage = LanguageUtil.getStudyLanguage(context);
        updateMotherTongueLanguage(context);
        updateMenuLanguage(context);
    }

    public static SubDatabase getDicDatabaseInstance(Context context) {
        String destPathWithFileName = BaseStorageUtil.getAraPlayerDicDBPath(context);
        return getInstance(context, destPathWithFileName);
    }

    //StudyLanguage can't be changed, but MotherTongue can be changed in the Setting.
    public void updateMotherTongueLanguage(Context context) {
        enumMotherTongueLanguage = LanguageUtil.getMotherTongueLanguage(context);
    }
    public void updateMenuLanguage(Context context) {
        enumMenuLanguage = LanguageUtil.getMenuLanguage(context);
    }

    public void openRead() {
        this.database = databaseHelper.getReadableDatabase();
    }

    /**
     * Open the databases connection.
     */
    public void openWrite() {
        this.database = databaseHelper.getWritableDatabase();
    }

    /**
     * Close the databases connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public int getSubtitleSize() {
        return getRecordsCount(QUERY.GET_SUBTITLE_SIZE);
    }

    public boolean isHasSubRuby() {
        return getSubtitleSize() > 0;
    }

    public boolean isHasBothSubtitleLanguages() {
        boolean isHasStudyLangSubtitle = isHasStudyLangSubtitle();
        boolean isHasMotherTongueSubtitle = isHasMotherTongueSubtitle();
        if (isHasStudyLangSubtitle && isHasMotherTongueSubtitle) {
            return true;
        }
        return false;
    }

    public boolean isHasStudyLangSubtitle() {
        String query = QUERY.GET_SUBTITLE_SIZE + QUERY.WHERE + COLUMN.USED + QUERY.EQUAL + Constant.PLAYER.SUB_TITLE.USED.SHOW + QUERY.AND + COLUMN.VOCA + QUERY.NOT_EMPTY;
        return getRecordsCount(query) > 0;
    }

    public boolean isHasMotherTongueSubtitle() {
        String query = QUERY.GET_SUBTITLE_SIZE + QUERY.WHERE + COLUMN.USED + QUERY.EQUAL + Constant.PLAYER.SUB_TITLE.USED.SHOW + QUERY.AND + COLUMN.MEANING + QUERY.NOT_EMPTY;
        return getRecordsCount(query) > 0;
    }

    public List<SubModel> getSubtitles() {
        return getSubtitles(null);
    }

    public ArrayList<SubModel> getSubtitles(String option) {
        try {
            DLog.d(TAG, "getSubtitles");
            ArrayList<SubModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.GET_SUBTITLE;
            if (!Utils.isEmpty(option)) {
                query += option;
            }
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserSubModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return null;
    }

    public List<DicModel> getSubtitleDialogListUsed() {
        String option = QUERY.WHERE + COLUMN.USED + QUERY.EQUAL + Constant.PLAYER.SUB_TITLE.USED.SHOW;
        return getSubtitleDialogList(option, false, false);
    }

    public List<DicModel> getSubtitleDialogList() {
        return getSubtitleDialogList(Constant.BASE_BLANK, true);
    }

    public List<DicModel> getSubtitleDialogListById(String id) {
        String option = QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + id + QUERY.IN_CLOSE;
        return getSubtitleDialogList(option, true);
    }

    public List<DicModel> getSubtitleDialogListExceptId(String id) {
        String option = QUERY.WHERE + COLUMN.ID + QUERY.NOT + QUERY.IN_OPEN + id + QUERY.IN_CLOSE;
        return getSubtitleDialogList(option, true);
    }

//    public ArrayList<DicModel> getSubtitleDialogListByLanguage(int langStudy) {
//        String option = QUERY.WHERE + COLUMN.LANG_STUDY + "=" + langStudy;
//        return getSubtitleDialogListByLanguage(option);
//    }

    public List<DicModel> getSubtitleDialogListByLanguage(String ids, int langStudy) {
        String option = QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + ids + QUERY.IN_CLOSE +
                QUERY.AND + COLUMN.LANG_STUDY + "=" + langStudy;
        return getSubtitleDialogListByLanguage(option);
    }

    public List<DicModel> getNoTranslationSubtitleDialogListByLanguage() {
        String option = QUERY.WHERE + COLUMN.MEANING + " = ''";
        return getSubtitleDialogListByLanguage(option);
    }

    public List<DicModel> getSubtitleDialogListByLanguage(String query) {
        return getSubtitleDialogList(query, true);
    }

    public List<DicModel> getSubtitleDialogListByIds(String ids) {
        String option = QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + ids + QUERY.IN_CLOSE;
        return getSubtitleDialogList(option, true);
    }

    public ArrayList<DicModel> getSubtitleDialogListByDifficult(VocaKnowGroupSelect vocaKnowGroupSelect,List<DicModel> subtitleListTotal) {
        String query = Constant.BASE_BLANK;
        final String queryDefault = QUERY.SELECT_ALL + TABLE.SUBTITLE + QUERY.WHERE;
        if (vocaKnowGroupSelect.isNotRated()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }
        if (vocaKnowGroupSelect.isAmki1st()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }
        if (vocaKnowGroupSelect.isAmki2nd()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }
        if (vocaKnowGroupSelect.isUnknown()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }
        if (vocaKnowGroupSelect.isKnown()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }
        if (vocaKnowGroupSelect.isDifficultPronunciation()) {
            String option = COLUMN.VOCA_KNOW + " = " + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN + QUERY.AND + COLUMN.VOCA_KNOWPRONOUNCE + " < " + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            query = addWhereClauseInSubtitleDialogListByDifficult(query, queryDefault, option);
        }

        List<DicModel> dicModelArrayList = new ArrayList<>();
        if (!Utils.isEmpty(query))
            dicModelArrayList = getSubtitleDialogList(query, true, true);

        Set<DicModel> resultSet = new HashSet<>(dicModelArrayList);
        if (vocaKnowGroupSelect.isBookmarked()) {
            for (DicModel bookmark : subtitleListTotal) {
                if (bookmark.isBookmark()) {
                    resultSet.add(bookmark);
                }
            }
        }
        return new ArrayList(resultSet);
    }

    @NotNull
    private String addWhereClauseInSubtitleDialogListByDifficult(String query, String queryDefault, String option) {
        option = queryDefault + option;
        if (!Utils.isEmpty(query)) {
            query += QUERY.UNION;
        }
        query += option;
        return query;
    }

    //Dalnim Modify
    public List<DicModel> getWordDialogListByDifficult(VocaKnowGroupSelect vocaKnowGroupSelect) {
        Set<Integer> setDifficultType = vocaKnowGroupSelect.getSelectedItems();
        String listVocaKnow = vocaKnowGroupSelect.getSelectedItemsWithComma();
        List<DicModel> dicModelWordList = getDicModelByVocaKnow(listVocaKnow);

        StringJoiner sbListVicaID = new StringJoiner(",");
        Map<String, DicModel> mapDicModelWordList = new HashMap<>();// dicModelWordList.stream().collect(Collectors.toMap(e -> e.getVocaType()+ "_" + e.getVocaId(), e -> e));
        for (DicModel dicModel : dicModelWordList) {
            sbListVicaID.add(Integer.toString(dicModel.getVocaId()));
            mapDicModelWordList.put(dicModel.getVocaType() + "_" + dicModel.getVocaId(), dicModel);
        }
        List<SubtitleWordListModel> subtitleWordListModelList = getSubtitleWordListByVocaID(sbListVicaID.toString());

        Map<Integer, Integer> mapSubtitleIDListWithDifficultWord = new HashMap<>();
        for (SubtitleWordListModel subtitleWordListModel : subtitleWordListModelList) {
            String vocaTypeId = subtitleWordListModel.getVocaType() + "_" + subtitleWordListModel.getVocaId();
            if (mapDicModelWordList.containsKey(vocaTypeId)) {
                DicModel dicModel = mapDicModelWordList.get(vocaTypeId);
                if (setDifficultType.contains(dicModel.getVocaKnow())) {
                    mapSubtitleIDListWithDifficultWord.put(subtitleWordListModel.getSubtitleId(), 0);
                }
            }
        }

        if (mapSubtitleIDListWithDifficultWord.size() == 0)
            return new ArrayList<>();

        String subtitleIDList = mapSubtitleIDListWithDifficultWord.keySet().stream().map(e -> Integer.toString(e)).collect(Collectors.joining(","));
        return getSubtitleDialogListById(subtitleIDList);
    }

    public void resetUsedInAllSubtitles(int used) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.USED, used);
        updateTableAllRows(TABLE.SUBTITLE, cv);
    }

    public void setUsedToUsedInAllSubtitles() {
        resetUsedInAllSubtitles(Constant.PLAYER.SUB_TITLE.USED.SHOW);
    }
    public void setHideAutoToUsedInAllSubtitles() {
        resetUsedInAllSubtitles(Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO);
    }

    public List<DicModel> getSubtitleDialogList(String option, boolean isMeaning) {
        return getSubtitleDialogList(option, false, isMeaning);
    }

    public List<DicModel> getSubtitleDialogList(String option, boolean isReplace, boolean isMeaning) {
        try {
            List<DicModel> list = new ArrayList<>();
            String query = QUERY.GET_SUBTITLE;
            if (isReplace) {
                query = option;
            } else {
                if (!Utils.isEmpty(option)) {
                    query += option;
                }
            }
            query += QUERY.ORDER_BY + COLUMN.START_TIME + QUERY.ASC;
            DLog.d(TAG, "getSubtitleDialogList - query=" + query + " - isMeaning=" + isMeaning);
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                final DicModel item = parserDicModelFromSubtitleTable(cursor);
                list.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public ArrayList<DicModel> getSubtitleDialogList(int id) {
        try {
            DLog.d(TAG, "getSubtitleDialogList - id=" + id);
            ArrayList<DicModel> list = new ArrayList<>();
            openRead();
            String query = "SELECT C.* FROM " + TABLE.DIC
                    + " A LEFT JOIN " + TABLE.SUBTITLE_WORDLIST
                    + " B ON A." + COLUMN.VOCA_ID + " = B." + COLUMN.VOCA_ID
                    + " LEFT JOIN " + TABLE.SUBTITLE + " C ON B." + COLUMN.SUBTITLE_ID + " = C." + COLUMN.ID
                    + " WHERE A." + COLUMN.VOCA_ID + " = " + id;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            int count = 1;
            while (!cursor.isAfterLast()) {
                final DicModel model = new DicModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
                model.setVocaDisplayRuby(StringUtils.replaceHTML(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_RUBY))));
                model.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME)));
                model.setIndex(count);
                list.add(model);
                count++;
                cursor.moveToNext();
            }
            cursor.close();
            return list;

        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public ArrayList<SubModel> getSubtitlesById(String id) {
        DLog.d(TAG, "getSubtitlesById - id=" + id);
        String option = QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + id + QUERY.IN_CLOSE;
        return getSubtitles(option);
    }

    public ArrayList<SubModel> getSubtitlesByLanguage(String language) {
        DLog.d(TAG, "getSubtitlesByLanguage - language=" + language);
        String option = QUERY.WHERE + COLUMN.LANG_STUDY + "=" + language + QUERY.ORDER_BY + COLUMN.ID + QUERY.ASC;
        return getSubtitles(option);
    }

    public ArrayList<SubModel> getSubtitlesByLanguage(String id, String language) {
        DLog.d(TAG, "getSubtitlesByLanguage - id=" + id + " - language=" + language);
        String option = QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + id + QUERY.IN_CLOSE +
                QUERY.AND + COLUMN.LANG_STUDY + "=" + language + QUERY.ORDER_BY + COLUMN.ID + QUERY.ASC;
        return getSubtitles(option);
    }

    public List<SubtitleWordListModel> getSubtitleWordList(String id) {
        try {
            DLog.d(TAG, "getSubtitleWordList - id=" + id);
            List<SubtitleWordListModel> list = new ArrayList<>();
            String query = QUERY.SELECT_ALL + TABLE.SUBTITLE_WORDLIST + QUERY.WHERE + COLUMN.SUBTITLE_ID + "=" + id;
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserSubtitleWordList(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public List<SubtitleWordListModel> getSubtitleWordListOrderBySubtitleID(String subtitleID) {
        String query = QUERY.SELECT_ALL + TABLE.SUBTITLE_WORDLIST + QUERY.WHERE + COLUMN.SUBTITLE_ID + QUERY.IN_OPEN + subtitleID + QUERY.IN_CLOSE + QUERY.ORDER_BY + COLUMN.SUBTITLE_ID;
        return getSubtitleWordListByQuery(query);
    }

    public List<SubtitleWordListModel> getSubtitleWordList() {
        String query = QUERY.SELECT_ALL + TABLE.SUBTITLE_WORDLIST;
        return getSubtitleWordListByQuery(query);
    }
    //dalnim : added this but I think I don't need this, Parse LRC file from AraPlayer will be enough.
    public MediaInfoModel getMediaInfo() {
        MediaInfoModel mediaInfoModel = new MediaInfoModel();
        String query = QUERY.SELECT_ALL + TABLE.MEDIA_INFO;
        try {
            List<SubtitleWordListModel> list = new ArrayList<>();
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mediaInfoModel = parserMediaInfo(cursor);
                break;
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return mediaInfoModel;
    }

    //Dalnim add
    public List<SubtitleWordListModel> getSubtitleWordListByVocaID(String listVocaID) {
        String query =QUERY.SELECT_ALL + TABLE.SUBTITLE_WORDLIST + QUERY.WHERE + COLUMN.VOCA_ID + QUERY.IN_OPEN + listVocaID + QUERY.IN_CLOSE + QUERY.ORDER_BY + COLUMN.SUBTITLE_ID;
        return getSubtitleWordListByQuery(query);
    }

    //Dalnim add
    public List<Integer> getSubtitleWordListByWordCountIsShorterThan(int wordCountInSubtitle) {
        return getSubtitleWordListByWordCount(wordCountInSubtitle, QUERY.LESS_THAN_OR_EQUAL);
    }

    //Dalnim add
    public List<Integer> getSubtitleWordListByWordCountIsLongerThan(int wordCountInSubtitle) {
        return getSubtitleWordListByWordCount(wordCountInSubtitle, QUERY.GREATER_THAN_OR_EQUAL);
    }

    //Dalnim add
    public List<Integer> getSubtitleWordListByWordCount(int wordCountInSubtitle, String lessOrGreaer) {
        String query = QUERY.SELECT_ALL_COUNT + TABLE.SUBTITLE_WORDLIST + QUERY.GROUP_BY + COLUMN.SUBTITLE_ID + QUERY.HAVING + COLUMN.COUNT + lessOrGreaer + wordCountInSubtitle;
        try {
            List<Integer> list = new ArrayList<>();
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SUBTITLE_ID)));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }


    //Dalnim add
    public List<SubtitleWordListModel> getSubtitleWordListByQuery(String query) {
        try {
            List<SubtitleWordListModel> list = new ArrayList<>();
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserSubtitleWordList(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public ArrayList<DicModel> getDics() {
        return getDics(null);
    }

    public ArrayList<DicModel> getDics(String sortQuery) {
        try {
            DLog.d(TAG, "getDics");
            ArrayList<DicModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.GET_DIC;
            if (!Utils.isEmpty(sortQuery)) {
                query += QUERY.ORDER_BY + sortQuery;
            } else {
                query += QUERY.ORDER_BY + COLUMN.VOCA;
            }
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            int count = 1;
            while (!cursor.isAfterLast()) {
                final DicModel item = parserDicModel(cursor);
                item.setIndex(count);
                item.setStartTime(count);
                list.add(item);
                count++;
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public DicModel getDicModelById(String id) {
        DicModel dicModel;
        openRead();
        Cursor cursor = database.rawQuery(QUERY.GET_DIC + QUERY.WHERE + COLUMN.ID + "=" + id, null);
        cursor.moveToFirst();
        dicModel = parserDicModel(cursor);
        cursor.close();
        close();
        return dicModel;
    }

    //Need to check vocaType too. At this time use only WORD.
    //TODO : Need to remove this method and use another getDicModelByVocaId method
    public DicModel getDicModelByVocaId(String vocaId) {
        DicModel dicModel = null;
        String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_ID + " = " + vocaId;
        DLog.d(TAG, "getDicModelByVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            dicModel = parserDicModel(cursor);
        }
        cursor.close();
        close();
        return dicModel;
    }

    public DicModel getDicModelByVocaTypeVocaId(IVocaCoreItem item) {
        if (VocaKnow.isVocaTypeWord(item)) {
            return getDicModelByVocaId(item);
        } else {
            return getSubtitleDicModelByVocaId(item);
        }
    }

    public DicModel getDicModelByVocaId(IVocaCoreItem item) {
        DicModel dicModel = null;
        String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_ID + " = " + item.getVIVocaId();
        DLog.d(TAG, "getDicModelByVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            dicModel = parserDicModel(cursor);
        }
        cursor.close();
        close();
        return dicModel;
    }

    public DicModel getSubtitleDicModelByVocaId(IVocaCoreItem item) {
        DicModel dicModel = null;
        String query = QUERY.GET_SUBTITLE + QUERY.WHERE + COLUMN.VOCA_ID + " = " + item.getVIVocaId();
        DLog.d(TAG, "getDicModelByVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            dicModel = parserDicModelFromSubtitleTable(cursor);
        }
        cursor.close();
        close();
        return dicModel;
    }


    public List<DicModel> getDicModelByVocaIds(String listId) {
        try {
            DLog.d(TAG, "getDicModelByVocaIds - listId=" + listId);
            ArrayList<DicModel> list = new ArrayList<>();
            String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_ID + QUERY.IN_OPEN + listId + QUERY.IN_CLOSE;
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserDicModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    //Dalnim add
    public List<DicModel> getDicModelByVocaKnow(String listVocaKnow) {
        String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.IN_OPEN + listVocaKnow + QUERY.IN_CLOSE;
        return getDicModelWordCommon(query);
//        try {
//            ArrayList<DicModel> list = new ArrayList<>();
//            String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.IN_OPEN + listVocaKnow + QUERY.IN_CLOSE;
//            openRead();
//            Cursor cursor = database.rawQuery(query, null);
//            cursor.moveToFirst();
//            int count = 1;
//            while (!cursor.isAfterLast()) {
//                final DicModel item = parserDicModel(cursor);
//                item.setIndex(count);
//                list.add(item);
//                count++;
//                cursor.moveToNext();
//            }
//            cursor.close();
//            close();
//            return list;
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        } finally {
//            close();
//        }
//        return new ArrayList<>();
    }

    //Dalnim add
    public ArrayList<DicModel> getDicModelByKnownWord() {
        String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.GREATER_THAN_OR_EQUAL + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
        return getDicModelWordCommon(query);
//        try {
////            ArrayList<DicModel> list = new ArrayList<>();
////            String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + ">=" + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
////            openRead();
////            Cursor cursor = database.rawQuery(query, null);
////            cursor.moveToFirst();
////            int count = 1;
////            while (!cursor.isAfterLast()) {
////                final DicModel item = parserDicModel(cursor);
////                item.setIndex(count);
////                list.add(item);
////                count++;
////                cursor.moveToNext();
////            }
////            cursor.close();
////            close();
////            return list;
////        } catch (Exception ex) {
////            DLog.e(TAG, ex.getMessage());
////        } finally {
////            close();
//        }
//        return new ArrayList<>();
    }

    //Dalnim add
    public ArrayList<DicModel> getDicModelByUnknownWord() {
        String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
        return getDicModelWordCommon(query);
    }
    //Dalnim add
    public ArrayList<DicModel> getDicModelWordCommon(String query) {
        try {

            ArrayList<DicModel> list = new ArrayList<>();
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            int count = 1;
            while (!cursor.isAfterLast()) {
                final DicModel item = parserDicModel(cursor);
                item.setIndex(count);
                list.add(item);
                count++;
                cursor.moveToNext();
            }
            cursor.close();
            close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }


    public List<DicModel> getDicModelListForQuiz(int quizCount) {
        try {
            int quizCountToGet = quizCount * 2;
            Set<DicModel> setQuiz = getDicModelListForQuiz_Candidates(quizCountToGet, QUERY.LESS);
            if (setQuiz.size() < quizCount) {
                setQuiz.addAll(getDicModelListForQuiz_Candidates(quizCountToGet, QUERY.EQUAL));
            }

            List<DicModel> list = new ArrayList<>(setQuiz);
            Collections.shuffle(list);
            list = (ArrayList<DicModel>) list.stream().limit(quizCount).collect(Collectors.toList());

            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    private Set<DicModel> getDicModelListForQuiz_Candidates(int size, String sqlWhereLess) {
        Set<DicModel> list = new HashSet<>();
        try {
            String query = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_KNOW + sqlWhereLess + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN + QUERY.AND + COLUMN.MEANING + QUERY.NOT_EMPTY +
                    QUERY.ORDER_BY + COLUMN.FREQUENCY + QUERY.DESC +
                    QUERY.LIMIT + size;

            DLog.d(TAG, "getDicModelListQuiz - query=" + query);
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserDicModel(cursor));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return list;
    }

    public ArrayList<SubtitleMeaningModel> getSubtitleMeaning(String listId) {
        try {
            DLog.d(TAG, "getSubtitleMeaning - listId=" + listId);
            ArrayList<SubtitleMeaningModel> list = new ArrayList<>();
            String query = QUERY.SELECT_ALL + TABLE.SUBTITLE_WORDLIST + QUERY.WHERE + COLUMN.SUBTITLE_ID + QUERY.IN_OPEN + listId + QUERY.IN_CLOSE;
            DLog.d(TAG, "query=" + query);
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SubtitleMeaningModel model = new SubtitleMeaningModel();
                model.setSubtitleId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SUBTITLE_ID)));
                final int vocaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID));
                String queryDic = QUERY.SELECT_ALL + TABLE.DIC + QUERY.WHERE + COLUMN.VOCA_ID + "=" + vocaId;
                DLog.d(TAG, "queryDic=" + queryDic);
                Cursor cursorDic = database.rawQuery(queryDic, null);
                cursorDic.moveToFirst();
                model.setDicModel(parserDicModel(cursorDic));
                list.add(model);
                cursorDic.close();
                cursor.moveToNext();
            }
            cursor.close();
            close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public ArrayList<VocaStudyChat> getVocaStudyChat(int subtitleId) {
        String query = "SELECT A.* FROM " + TABLE.DIC
                + " A LEFT JOIN " + TABLE.SUBTITLE_WORDLIST + " B ON A." + COLUMN.VOCA_ID + " = B." + COLUMN.VOCA_ID
                + " LEFT JOIN " + TABLE.SUBTITLE + " C ON B." + COLUMN.SUBTITLE_ID + " = C." + COLUMN.ID + " WHERE C." + COLUMN.ID + " = " + subtitleId
                + " ORDER BY A." + COLUMN.VOCA;
        return getVocaStudyChatListFromSQL(query);
    }

    public ArrayList<VocaStudyChat> getVocaStudyChatListNotRatedOnly() {
        Integer listCountToGet = sharedPreferencesDB.getCountOfNotRatedWordBeforePlaying();

         //TODO : Need to implement onVocaKnowChanged in the VideoInformationActivity.
        listCountToGet = 0;
//        if (BuildConfig.DEBUG) {
//            listCountToGet = 0;
//        }
        String query = QUERY.SELECT_ALL + TABLE.DIC
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.EQUAL + Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED + QUERY.AND + COLUMN.VOCA_ID + QUERY.GREATER_THAN_OR_EQUAL + 0
                + QUERY.ORDER_BY + COLUMN.FREQUENCY + QUERY.DESC
                + QUERY.LIMIT + listCountToGet;
        return getVocaStudyChatListFromSQL(query);
    }

    public ArrayList<VocaStudyChat> getVocaStudyChatListFromSQL(String query) {
        try {
            DLog.d(TAG, "getVocaStudyChat - query=" + query);
            ArrayList<VocaStudyChat> list = new ArrayList<>();
            openRead();
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(Voca.createVocaStudyChat(parserDicModel(cursor)));
                cursor.moveToNext();
            }
            cursor.close();
            return list;

        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public ArrayList<RubyTextModel> getWordRubyTag() {
        try {
            DLog.d(TAG, "getWordRubyTag");
            ArrayList<RubyTextModel> list = new ArrayList<>();
            openRead();
            Cursor cursor = database.rawQuery(QUERY.GET_DIC, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserToRubyTextModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    private void updateKnownPronounceInDicTable(int vocaId, int vocaKnow, int vocaKnowPronounce) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, vocaKnow);
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, vocaKnowPronounce);
        updateTableByVocaTypeId(TABLE.DIC, cv, Constant.API_VALUE.VALUE_VOCA_TYPE_WORD, vocaId);
//        udpateDicByVocaId(cv, vocaId);
    }

//    private void udpateDicByVocaId(ContentValues cv, int vocaId) {
//        openWrite();
//        database.update(TABLE.DIC,
//                cv, COLUMN.VOCA_ID + "=?",
//                new String[]{String.valueOf(vocaId)});
//        close();
//    }

    public String getSubtitleIdListToUpdateVocaKnow(VocaTypeId vocaTypeId) {
        final List<SubModel> list = getSubtitleModelByVoca(vocaTypeId);
        String ids = list.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.joining(","));
        return ids;
    }

    public List<SubModel> getSubtitleModelByVoca(IVocaCoreItem item) {
        if (VocaKnow.isVocaTypeWord(item)) {
            return getSubtitleModelByWordVocaId(item.getVIVocaId());
        } else {
            return getSubtitleModelBySubtitleVocaTypeId(item);
        }
    }

    public List<SubModel> getSubtitleModelByWordVocaId(int vocaId) {
        try {
            ArrayList<SubModel> list = new ArrayList<>();
            openRead();
            String query = "SELECT C.* FROM " + TABLE.DIC + " A LEFT JOIN " + TABLE.SUBTITLE_WORDLIST
                    + " B ON A." + COLUMN.VOCA_ID + " = B." + COLUMN.VOCA_ID
                    + " LEFT JOIN " + TABLE.SUBTITLE + " C ON B." + COLUMN.SUBTITLE_ID + " = C." + COLUMN.ID
                    + " WHERE A." + COLUMN.VOCA_ID + " = " + vocaId;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parserSubModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }

    public List<SubModel> getSubtitleModelBySubtitleVocaTypeId(IVocaCoreItem item) {
        String option = QUERY.WHERE + COLUMN.VOCA_TYPE + QUERY.EQUAL + item.getVIVocaType() + QUERY.AND + COLUMN.VOCA_ID + QUERY.EQUAL + item.getVIVocaId();
        return getSubtitles(option);
    }

    public void updateBookmarkInDB(IVocaBasicItem iVocaBasicItem) {
        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
            updateBookmarkDic(iVocaBasicItem);
        } else if (VocaKnow.isVocaTypeSubtitle(iVocaBasicItem)) {
            updateBookmarkSubtitle(iVocaBasicItem);
        }
    }
    //이건 개별 자막 DB가 아니라 전체 사전DB에 업데이트 하는것임.
    public void updateBookmarkInDicDB(IVocaBasicItem iVocaBasicItem) {
        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.BOOKMARK, iVocaBasicItem.getVIBookmark());
            updateTableById(TABLE_DIC_WORD, cv, iVocaBasicItem.getVIVocaId());

        }
    }

    public void updateBookmarkDic(VocaStudyChatExam voca) {
        updateBookmarkDic(voca.getVocaId(), voca.getBookmark());
    }

    public void updateBookmarkDic(DicModel model) {
        updateBookmarkDic(model.getVocaId(), model.getBookmark());
    }

    public void updateBookmarkDic(IVocaBasicItem item) {
        updateBookmarkDic(item.getVIVocaId(), item.getVIBookmark());
    }

    public void updateBookmarkDic(int vocaId, int bookmark) {
        DLog.d(TAG, "updateDicBookmark - vocaId=" + vocaId + " - bookmark=" + bookmark);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.BOOKMARK, bookmark);
        updateTableByVocaTypeId(TABLE.DIC, cv, Constant.API_VALUE.VALUE_VOCA_TYPE_WORD, vocaId);

//        udpateDicByVocaId(cv, vocaId);
    }
    //
    public void updateBookmarkSubtitle(IVocaBasicItem item) {
        DLog.d(TAG, "updateSubtitleBookmark - model=" + item.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.BOOKMARK, item.getVIBookmark());
        updateTableByVocaTypeId(TABLE.SUBTITLE, cv, item.getVIVocaType(), item.getVIVocaId());
        if (VocaKnow.isVocaTypeWord(item)) {
            updateTableByVocaTypeId(TABLE.DIC, cv, item.getVIVocaType(), item.getVIVocaId());
        }
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(item.getVIVocaId())});
//        close();
    }

//    public void updateSubtitleBookmark(DicModel model) {
//        DLog.d(TAG, "updateSubtitleBookmark - model=" + model.toString());
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN.BOOKMARK, model.getBookmark());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(model.getId())});
//        close();
//    }

    public void updateRepeatSubtitle(DicModel model) {
        DLog.d(TAG, "updateRepeatSubtitle - model=" + model.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.REPEAT, model.getVIRepeatCount());
        updateTableById(TABLE.SUBTITLE, cv, model.getId());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(model.getId())});
//        close();
    }

    public void updateRepeatSubtitle(int count) {
        DLog.d(TAG, "updateRepeatSubtitle - count=" + count);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.REPEAT, count);
        updateTableAllRows(TABLE.SUBTITLE, cv);
//        openWrite();
//        database.update(TABLE.SUBTITLE, cv, null, null);
//        close();
    }

    public void updateTimeSubtitle(DicModel model) {
        DLog.d(TAG, "updateTimeSubtitle - model=" + model.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.START_TIME, model.getStartTime());
        cv.put(COLUMN.END_TIME, model.getEndTime());
        updateTableById(TABLE.SUBTITLE, cv, model.getId());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(model.getId())});
//        close();
    }

    //Dalnim Add
    public void updateUsedSubtitleHideAuto(List<DicModel> subtitleList) {
        for (DicModel dicModel : subtitleList) {
            if (dicModel.getUsed() == Constant.PLAYER.SUB_TITLE.USED.HIDE_AUTO) {
                updateUsedSubtitle(dicModel);
            }
        }
    }

    //Dalnim Add
    public void updateCheckedSubtitleInDB(List<Integer> ids) {
        setHideAutoToUsedInAllSubtitles();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.USED, Constant.PLAYER.SUB_TITLE.USED.SHOW);
        updateTableByIds(TABLE.SUBTITLE, cv, ids);
    }

    public void updateUsedSubtitle(DicModel model) {
        DLog.d(TAG, "updateUsedSubtitle");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.USED, model.getUsed());
        updateTableById(TABLE.SUBTITLE, cv, model.getId());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(model.getId())});
//        close();
    }
    //자막의 단어가 바뀌면, 자막sqlite의 단어 테이블과 subtitle테이블, subtitleWordList테이블등도 업데이트 해준다.
    public void updatePhraseInformation(EditVoca editVoca) {
        updatePhraseInformationInDic(editVoca);

        IVocaBasicItem iVocaBasicItem = editVoca.getiVocaFullItem();
        int oldVocaId = editVoca.getOldVocaId();
        //기존 단어장에 없는 새로 추가된 단어이면 subtitleWordList테이블등도 업데이트 해준다. 근데 뭘 업데이트 하지 안하면 안되나?
        if (iVocaBasicItem.getVIVocaId() != oldVocaId) {
            updatePhraseInformationInSubtitle(iVocaBasicItem, oldVocaId);
            updatePhraseInformationInSubtitleWordList(iVocaBasicItem, oldVocaId);
        }
    }

    //Update vocaId and vocaIdServer to the same value here.
    public void updatePhraseInformationInDic(EditVoca editVoca) {
        IVocaBasicItem iVocaBasicItem = editVoca.getiVocaFullItem();
        int newVocaId = iVocaBasicItem.getVIVocaId();
        int oldVocaId = editVoca.getOldVocaId();
        DLog.d(TAG, "updatePhraseInformation - model=" + iVocaBasicItem.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_ID, iVocaBasicItem.getVIVocaId());
        cv.put(COLUMN.VOCA_ID_TO_SEND_SERVER, iVocaBasicItem.getVIVocaId());
//        cv.put(COLUMN.VOCA, iVocaBasicItem.getVIVoca()); //단어 자체는 변경하면 안된다.
        cv.put(COLUMN.PRONOUNCE, iVocaBasicItem.getVIPronounce());
        cv.put(COLUMN.MEANING, iVocaBasicItem.getVIMeaning(LanguageUtil.getMotherTongueLanguage(context)));
        cv.put(COLUMN.MEANING_DETAILED, iVocaBasicItem.getVIMeaningDetailed(LanguageUtil.getMotherTongueLanguage(context)));
        //현재 자막에서는 getOldVocaId가 0보다 작더라도 Dic DB(subtitle의 dic테이블이 아님)에 단어를 추가하여 getVIVocaId()를 새로 받아오므로, updateTableByVocaTypeId를 지나면 vocaId는 0보다 커진다.
        //현재 자막의 단어는 뜻을 바꾸면 vocaId가 양수가 될지 몰라도 다른 자막에서는 여전히 마이너스 값을 가질수 있다.
        if (oldVocaId == newVocaId) {
            updateTableByVocaTypeId(TABLE.DIC, cv, iVocaBasicItem.getVIVocaType(), oldVocaId);
        } else {
            updateTableByVocaTypeId(TABLE.DIC, cv, iVocaBasicItem.getVIVocaType(), oldVocaId);
            updateTableByVocaTypeId(TABLE.DIC, cv, iVocaBasicItem.getVIVocaType(), newVocaId);
        }
    }

    public void updatePhraseInformationInSubtitle(IVocaBasicItem iVocaBasicItem, int oldVocaId) {
        String vocaIdTag = "\"" + COLUMN.VOCA_ID + "=" + oldVocaId + ">" + "\"";
        String vocaIdTagWithToSendServerId = "\"" + COLUMN.VOCA_ID + "=" + iVocaBasicItem.getVIVocaId() + ">" +"\"";

        String query = QUERY.UPDATE + TABLE.SUBTITLE + QUERY.SET + COLUMN.VOCA_RUBY + QUERY.EQUAL +
                QUERY.REPLACE + QUERY.PARENTHESIS_OPEN + COLUMN.VOCA_RUBY + "," + vocaIdTag + ", " + vocaIdTagWithToSendServerId + QUERY.PARENTHESIS_CLOSE;

        openWrite();
        database.execSQL(query);
        close();
    }

    public void updatePhraseInformationInSubtitleWordList(IVocaBasicItem iVocaBasicItem, int oldVocaId) {
        DLog.d(TAG, "updatePhraseInformation - model=" + iVocaBasicItem.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_ID, iVocaBasicItem.getVIVocaId());
        updateTableByVocaTypeId(TABLE.SUBTITLE_WORDLIST, cv, iVocaBasicItem.getVIVocaType(), oldVocaId);
//        openWrite();
//        database.update(TABLE.SUBTITLE_WORDLIST,
//                cv, COLUMN.VOCA_ID + "=?",
//                new String[]{String.valueOf(oldVocaId)});
//        close();
    }

    public String getIdOfSubtitleFromPhraseInformation(int vocaId) {
        final List<SubModel> list = getSubtitleModelByWordVocaId(vocaId);
        String ids = Constant.BASE_BLANK;
        for (SubModel s : list) {
            if (!Utils.isEmpty(ids)) {
                ids += ",";
            }
            ids += s.getId();
        }
        return ids;
    }

    public boolean isSubtitleHasVocaTypeBase(DicModel model) {
        return isSubtitleHasVocaTypeBase(model.getVocaTypeBase(), model.getVocaTypeBase());
    }

    public boolean isSubtitleHasVocaTypeBase(int vocaTypeBase, int vocaIdBase) {
        if ((vocaTypeBase >= Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) && (vocaIdBase > 0)) {
            return true;
        }
        return false;
    }

    public void updateMultipleVocaKnowInDBByListObject(List<Object> itemList, int newVocaKnow) {
        List<IVocaBasicItem> list = new ArrayList<>();
        for (Object obj : itemList) {
            if (obj instanceof IVocaBasicItem) {
                IVocaBasicItem iVocaBasicItem = (IVocaBasicItem) obj;
                list.add(iVocaBasicItem);
            }
        }
        updateMultipleVocaKnowInDB(list, newVocaKnow);
    }
    public void updateMultipleVocaKnowInDB(List<IVocaBasicItem> itemList, int newVocaKnow) {
        List<IVocaBasicItem> itemListWord = new ArrayList<>();
        List<IVocaBasicItem> itemListSubitle = new ArrayList<>();
        for (IVocaBasicItem item : itemList) {
            if (VocaKnow.isVocaTypeWord(item)) {
                itemListWord.add(item);
            } else if (VocaKnow.isVocaTypeSubtitle(item)) {
                itemListSubitle.add(item);
            }
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, newVocaKnow);
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, newVocaKnow);

        if (!Utils.isEmpty(itemListWord)) {
            updateTableByMultipleVocaTypeId(TABLE.DIC, cv, itemListWord);
        }
        if (!Utils.isEmpty(itemListSubitle)) {
            updateTableByMultipleVocaTypeId(TABLE.SUBTITLE, cv, itemListSubitle);
        }
    }


    public void updateVocaKnowInDB(IVocaBasicItem iVocaBasicItem) {
        ContentValues cv = createContentValuesForVocaKnow(iVocaBasicItem);
        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
            updateTableByVocaTypeId(TABLE.DIC, cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
            updateTableByVocaTypeIdBase(cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
        } else if (VocaKnow.isVocaTypeSubtitle(iVocaBasicItem)) {
            updateTableByVocaTypeId(TABLE.SUBTITLE, cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
            updateKnownPronounceInDicTableWhenVocaTypeBaseIsWord(iVocaBasicItem);
        }
    }

    public void updateVocaKnowInDicDB(IVocaBasicItem iVocaBasicItem) {
        ContentValues cv = createContentValuesForVocaKnow(iVocaBasicItem);
        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
            updateTableById(TABLE_DIC_WORD, cv, iVocaBasicItem.getVIVocaId());
        }
    }

    private ContentValues createContentValuesForVocaKnow(IVocaBasicItem iVocaBasicItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, iVocaBasicItem.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, iVocaBasicItem.getVIVocaKnowPronounce());
        return cv;
    }


//    public void updateVocaKnowInDB(IVocaBasicItem iVocaBasicItem) {
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN.VOCA_KNOW, iVocaBasicItem.getVIVocaKnow());
//        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, iVocaBasicItem.getVIVocaKnowPronounce());
//        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
////            udpateDicByVocaId(cv, iVocaBasicItem.getVIVocaId());
//            updateTableByVocaTypeId(TABLE.DIC, cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
//            updateTableByVocaTypeIdBase(cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
//        } else if (VocaKnow.isVocaTypeSubtitle(iVocaBasicItem)) {
////            updateKnownPronounceByVocaIdInSubtitleTable(cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
//            updateTableByVocaTypeId(TABLE.SUBTITLE, cv, iVocaBasicItem.getVIVocaType(), iVocaBasicItem.getVIVocaId());
//            updateKnownPronounceInDicTableWhenVocaTypeBaseIsWord(iVocaBasicItem);
//        }
//    }


    private void updateKnownPronounceInDicTableWhenVocaTypeBaseIsWord(IVocaBasicItem iVocaBasicItem) {
        if (VocaKnow.isVocaTypeWord(iVocaBasicItem)) {
            updateKnownPronounceInDicTable(iVocaBasicItem.getVIVocaIdBase(), iVocaBasicItem.getVIVocaKnow(), iVocaBasicItem.getVIVocaKnowPronounce());
        }
    }


//    public void updateKnownPronounceByVocaIdInSubtitleTable(ContentValues cv, int vocaType, int vocaId) {
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.VOCA_TYPE + "=? AND " + COLUMN.VOCA_ID + "=?",
//                new String[]{String.valueOf(vocaType), String.valueOf(vocaId)});
//        close();
//    }



//    public void addTranslate(DicModel item) {
//        DLog.d(TAG, "addTranslate - item=" + item.toString());
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN.ID, item.getId());
//        cv.put(COLUMN.LANG_STUDY, item.getLangStudy());
//        cv.put(COLUMN.VOCA_TYPE, item.getVocaType());
//        cv.put(COLUMN.VOCA_ID, item.getVocaId());
//        cv.put(COLUMN.VOCA_KNOW, item.getVocaKnow());
//        cv.put(COLUMN.VOCA, item.getVocaDisplay());
//        cv.put(COLUMN.VOCA_RUBY, item.getVocaDisplayRuby());
//        cv.put(COLUMN.START_TIME, item.getStartTime());
//        cv.put(COLUMN.END_TIME, item.getEndTime());
//        cv.put(COLUMN.BOOKMARK, item.getBookmark());
//        cv.put(COLUMN.REPEAT, item.getRepeat());
//        cv.put(COLUMN.START_TIME_ORIGINAL, item.getStartTimeOriginal());
//        cv.put(COLUMN.END_TIME_ORIGINAL, item.getEndTimeOriginal());
//        cv.put(COLUMN.VOCA_ORIGINAL, item.getSubtitleOriginal());
//        cv.put(COLUMN.USED, item.getUsed());
//        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getKnowPronounceBase());
//        openWrite();
//        database.insert(TABLE.SUBTITLE, null, cv);
//        close();
//    }

    public void deleteAllRecordsInSubtitleDb() {
        deleteAllRecords(TABLE.SUBTITLE);
        deleteAllRecords(TABLE.SUBTITLE_WORDLIST);
        deleteAllRecords(TABLE.DIC);
    }
    public void addSubtitle(DicModel item) {
        addSubtitle(item, true);
    }

    public void updateSubtitle(DicModel item) {
        addSubtitle(item, false);
    }

    public void addSubtitle(DicModel item, boolean isAdd) {
        DLog.d(TAG, "addSubtitle - item=" + item.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getId());
        cv.put(COLUMN.LANG_STUDY, item.getLangStudy());
        cv.put(COLUMN.VOCA_TYPE, item.getVocaType());
        cv.put(COLUMN.VOCA_ID, item.getVocaId());
        cv.put(COLUMN.VOCA, item.getVocaDisplay());
        cv.put(COLUMN.VOCA_RUBY, StringUtils.replaceNewLineToBRTag(item.getVocaDisplayRuby()));
        cv.put(COLUMN.MEANING, item.getMeaning());
        cv.put(COLUMN.VOCA_KNOW, item.getVocaKnow());
        cv.put(COLUMN.START_TIME, item.getStartTime());
        cv.put(COLUMN.END_TIME, item.getEndTime());
        cv.put(COLUMN.BOOKMARK, item.getBookmark());
        cv.put(COLUMN.REPEAT, item.getVIRepeatCount());
        cv.put(COLUMN.START_TIME_ORIGINAL, item.getStartTimeOriginal());
        cv.put(COLUMN.END_TIME_ORIGINAL, item.getEndTimeOriginal());
        cv.put(COLUMN.VOCA_ORIGINAL, item.getSubtitleOriginal());
        cv.put(COLUMN.USED, item.getUsed());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getKnowPronounceBase());
        cv.put(COLUMN.MEMO, item.getMemo());
        if (isAdd) {
            int studyLang = EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG).getIdApi();
            cv.put(COLUMN.LANG_STUDY, studyLang);
            cv.put(COLUMN.USED, Constant.PLAYER.SUB_TITLE.USED.SHOW);
            insertIntoTable(TABLE.SUBTITLE, cv);
        } else {
            updateTableById(TABLE.SUBTITLE, cv, item.getId());
//            database.update(TABLE.SUBTITLE,
//                    cv, COLUMN.ID + "=?",
//                    new String[]{String.valueOf(item.getId())});
        }
    }
    
    public void addSubtitles(List<DicModel> items) {
        DLog.d(TAG, "addSubtitles - count=" + items.size());
        int studyLang = EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG).getIdApi();
        int chunkSize = 200; // 청크 단위 크기
        
        openWrite();
        database.beginTransaction();
        try {
            // SQLiteStatement 준비
            String sql = "INSERT INTO " + TABLE.SUBTITLE + " (" +
                    COLUMN.ID + ", " + COLUMN.LANG_STUDY + ", " + COLUMN.VOCA_TYPE + ", " + 
                    COLUMN.VOCA_ID + ", " + COLUMN.VOCA + ", " + COLUMN.VOCA_RUBY + ", " + 
                    COLUMN.MEANING + ", " + COLUMN.VOCA_KNOW + ", " + COLUMN.START_TIME + ", " + 
                    COLUMN.END_TIME + ", " + COLUMN.BOOKMARK + ", " + COLUMN.REPEAT + ", " + 
                    COLUMN.START_TIME_ORIGINAL + ", " + COLUMN.END_TIME_ORIGINAL + ", " + 
                    COLUMN.VOCA_ORIGINAL + ", " + COLUMN.VOCA_KNOWPRONOUNCE + ", " + 
                    COLUMN.MEMO + ", " + COLUMN.USED + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            android.database.sqlite.SQLiteStatement stmt = database.compileStatement(sql);
            
            for (int i = 0; i < items.size(); i += chunkSize) {
                int endIndex = Math.min(i + chunkSize, items.size());
                List<DicModel> chunk = items.subList(i, endIndex);
                
                // 청크 단위로 SQLiteStatement를 사용한 배치 삽입
                for (DicModel item : chunk) {
                    stmt.clearBindings();
                    stmt.bindLong(1, item.getId());
                    stmt.bindLong(2, studyLang);
                    stmt.bindLong(3, item.getVocaType());
                    stmt.bindLong(4, item.getVocaId());
                    stmt.bindString(5, item.getVocaDisplay());
                    stmt.bindString(6, StringUtils.replaceNewLineToBRTag(item.getVocaDisplayRuby()));
                    stmt.bindString(7, item.getMeaning());
                    stmt.bindLong(8, item.getVocaKnow());
                    stmt.bindLong(9, item.getStartTime());
                    stmt.bindLong(10, item.getEndTime());
                    stmt.bindLong(11, item.getBookmark());
                    stmt.bindLong(12, item.getVIRepeatCount());
                    stmt.bindLong(13, item.getStartTimeOriginal());
                    stmt.bindLong(14, item.getEndTimeOriginal());
                    stmt.bindString(15, item.getSubtitleOriginal());
                    stmt.bindLong(16, item.getKnowPronounceBase());
                    stmt.bindString(17, item.getMemo());
                    stmt.bindLong(18, Constant.PLAYER.SUB_TITLE.USED.SHOW);
                    stmt.executeInsert();
                }
            }
            
            stmt.close();
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void addWordInDicTableInSubtitleDb(DicModel item) {
        DLog.d(TAG, "addSubtitle - item=" + item.toString());
        ContentValues cv = new ContentValues();

        cv.put(COLUMN.ID, item.getId());
        cv.put(COLUMN.VOCA, item.getVIVoca());
        cv.put(COLUMN.VOCA_TTS, item.getVIVocaTTS());
        cv.put(COLUMN.VOCA_TYPE, item.getVocaType());
        cv.put(COLUMN.VOCA_ID, item.getVocaId());
        cv.put(COLUMN.VOCA_ID_TO_SEND_SERVER, item.getVocaId());
        cv.put(COLUMN.PRONOUNCE, item.getPronounce());
        cv.put(COLUMN.MEANING, item.getMeaning());
        cv.put(COLUMN.VOCA_KNOW, item.getVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVocaKnowPronounce());
        cv.put(COLUMN.MEANING_ENG, item.getMeaningEng());
        cv.put(COLUMN.MEANING_TTS, "");
        cv.put(COLUMN.BOOKMARK, item.getBookmark());
        cv.put(COLUMN.VOCA_LEVEL, item.getWordLevel());
        cv.put(COLUMN.FREQUENCY, item.getFrequency());
        cv.put(COLUMN.HANJA, "");
        cv.put(COLUMN.JMDICT_MEANING, "");
        cv.put(COLUMN.JMDICT_MEANING_ENG, "");
        cv.put(COLUMN.POSALL, "");
        cv.put(COLUMN.VOCA_APPEARANCE_ORDER, 0);
        cv.put(COLUMN.MEANING_DETAILED, item.getMeaningDetailed());
        insertIntoTable(TABLE.DIC, cv);
    }

    public void addWordsInDicTableInSubtitleDb(List<DicModel> items) {
        DLog.d(TAG, "addWordsInDicTableInSubtitleDb - count=" + items.size());
        int chunkSize = 200; // 청크 단위 크기
        
        openWrite();
        database.beginTransaction();
        try {
            // SQLiteStatement 준비
            String sql = "INSERT INTO " + TABLE.DIC + " (" +
                    COLUMN.ID + ", " + COLUMN.VOCA + ", " + COLUMN.VOCA_TTS + ", " + 
                    COLUMN.VOCA_TYPE + ", " + COLUMN.VOCA_ID + ", " + COLUMN.VOCA_ID_TO_SEND_SERVER + ", " + 
                    COLUMN.PRONOUNCE + ", " + COLUMN.MEANING + ", " + COLUMN.VOCA_KNOW + ", " + 
                    COLUMN.VOCA_KNOWPRONOUNCE + ", " + COLUMN.MEANING_ENG + ", " + COLUMN.MEANING_TTS + ", " + 
                    COLUMN.BOOKMARK + ", " + COLUMN.VOCA_LEVEL + ", " + COLUMN.FREQUENCY + ", " + 
                    COLUMN.HANJA + ", " + COLUMN.JMDICT_MEANING + ", " + COLUMN.JMDICT_MEANING_ENG + ", " + 
                    COLUMN.POSALL + ", " + COLUMN.VOCA_APPEARANCE_ORDER + ", " + COLUMN.MEANING_DETAILED + 
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            android.database.sqlite.SQLiteStatement stmt = database.compileStatement(sql);
            
            for (int i = 0; i < items.size(); i += chunkSize) {
                int endIndex = Math.min(i + chunkSize, items.size());
                List<DicModel> chunk = items.subList(i, endIndex);
                
                // 청크 단위로 SQLiteStatement를 사용한 배치 삽입
                for (DicModel item : chunk) {
                    stmt.clearBindings();
                    stmt.bindLong(1, item.getId());
                    stmt.bindString(2, item.getVIVoca());
                    stmt.bindString(3, item.getVIVocaTTS());
                    stmt.bindLong(4, item.getVocaType());
                    stmt.bindLong(5, item.getVocaId());
                    stmt.bindLong(6, item.getVocaId());
                    stmt.bindString(7, item.getPronounce());
                    stmt.bindString(8, item.getMeaning());
                    stmt.bindLong(9, item.getVocaKnow());
                    stmt.bindLong(10, item.getVocaKnowPronounce());
                    stmt.bindString(11, item.getMeaningEng());
                    stmt.bindString(12, "");
                    stmt.bindLong(13, item.getBookmark());
                    stmt.bindLong(14, item.getWordLevel());
                    stmt.bindLong(15, item.getFrequency());
                    stmt.bindString(16, "");
                    stmt.bindString(17, "");
                    stmt.bindString(18, "");
                    stmt.bindString(19, "");
                    stmt.bindLong(20, 0);
                    stmt.bindString(21, item.getMeaningDetailed());
                    stmt.executeInsert();
                }
            }
            
            stmt.close();
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void addItemInSubtitleWordListInSubtitleDb(SubtitleWordListModel item) {
        DLog.d(TAG, "addSubtitle - item=" + item.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getId());
        cv.put(COLUMN.SUBTITLE_ID, item.getSubtitleId());
        cv.put(COLUMN.VOCA_TYPE, item.getVocaType());
        cv.put(COLUMN.VOCA_ID, item.getVocaId());
        insertIntoTable(TABLE.SUBTITLE_WORDLIST, cv);
    }

    public void addItemsInSubtitleWordListInSubtitleDb(List<SubtitleWordListModel> items) {
        DLog.d(TAG, "addItemsInSubtitleWordListInSubtitleDb - count=" + items.size());
        int chunkSize = 200; // 청크 단위 크기
        
        openWrite();
        database.beginTransaction();
        try {
            // SQLiteStatement 준비
            String sql = "INSERT INTO " + TABLE.SUBTITLE_WORDLIST + " (" +
                    COLUMN.ID + ", " + COLUMN.SUBTITLE_ID + ", " + COLUMN.VOCA_TYPE + ", " + 
                    COLUMN.VOCA_ID + ") VALUES (?, ?, ?, ?)";
            
            android.database.sqlite.SQLiteStatement stmt = database.compileStatement(sql);
            
            for (int i = 0; i < items.size(); i += chunkSize) {
                int endIndex = Math.min(i + chunkSize, items.size());
                List<SubtitleWordListModel> chunk = items.subList(i, endIndex);
                
                // 청크 단위로 SQLiteStatement를 사용한 배치 삽입
                for (SubtitleWordListModel item : chunk) {
                    stmt.clearBindings();
                    stmt.bindLong(1, item.getId());
                    stmt.bindLong(2, item.getSubtitleId());
                    stmt.bindLong(3, item.getVocaType());
                    stmt.bindLong(4, item.getVocaId());
                    stmt.executeInsert();
                }
            }
            
            stmt.close();
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updateTranslate(DicModel item) {
        DLog.d(TAG, "updateTranslate");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.MEANING, item.getMeaning());
        updateTableById(TABLE.SUBTITLE, cv, item.getId());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(item.getId())});
//        close();
    }

    public void updateMultipleWordMeaning(DicModel item) {
        DLog.d(TAG, "updateSubtitle");
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_ID, item.getVocaId());
        cv.put(COLUMN.VOCA, item.getVocaDisplay());
        cv.put(COLUMN.VOCA_RUBY, StringUtils.replaceNewLineToBRTag(item.getVocaDisplayRuby()));
        cv.put(COLUMN.MEANING, item.getMeaning());
        updateTableById(TABLE.SUBTITLE, cv, item.getId());
//        openWrite();
//        database.update(TABLE.SUBTITLE,
//                cv, COLUMN.ID + "=?",
//                new String[]{String.valueOf(item.getId())});
//        close();
    }

    public void deleteSubtitle(int id) {
        deleteSubtitleInSubtitleTable(id);
        deleteSubtitleInWordListTable(id);
    }

    public void deleteSubtitleInSubtitleTable(int id) {
        DLog.d(TAG, "deleteSubtitle - id=" + id);
        openWrite();
        database.delete(TABLE.SUBTITLE,
                COLUMN.ID + "=?",
                new String[]{String.valueOf(id)});
        close();
    }
    public void deleteSubtitlesInSubtitleTable(List<Integer> ids) {
        DLog.d(TAG, "deleteSubtitles - ids=" + ids);
        openWrite();
//        String whereClause1 = ids.stream()
//                .map(e -> QUERY.PARENTHESIS_OPEN + COLUMN.ID + QUERY.EQUAL + e + QUERY.PARENTHESIS_CLOSE)
//                .collect(Collectors.joining( QUERY.OR));
//        database.delete(TABLE.SUBTITLE, whereClause1,null);

        String whereClause = COLUMN.ID + QUERY.IN_OPEN + TextUtils.join(",", Collections.nCopies(ids.size(), "?")) + QUERY.IN_CLOSE;
        String[] whereArgs = ids.stream().map(Object::toString).toArray(String[]::new);
        //String[] whereArgs = ids.toArray(new String[ids.size()]); //이건 integer를 바로 string으로 담기 때문에 에러가 난다.
        database.delete(TABLE.SUBTITLE, whereClause, whereArgs);
        close();
    }

    public void deleteSubtitleInWordListTable(int subtitleId) {
        DLog.d(TAG, "deleteSubtitle - id=" + subtitleId);
        openWrite();
        database.delete(TABLE.SUBTITLE_WORDLIST,
                COLUMN.SUBTITLE_ID + "=?",
                new String[]{String.valueOf(subtitleId)});
        close();
    }

    private String getQueryDataChanged() {
        return QUERY.SELECT_ALL + TABLE.SUBTITLE + QUERY.WHERE + COLUMN.BOOKMARK + " = 1 OR " +
                COLUMN.START_TIME + " != " + COLUMN.START_TIME_ORIGINAL + " OR " + COLUMN.END_TIME + " != " + COLUMN.END_TIME_ORIGINAL +
                " OR " + COLUMN.USED + "!= " + Constant.PLAYER.SUB_TITLE.USED.SHOW;
    }

    public List<DicModel> getDataChanged() {
        DLog.d(TAG, "getDataChanged");
        openRead();
        String queryBookmark = getQueryDataChanged();
        DLog.d(TAG, "checkDBChanged - queryBookmark=" + queryBookmark);
        Cursor cursor = database.rawQuery(queryBookmark, null);
        cursor.moveToFirst();
        List<DicModel> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            final DicModel item = new DicModel();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
            item.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME)));
            item.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME)));
            item.setBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
            item.setUsed(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USED)));
            list.add(item);
            cursor.moveToNext();
        }
        close();
        return list;
    }

    public int getDataChangedSize() {
        try {
            DLog.d(TAG, "getDataChangedSize");
            openRead();
            String queryBookmark = getQueryDataChanged();
            DLog.d(TAG, "getDataChangedSize - queryBookmark=" + queryBookmark);
            Cursor cursor = database.rawQuery(queryBookmark, null);
            int count = cursor.getCount();
            cursor.close();
            close();
            DLog.d(TAG, "getDataChangedSize - count=" + count);
            return count;
        } catch (Exception ex) {

        }
        return 0;
    }

    public void updateDataFromRefreshSubtitle(List<DicModel> list) {
        DLog.d(TAG, "updateDataFromRefreshSubtitle");
        openWrite();
        for (DicModel item : list) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.BOOKMARK, item.getBookmark());
            cv.put(COLUMN.START_TIME, item.getStartTime());
            cv.put(COLUMN.END_TIME, item.getEndTime());
            cv.put(COLUMN.USED, item.getUsed());
            updateTableById(TABLE.SUBTITLE, cv, item.getId());
        }
        close();
    }

    public List<com.dalread.database.sqlite.model.SubtitleModel> getSubtitleModelByUsed(boolean isHidedSubtitle, int delaySubtitleTime) {
        DLog.d(TAG, "getSubtitleModelByUsed");
        openRead();
        String option = Constant.BASE_BLANK;
        if (!isHidedSubtitle) {
            option = QUERY.WHERE + COLUMN.USED + " = " + Constant.PLAYER.SUB_TITLE.USED.SHOW;
        }
        String query = QUERY.GET_SUBTITLE + option + QUERY.ORDER_BY + COLUMN.ID + QUERY.ASC;
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        List<com.dalread.database.sqlite.model.SubtitleModel> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            com.dalread.database.sqlite.model.SubtitleModel item = new com.dalread.database.sqlite.model.SubtitleModel();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
            item.setLangStudy(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LANG_STUDY)));
            item.setSubtitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
            item.setSubtitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
            item.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
            item.setStarTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME)) + delaySubtitleTime);
            item.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME)) + delaySubtitleTime);
            list.add(item);
            cursor.moveToNext();
        }
        close();
        return list;
    }

    public int getVocaKnowCount() {
        return getVocaKnowCount(null);
    }

    public int getVocaKnowCount(String langStudy) {
        String query = QUERY.GET_DIC_SIZE + QUERY.WHERE + COLUMN.VOCA_KNOW + "=" + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
        if (!Utils.isEmpty(langStudy)) {
            query += QUERY.AND + COLUMN.LANG_STUDY + "=" + langStudy;
        }
        return getRecordsCount(query);
    }

    public int getVocaKnowAll() {
        return getVocaKnowAll(null);
    }

    public int getVocaKnowAll(String langStudy) {
        String query = QUERY.GET_DIC_SIZE;
        if (!Utils.isEmpty(langStudy)) {
            query += QUERY.WHERE + COLUMN.LANG_STUDY + "=" + langStudy;
        }
        return getRecordsCount(query);
    }

    public int getMaxIdByTable(String column, String table) {
        String query = "SELECT " + column + " FROM " + table + QUERY.ORDER_BY + column + QUERY.DESC + QUERY.LIMIT + 1;
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        int maxId = cursor.getInt(cursor.getColumnIndexOrThrow(column));
        cursor.close();
        close();
        return maxId;
    }

    private SubModel parserSubModel(Cursor cursor) {
        SubModel model = new SubModel();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setLanguage(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LANG_STUDY)));
        model.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
        model.setContentRuby(StringUtils.replaceHTML(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_RUBY))));
        model.setStart(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME)));
        model.setEnd(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME)));
        model.setStartTimeOriginal(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME_ORIGINAL)));
        model.setEndTimeOriginal(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME_ORIGINAL)));
        return model;
    }

    private SubtitleWordListModel parserSubtitleWordList(Cursor cursor) {
        SubtitleWordListModel model = new SubtitleWordListModel();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setSubtitleId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SUBTITLE_ID)));
        model.setVocaType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE)));
        model.setVocaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
        return model;
    }

    private MediaInfoModel parserMediaInfo(Cursor cursor) {
        MediaInfoModel model = new MediaInfoModel();
        model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.TITLE)));
        model.setTitleTts(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.TITLE_TTS)));
        model.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.ARTIST)));
        model.setArtistTts(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.ARTIST_TTS)));
        model.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.ALBUM)));
        model.setLyricCreator(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.LYRIC_CREATOR)));
        model.setLyricFileCreator(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.LYRIC_FILE_CREATOR)));
        model.setLyricFileEditor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.LYRIC_FILE_EDITOR)));
        model.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LENGTH)));
        model.setVersion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VERSION)));
        return model;
    }

    private DicModel parserDicModelFromSubtitleTable(Cursor cursor) {
        final DicModel model = new DicModel();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setLangStudy(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LANG_STUDY)));
        model.setVocaType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE)));
        model.setVocaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
        model.setVocaTypeBase(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE_BASE)));
        model.setVocaIdBase(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID_BASE)));
        model.setVocaDisplay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
        model.setVocaDisplayRuby(StringUtils.replaceHTML(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_RUBY))));
        model.setStartTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME)));
        model.setEndTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME)));
        model.setPath(Voca.getOutputRecordingFileName(studyLanguage, model.getVocaType(), model.getVocaId(), uid));
        model.setBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVIRepeatCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.REPEAT)));
        model.setStartTimeOriginal(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.START_TIME_ORIGINAL)));
        model.setEndTimeOriginal(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN.END_TIME_ORIGINAL)));
        model.setUsed(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USED)));
        model.setSubtitleWordlistId(String.valueOf(model.getId()));
        model.setVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setSubtitleOriginal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ORIGINAL)));
        model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
//        model.setVocaIdServer(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID_TO_SEND_SERVER)));
        model.setMemo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEMO)));
        return model;
    }

    private DicModel parserDicModel(Cursor cursor) {
        DicModel model = new DicModel();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setVocaDisplay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
        model.setVocaTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TTS)));
        model.setVocaType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE)));
        model.setVocaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
        model.setPronounce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE)));
        model.setVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
        model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TTS)));
        model.setMeaningEng(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setJmdictMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.JMDICT_MEANING)));
        model.setJmdictMeaningEng(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.JMDICT_MEANING_ENG)));
        model.setBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setWordLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_LEVEL)));
        model.setFrequency(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.FREQUENCY)));
        model.setVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setStartTime(model.getId());
        model.setPath(Voca.getOutputRecordingFileName(studyLanguage, model.getVocaType(), model.getVocaId(), uid));
//        model.setVocaIdServer(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID_TO_SEND_SERVER)));
        model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DETAILED)));
        model.setHanja(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.HANJA)));
        model.setPosAll(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.POSALL)));
        return model;
    }

    private RubyTextModel parserToRubyTextModel(Cursor cursor) {
        RubyTextModel model = new RubyTextModel();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setVocaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
        model.setVocaType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE)));
        model.setVoca(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
        model.setPronounce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE)));
        model.setVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
        model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TTS)));
        model.setMeaningEng(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setJmdictMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.JMDICT_MEANING)));
        model.setJmdictMeaningEng(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.JMDICT_MEANING_ENG)));
        return model;
    }

    //Dalnim Add
    public int getMinColumnValueInTbl(String column, String table) {
//        String query = "SELECT MIN(" + column + ") FROM " + table;
        String query = QUERY.SELECT + QUERY.MIN + QUERY.PARENTHESIS_OPEN + column + QUERY.PARENTHESIS_CLOSE + QUERY.FROM + table;

        openRead();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        cursor.close();
        close();
        return id;
    }

    //Dalnim add
    public int getNewIDInSubtitleTable() {
        String column = Constant.PLAYER.SQL.COLUMN.ID;
        int value = getMaxColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
        return value + 1;
    }

    //Dalnim add
    public int getNewVocaIDInSubtitleTable() {
        String column = Constant.PLAYER.SQL.COLUMN.VOCA_ID;
        int value = getMinColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
        if (value >= 0) {
            return -1;
        }
        return value - 1;
    }

    //Dalnim Add
    public int getMaxColumnValueInTbl(String column, String table) {
        String query = QUERY.SELECT + QUERY.MAX + QUERY.PARENTHESIS_OPEN + column + QUERY.PARENTHESIS_CLOSE + QUERY.FROM + table;
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToLast();
        int id = cursor.getInt(0);
        cursor.close();
        close();
        return id;
    }

    public void updateTableAllRows(String tblName, ContentValues cv) {
        openWrite();
        database.update(tblName, cv, null, null);
        close();
    }

//    public void updateTableById(String tblName, ContentValues cv, int id) {
//        openWrite();
//        database.update(tblName, cv, COLUMN.ID + "=?", new String[]{String.valueOf(id)});
//        close();
//    }

    public void updateTableByVocaTypeId(String tblName, ContentValues cv, int vocaType, int vocaId) {
        openWrite();
        database.update(tblName,
                cv, COLUMN.VOCA_TYPE + "=? AND " + COLUMN.VOCA_ID + "=?",
                new String[]{String.valueOf(vocaType), String.valueOf(vocaId)});
        close();
    }

    public void updateTableByVocaTypeIdBase(ContentValues cv, int vocaTypeBase, int vocaIdBase) {
        openWrite();
        database.update(TABLE.SUBTITLE,
                cv, COLUMN.VOCA_TYPE_BASE + "=? AND " + COLUMN.VOCA_ID_BASE + "=?",
                new String[]{String.valueOf(vocaTypeBase), String.valueOf(vocaIdBase)});
        close();
    }

    public void updateTableByMultipleVocaTypeId(String tblName, ContentValues cv, List<IVocaBasicItem> item) {
        String whereClause = item.stream()
                .map(e -> QUERY.PARENTHESIS_OPEN + COLUMN.VOCA_TYPE + QUERY.EQUAL + e.getVIVocaType() + QUERY.AND
                        + COLUMN.VOCA_ID + QUERY.EQUAL + e.getVIVocaId() + QUERY.PARENTHESIS_CLOSE)
                .collect(Collectors.joining( QUERY.OR));
        openWrite();
        database.update(tblName, cv, whereClause,null);
        close();
    }

    //이건 DicSentenceSubDatabase 껄 쓰도록 해야 한다. AraPlayer에서도 DicSentenceSubDatabase를 상속받게 해야 한다.
    public List<IVocaFullPlayTTSItem> getAllWordListWordListWithComma1(String wordListWithComma) {
        if (wordListWithComma.trim().equals(""))
            return Collections.emptyList();
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE;
        String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + wordListWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
        return getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
//        return Collections.emptyList();
    }

    public List<IVocaFullPlayTTSItem> getAllWordListWordListWithComma(String wordListWithComma) {
        if (wordListWithComma.trim().equals("")) {
            return Collections.emptyList();
        }

        // Split the wordListWithComma into a list of words
        String[] wordsArray = wordListWithComma.split(",");
        List<String> wordsList = Arrays.asList(wordsArray);

        // Divide the list into chunks of 100 words each
        final int chunkSize = 300;
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < wordsList.size(); i += chunkSize) {
            int end = Math.min(wordsList.size(), i + chunkSize);
            chunks.add(wordsList.subList(i, end));
        }

        // Prepare the final list to collect results
        List<IVocaFullPlayTTSItem> finalList = new ArrayList<>();

        // Process each chunk
        for (List<String> chunk : chunks) {
            String chunkWithComma = String.join(",", chunk);
            String query = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE;
            String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + chunkWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
            List<IVocaFullPlayTTSItem> chunkResult = getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
            finalList.addAll(chunkResult);
        }

        return finalList;
    }

    //이건 AraConv의 SubDatabase와 중복된다.
//    public List<GptTextShortCut> getGptShortCutListForPopupMenu1() {
//        return getGptShortCutListForPopupMenu(COLUMN.USE_POPUP_1_MENU);
//    }
//
//    private List<GptTextShortCut> getGptShortCutListForPopupMenu(String popupMenuFieldName) {
//        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
//                + QUERY.WHERE + popupMenuFieldName + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
//        return getGptTextShortCutBySQL(query);
//    }
//
//    @NonNull
//    private static String getUsedAppColumnName() {
//        String usedColumnName = COLUMN.USED_ARACONV;
//        if (AppFlavorUtil.isAraHangulApp()) {
//            usedColumnName = COLUMN.USED_ARAHANGUL;
//        }
//        return usedColumnName;
//    }
//
//    protected List<GptTextShortCut>  getGptTextShortCutBySQL(String query) {
//        List<GptTextShortCut> itemList = new ArrayList<>();
//        try {
//            openRead();
//            Cursor cursor = database.rawQuery(query, null);
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                itemList.add(parserGptTextShortCut(cursor));
//                cursor.moveToNext();
//            }
//            cursor.close();
//        } catch (Exception ex) {
//            DLog.e(TAG, ex.getMessage());
//        } finally {
//            close();
//        }
//        return itemList;
//    }
//
//    private GptTextShortCut parserGptTextShortCut(Cursor cursor) {
//        GptTextShortCut model = new GptTextShortCut();
//        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setSystemDefault(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SYSTEM_DEFAULT)));
//        model.setDisplayOrder(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.DISP_ORDER)));
//        model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.TITLE)));
////        model.setEdited(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.EDITED)));
//        model.setUsedPrompt(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_PROMPT)));
//        model.setUsePopup1Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_1_MENU)));
//        model.setUsePopup2Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_2_MENU)));
//        model.setUsePopup3Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_3_MENU)));
//
//        String languageName; // = EnumLanguage.KOREAN.getLangaugeName();;
//        String languageNameMotherTongue;
//        String meaning;
//        String studyLangMarker = "[[STUDY_LANG_NAME]]";
//        String motherTongueLangMarker = "[[MOTHER_TONGUE_LANG_NAME]]";
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.KOREAN);
//        languageNameMotherTongue = EnumLanguage.getLangNameByLanguage(enumMotherTongueLanguage, EnumLanguage.KOREAN);
////        languageName = EnumLanguage.KOREAN.getStudyLangEnglishByMotherTongueName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO));
//        model.setMEANING_KO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ENGLISH);
////        languageName = EnumLanguage.ENGLISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG));
//        model.setMEANING_ENG(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.JAPANESE);
////        languageName = EnumLanguage.JAPANESE.getStudyLangEnglishByMotherTongueName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP));
//        model.setMEANING_JP(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CHINESE_SIMPLIFIED);
////        languageName = EnumLanguage.CHINESE_SIMPLIFIED.getStudyLangEnglishByMotherTongueName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S));
//        model.setMEANING_CH_S(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CHINESE_TRADITIONAL);
////        languageName = EnumLanguage.CHINESE_TRADITIONAL.getStudyLangEnglishByMotherTongueName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T));
//        model.setMEANING_CH_T(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.FRENCH);
////        languageName = EnumLanguage.FRENCH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR));
//        model.setMEANING_FR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.RUSSIAN);
////        languageName = EnumLanguage.RUSSIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU));
//        model.setMEANING_RU(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ARABIC);
////        languageName = EnumLanguage.ARABIC.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR));
//        model.setMEANING_AR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.BENGALI);
////        languageName = EnumLanguage.BENGALI.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN));
//        model.setMEANING_BN(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CROATIAN);
////        languageName = EnumLanguage.CROATIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR));
//        model.setMEANING_HR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CZECH);
////        languageName = EnumLanguage.CZECH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS));
//        model.setMEANING_CS(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.DANISH);
////        languageName = EnumLanguage.DANISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA));
//        model.setMEANING_DA(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.DUTCH);
////        languageName = EnumLanguage.DUTCH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL));
//        model.setMEANING_NL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.FINNISH);
////        languageName = EnumLanguage.FINNISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI));
//        model.setMEANING_FI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.GERMAN);
////        languageName = EnumLanguage.GERMAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE));
//        model.setMEANING_DE(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.GREEK);
////        languageName = EnumLanguage.GREEK.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL));
//        model.setMEANING_EL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HEBREW);
////        languageName = EnumLanguage.HEBREW.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE));
//        model.setMEANING_HE(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HINDI);
////        languageName = EnumLanguage.HINDI.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI));
//        model.setMEANING_HI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HUNGARIAN);
////        languageName = EnumLanguage.HUNGARIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU));
//        model.setMEANING_HU(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.INDONESIAN);
////        languageName = EnumLanguage.INDONESIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID));
//        model.setMEANING_ID(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ITALIAN);
////        languageName = EnumLanguage.ITALIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT));
//        model.setMEANING_IT(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.NORWEGIAN);
////        languageName = EnumLanguage.NORWEGIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO));
//        model.setMEANING_NO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.POLISH);
////        languageName = EnumLanguage.POLISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL));
//        model.setMEANING_PL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.PORTUGUESE);
////        languageName = EnumLanguage.PORTUGUESE.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT));
//        model.setMEANING_PT(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ROMANIAN);
////        languageName = EnumLanguage.ROMANIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO));
//        model.setMEANING_RO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SLOVAK);
////        languageName = EnumLanguage.SLOVAK.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK));
//        model.setMEANING_SK(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SPANISH);
////        languageName = EnumLanguage.SPANISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES));
//        model.setMEANING_ES(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SWEDISH);
////        languageName = EnumLanguage.SWEDISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV));
//        model.setMEANING_SV(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.THAI);
////        languageName = EnumLanguage.THAI.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH));
//        model.setMEANING_TH(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.TURKISH);
////        languageName = EnumLanguage.TURKISH.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR));
//        model.setMEANING_TR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.UKRAINIAN);
////        languageName = EnumLanguage.UKRAINIAN.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK));
//        model.setMEANING_UK(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.VIETNAMESE);
////        languageName = EnumLanguage.VIETNAMESE.getLangaugeName();
//        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI));
//        model.setMEANING_VI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));
//
//        return model;
//    }
//
//    public List<GptTextShortCut> getGptShortCutList() {
//        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
//                + QUERY.WHERE + COLUMN.USE_PROMPT + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
//        return getGptTextShortCutBySQL(query);
//    }
//
//    public void updateGptShortCut(GptTextShortCut item) {
//        ContentValues cv = fillContentValues(item);
//        cv.put(COLUMN.DISP_ORDER, item.getDisplayOrder());
//        updateTableById(TABLE_GPT_TEXT_SHORT_CUT, cv, item.getId());
//    }
//
//    public boolean insertGptShortCut(GptTextShortCut item) {
//        ContentValues cv = fillContentValues(item);
//        cv.put(COLUMN.SYSTEM_DEFAULT, Constant.INT_BOOLEAN.FASLE);
//        cv.put(getUsedAppColumnName(), Constant.INT_BOOLEAN.TRUE);
//        cv.put(COLUMN.DISP_ORDER, Constant.DEFAULT_DISP_ORDER);
//        cv.put(COLUMN.USE_POPUP_1_MENU, item.getUsePopup1Menu());
//        cv.put(COLUMN.USE_POPUP_2_MENU, item.getUsePopup2Menu());
//        cv.put(COLUMN.USE_POPUP_3_MENU, item.getUsePopup3Menu());
//        long newRowId = insertIntoTable(TABLE_GPT_TEXT_SHORT_CUT, cv);
//        DLog.d("insertGptRequest", "newRowId : " + newRowId);
//        return newRowId == -1 ? false : true;
//    }
//    private ContentValues fillContentValues(GptTextShortCut item) {
//        ContentValues cv = new ContentValues();
//        String meaningFld = getMeaningFldValue(enumMenuLanguage);
//        String meaningValue = item.getMEANING(enumMenuLanguage);
//        cv.put(COLUMN.TITLE, item.getTitle());
//        cv.put(meaningFld, meaningValue);
//        cv.put(COLUMN.DISP_ORDER, item.getDisplayOrder());
//        return cv;
//    }
    //아라플레이어에는 단어만 한다.
    public List<IVocaFullPlayTTSItem> getPlayTTSVocaListByVocaIdList(List<? extends IVocaCoreItem> itemList) {
        String idsWord = itemList.stream().filter(e -> BaseVocaKnow.isVocaTypeWord(e)).map(e -> e.getVIVocaId().toString()).collect(Collectors.joining(","));
        String queryWord = QUERY.GET_DIC + QUERY.WHERE + COLUMN.VOCA_ID + QUERY.IN_OPEN + idsWord + QUERY.IN_CLOSE + QUERY.FINISH;
        List<DicModel> listWord = getDicModelWordCommon(queryWord);
        Collections.sort(listWord, Comparator.nullsLast(Comparator.comparing(IVocaFullPlayTTSItem::getVIVoca)));
        return VocaListUtil.convertListDicModelToVocaFullPlayerTtsItemList(listWord);
    }
}
