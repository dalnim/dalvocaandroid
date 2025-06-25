package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.VocaKnowGroupSelect;
import com.dalread.model.VocaKnowGroupSelected;
import com.dalread.model.VocaTypeId;
import com.dalread.model.VocaTypeIdListWithComma;
import com.dalread.model.WordListType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class BaseVocaList {

    //getWordListByWordListGroup은 왜 object를 리턴하는지 모르겠네. 헤더를 포함할려고 그러나? 그리고 IVocaFullItem보다는 IVocaFullPlayTTSItem를 사용해야할듯.
    @NotNull
    public static List<IVocaFullPlayTTSItem> getWordListGroupByGrade(VocaKnowGroupSelect vocaKnowGroupSelect, List<IVocaFullPlayTTSItem> totalItems) {
        List<IVocaFullPlayTTSItem> data = new ArrayList<>();
        List<IVocaFullPlayTTSItem> grade1 = new ArrayList<>();
        List<IVocaFullPlayTTSItem> grade2 = new ArrayList<>();
        List<IVocaFullPlayTTSItem> unknown = new ArrayList<>();
        List<IVocaFullPlayTTSItem> known = new ArrayList<>();
        List<IVocaFullPlayTTSItem> notRated = new ArrayList<>();

        for (IVocaFullPlayTTSItem item : totalItems) {
            if (BaseVocaKnow.isKnown(item)) {
                known.add(item);
            } else if (BaseVocaKnow.isAmkiGrade1(item)) {
                grade1.add(item);
            } else if (BaseVocaKnow.isAmkiGrade2(item)) {
                grade2.add(item);
            } else if (BaseVocaKnow.isUnknown(item)) {
                unknown.add(item);
            } else {
                notRated.add(item);
            }
        }

        if (vocaKnowGroupSelect.isAmki1st() && !Utils.isEmpty(grade1)) {
            data.addAll(grade1);
        }
        if (vocaKnowGroupSelect.isAmki2nd() && !Utils.isEmpty(grade2)) {
            data.addAll(grade2);
        }
        if (vocaKnowGroupSelect.isUnknown() && !Utils.isEmpty(unknown)) {
            data.addAll(unknown);
        }
        if (vocaKnowGroupSelect.isNotRated() && !Utils.isEmpty(notRated)) {
            data.addAll(notRated);
        }
        if (vocaKnowGroupSelect.isKnown() && !Utils.isEmpty(known)) {
            data.addAll(known);
        }


        return data;
    }

    @NotNull
    public static Map<String, IVocaFullPlayTTSItem> getVocaTypeIdMapByFilter(VocaKnowGroupSelect vocaKnowGroupSelect, List<IVocaFullPlayTTSItem> totalItems) {
        Map<String, IVocaFullPlayTTSItem> data = new HashMap<>();

        for (IVocaFullPlayTTSItem item : totalItems) {
            String vocaTypeId = item.getVIVocaTypeId();
            if (vocaKnowGroupSelect.isKnown() && BaseVocaKnow.isKnown(item)) {
                data.put(vocaTypeId, item);
            } else if (vocaKnowGroupSelect.isAmki1st() && BaseVocaKnow.isAmkiGrade1(item)) {
                data.put(vocaTypeId, item);
            } else if (vocaKnowGroupSelect.isAmki2nd() && BaseVocaKnow.isAmkiGrade2(item)) {
                data.put(vocaTypeId, item);
            } else if (vocaKnowGroupSelect.isUnknown() && BaseVocaKnow.isUnknown(item)) {
                data.put(vocaTypeId, item);
            } else if (vocaKnowGroupSelect.isNotRated() && BaseVocaKnow.isNotRated(item)) {
                data.put(vocaTypeId, item);
            } else if (vocaKnowGroupSelect.isBookmarked() && Voca.isBookmark(item)) {
                data.put(vocaTypeId, item);
            }
        }
        return data;
    }
    @NotNull
    public static Map<String, IVocaFullPlayTTSItem> getVocaTypeIdMapByBookmarked(VocaKnowGroupSelect vocaKnowGroupSelect, List<IVocaFullPlayTTSItem> totalItems) {
        Map<String, IVocaFullPlayTTSItem> data = new HashMap<>();

        for (IVocaFullPlayTTSItem item : totalItems) {
            String vocaTypeId = item.getVIVocaTypeId();
            if (vocaKnowGroupSelect.isBookmarked() && Voca.isBookmark(item)) {
                data.put(vocaTypeId, item);
            }
        }
        return data;
    }
    @NotNull
    public static Map<String, IVocaFullPlayTTSItem> convertVocaTypeIdMap(List<IVocaFullPlayTTSItem> totalItems) {
        Map<String, IVocaFullPlayTTSItem> data = new HashMap<>();
        for (IVocaFullPlayTTSItem item : totalItems) {
            String vocaTypeId = item.getVIVocaTypeId();
            data.put(vocaTypeId, item);
        }
        return data;
    }
    /*
     * @deprecated Replaced by {@link #getWordListGroupByGrade(VocaKnowGroupSelect, List<? extends IVocaFullItem>)}
     */
    @NotNull
    public static List<Object> getWordListByWordListGroup(VocaKnowGroupSelect vocaKnowGroupSelect, List<? extends IVocaFullItem> totalItems) {
        ArrayList<Object> data = new ArrayList<>();
        ArrayList<Object> grade1 = new ArrayList<>();
        ArrayList<Object> grade2 = new ArrayList<>();
        ArrayList<Object> unknown = new ArrayList<>();
        ArrayList<Object> known = new ArrayList<>();
        ArrayList<Object> notRated = new ArrayList<>();

        for (IVocaBasicItem item : totalItems) {
            if (BaseVocaKnow.isKnown(item)) {
                known.add(item);
            } else if (BaseVocaKnow.isAmkiGrade1(item)) {
                grade1.add(item);
            } else if (BaseVocaKnow.isAmkiGrade2(item)) {
                grade2.add(item);
            } else if (BaseVocaKnow.isUnknown(item)) {
                unknown.add(item);
            } else {
                notRated.add(item);
            }
        }

        if (vocaKnowGroupSelect.isAmki1st() && !Utils.isEmpty(grade1)) {
            data.addAll(grade1);
        }
        if (vocaKnowGroupSelect.isAmki2nd() && !Utils.isEmpty(grade2)) {
            data.addAll(grade2);
        }
        if (vocaKnowGroupSelect.isUnknown() && !Utils.isEmpty(unknown)) {
            data.addAll(unknown);
        }
        if (vocaKnowGroupSelect.isNotRated() && !Utils.isEmpty(notRated)) {
            data.addAll(notRated);
        }
        if (vocaKnowGroupSelect.isKnown() && !Utils.isEmpty(known)) {
            data.addAll(known);
        }


        return data;
    }

    public static <T extends IVocaFullPlayTTSItem> VocaKnowGroupSelected<T> getWordListByWordListGroup(Context context, List<T> totalItems) {
        VocaKnowGroupSelected<T> vocaKnowGroupSelected = new VocaKnowGroupSelected<>(context, VocaKnowGroupSelected.Type.AMKI_GRADE);

        for (T item : totalItems) {
            vocaKnowGroupSelected.addItem(item);
        }

        vocaKnowGroupSelected.populateAllList();

        return vocaKnowGroupSelected;
    }

//
//    public static <T extends IVocaFullItem> VocaKnowGroupSelected<T> getWordListByWordListGroup(List<T> totalItems) {
//        VocaKnowGroupSelected<T> vocaKnowGroupSelected = new VocaKnowGroupSelected<>();
//
//        for (IVocaBasicItem item : totalItems) {
//            if (BaseVocaKnow.isNotRated(item)) {
//                vocaKnowGroupSelected.getNotRatedList().add((T)item);
//            } else if (BaseVocaKnow.isAmkiGrade1(item)) {
//                vocaKnowGroupSelected.getGrade1List().add((T)item);
//            } else if (BaseVocaKnow.isAmkiGrade2(item)) {
//                vocaKnowGroupSelected.getGrade2List().add((T)item);
//            } else if (BaseVocaKnow.isUnknown(item)) {
//                vocaKnowGroupSelected.getUnknownList().add((T)item);
//            } else {
//                vocaKnowGroupSelected.getKnownList().add((T) item);
//            }
//        }
//        vocaKnowGroupSelected.getAllVocaList().addAll(vocaKnowGroupSelected.getNotRatedList());
//        vocaKnowGroupSelected.getAllVocaList().addAll(vocaKnowGroupSelected.getGrade1List());
//        vocaKnowGroupSelected.getAllVocaList().addAll(vocaKnowGroupSelected.getGrade2List());
//        vocaKnowGroupSelected.getAllVocaList().addAll(vocaKnowGroupSelected.getUnknownList());
//        vocaKnowGroupSelected.getAllVocaList().addAll(vocaKnowGroupSelected.getKnownList());
//
//        vocaKnowGroupSelected.getHeaderModelNotRated().setSize(vocaKnowGroupSelected.getNotRatedList().size());
//        vocaKnowGroupSelected.getHeaderModelGrade1().setSize(vocaKnowGroupSelected.getGrade1List().size());
//        vocaKnowGroupSelected.getHeaderModelGrade2().setSize(vocaKnowGroupSelected.getGrade2List().size());
//        vocaKnowGroupSelected.getHeaderModelUnknown().setSize(vocaKnowGroupSelected.getUnknownList().size());
//        vocaKnowGroupSelected.getHeaderModelKnown().setSize(vocaKnowGroupSelected.getKnownList().size());
//
//        return vocaKnowGroupSelected;
//    }

//    @NotNull
//    public static List<IVocaCoreItem> convertToVocaCoreItemList(List<? extends IVocaCoreItem> totalItems) {
//        List<IVocaCoreItem> iVocaCoreItemList = new ArrayList<>();
//        for(IVocaCoreItem iVocaCoreItem : totalItems) {
//            iVocaCoreItemList.add(iVocaCoreItem);
//        }
//        return iVocaCoreItemList;
//    }

    @NotNull
    public static List<VocaTypeId> convertToVocaTypeIdList(List<? extends IVocaCoreItem> totalItems) {
        if (totalItems == null) {
            return Collections.emptyList();
        }
        List<VocaTypeId> vocaTypeIdList = new ArrayList<>();
        for(IVocaCoreItem iVocaCoreItem : totalItems) {
            VocaTypeId vocaTypeId = new VocaTypeId(iVocaCoreItem.getVIVocaId(), iVocaCoreItem.getVIVocaType());
            vocaTypeIdList.add(vocaTypeId);
        }
        return vocaTypeIdList;
    }

    public static List<IVocaFullPlayTTSItem> convertToVocaFullPlayerTtsItemList(List<Object> list) {
        return list.stream()
                .filter(e -> e instanceof IVocaFullPlayTTSItem)
                .map(e -> (IVocaFullPlayTTSItem) e)
                .collect(Collectors.toList());
    }

    public static int getCountOfKnownInList(List<? extends IVocaBasicItem> totalItems) {
        return (int) totalItems.stream()
                .filter(BaseVocaKnow::isKnown)
                .count();
    }

    public static int getCountOfUnknownInList(List<? extends IVocaBasicItem> totalItems) {
        return (int) totalItems.stream()
                .filter(BaseVocaKnow::isUnknownAndLess)
                .count();
    }


    public static <T extends IVocaBasicItem> List<T> getRandomUnknown(List<T> totalItems, int count) {
        return totalItems.stream()
                .skip((long) (totalItems.size() * Math.random()))
                .limit(count)
                .collect(Collectors.toList());
    }
    public static VocaTypeIdListWithComma getVocaTypeAndIDListWithComma(List<? extends IVocaBasicItem> vocaList) {
        StringJoiner sjVocaType = new StringJoiner(",");
        StringJoiner sjVocaID = new StringJoiner(",");
        for(IVocaBasicItem voca : vocaList) {
            sjVocaType.add(String.valueOf(voca.getVIVocaType()));
            sjVocaID.add(String.valueOf(voca.getVIVocaId()));
        }

        return new VocaTypeIdListWithComma(sjVocaType.toString(), sjVocaID.toString());
    }

    public static String getVocaListInStringWithEnter(List<? extends IVocaBasicItem> list) {
        if (list == null) {
            return "";
        }
        return getVocaListInStringCore(list, "\t");
    }
    private static String getVocaListInStringCore(List<? extends IVocaBasicItem> list, String delimiter) {
        if (list == null) {
            return "";
        }
        return list.stream().map(e -> e.getVIVoca()).collect(Collectors.joining(delimiter));
    }
    public static String getVocaKnowChangedText(List<? extends IVocaBasicItem> listBefore, List<? extends IVocaBasicItem> listAfter) {
        StringJoiner result = new StringJoiner("\n");
        for (int i = 0; i < listBefore.size(); i++) {
            IVocaBasicItem originalItem = listBefore.get(i);
            IVocaBasicItem updatedItem = listAfter.get(i);
            int originalItemId = originalItem.getVIId();
            int updatedItemId = updatedItem.getVIId();
            int originalItemVocaKnow = originalItem.getVIVocaKnow();
            int updatedItemVocaKnow = updatedItem.getVIVocaKnow();
            if (originalItem.getVIId().equals(updatedItem.getVIId()) && originalItem.getVIVocaKnow() != updatedItem.getVIVocaKnow()) {
                int id = originalItem.getVIId();
                int oldVocaKnow = originalItem.getVIVocaKnow();
                int newVocaKnow = updatedItem.getVIVocaKnow();
                System.out.println("Item with id " + id + " changed from " + oldVocaKnow + " to " + newVocaKnow);
                result.add(originalItem.getVIVoca());
            }
        }
        return result.toString().trim();
    }

    public static String getAllVocasAsString(List<? extends IVocaBasicItem> list) {
        return list.stream()
                .map(e -> e.getVIVoca())
                .collect(Collectors.joining("\n"));
    }
    public static String getAllVocasMeaningAsString(List<? extends IVocaBasicItem> list, EnumLanguage language) {
        return list.stream()
                .map(e -> e.getVIVoca() + "\t" + e.getVIMeaning(language))
                .collect(Collectors.joining("\n"));
    }

    public static String getUnknownVocasAsString(List<? extends IVocaBasicItem> list) {
        return list.stream()
                .filter(e -> BaseVocaKnow.isUnknownAndLess(e.getVIVocaKnow()))
                .map(e -> e.getVIVoca())
                .collect(Collectors.joining("\n"));
    }

    public static String getUnknownVocasMeaningAsString(List<? extends IVocaBasicItem> list, EnumLanguage language) {
        return list.stream()
                .filter(e -> BaseVocaKnow.isUnknownAndLess(e.getVIVocaKnow()))
                .map(e -> e.getVIVoca() + "\t" + e.getVIMeaning(language))
                .collect(Collectors.joining("\n"));
    }

    public static String getKnownVocasAsString(List<? extends IVocaBasicItem> list) {
        return list.stream()
                .filter(e -> BaseVocaKnow.isKnown(e.getVIVocaKnow()))
                .map(e -> e.getVIVoca())
                .collect(Collectors.joining("\n"));
    }

    public static <T extends IVocaBasicItem> List<T> getDifficultVocaListFromList(List<T> list) {
        return list.stream()
                .filter(BaseVocaKnow::isUnknownAndLess)
                .collect(Collectors.toList());
    }
//    //예전 이름 getAllWordListFromSentence
//    public static List<IVocaFullPlayTTSItem> getAllWordListOfFromDB(String sentence, SubDatabase subDatabase) {
//        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(sentence);
//        return subDatabase.getAllWordListWordListWithComma(wordListWithComma);
//    }
//
//    public static List<IVocaFullPlayTTSItem> getAllWordListOfFromDB(List<? extends IVocaBasicItem> item, SubDatabase subDatabase) {
//        String wordListWithComma = StringUtils.splitSentenceIntoWordListWithComma(getVocaStringList(item).toString());
//        return subDatabase.getAllWordListWordListWithComma(wordListWithComma);
//    }

    public static List<String> getVocaStringList(List<? extends IVocaBasicItem> list) {
        return list.stream()
                .map(e -> e.getVIVoca())
                .collect(Collectors.toList());
    }

    public static void updateCheckedInVocaList(List<? extends IVocaFullPlayTTSItem> list, boolean toBeChecked) {
        list.forEach(e -> e.setVIChecked(toBeChecked));
    }

    public static List<IVocaFullPlayTTSItem> getVocaListByChecked(List<IVocaFullPlayTTSItem> list) {
        return list.stream()
                .filter(e -> e.isVIChecked())
                .collect(Collectors.toList());
    }

    public static List<IVocaFullPlayTTSItem> getVocaList(WordListType wordListType, int bookId, SubDatabase subDatabase, List<VocaTypeId> vocaTypeIdList) {
        List<IVocaFullPlayTTSItem> vocaList = new ArrayList<>();
        if (wordListType.equals(WordListType.BOOK)) {
            if (bookId > 0) {
                vocaList = subDatabase.getVocaListByBookIdInVocaBook(bookId);
            }
        } else if (wordListType.equals(WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)) {
            if (bookId > 0) {
                vocaList = subDatabase.getVocaListByBookId(bookId);
            } else if (Utils.isNotEmpty(vocaTypeIdList)) {
                vocaList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaTypeIdList);
            }
        } else if (wordListType.equals(WordListType.VOCA_TYPE_ID_LIST)) {
            vocaList = subDatabase.getPlayTTSVocaListByVocaIdList(vocaTypeIdList);
        } else if (wordListType.equals(WordListType.BOOKMARK)) {
            vocaList = subDatabase.getVocaBookmarkList();
        } else if (wordListType.equals(WordListType.USER_VOCA_BOOK_LOCAL)) {
            if (bookId > 0) {
                vocaList = subDatabase.getVocaListByBookIdInUserVocaBookLocal(bookId);
            }
        }
        BaseVoca.resetVocaList(vocaList);
        return vocaList;
    }
}
