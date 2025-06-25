package com.dalread.util;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.SubModel;
import com.dalread.model.VocaKnowMeaning;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MergeUtil {

    public static void generateMeaning(List<DicModel> dicModels) {
        generateMeaning(dicModels, null);
    }

    public static void generateMeaning(List<DicModel> dicModels, List<RubyTextModel> rubyTextModels) {
        int count = 1;
        for (DicModel dic : dicModels) {
            dic.setIndex(count);
            dic.setPosition(count - 1);
            count++;
            generateMeaning(dic, rubyTextModels);
        }
    }

    private static boolean isShowPronounceBeforeMeaning (int studyLang) {
        if (EnumLanguage.findByIdApi(studyLang) == EnumLanguage.ENGLISH) {
            return false;
        }
        return true;
    }
    public static void generateMeaning(DicModel dicModel, List<RubyTextModel> rubyTextModels) {
        String meaning = Constant.BASE_BLANK;
        boolean showPronounceBeforeMeaning = isShowPronounceBeforeMeaning(dicModel.getLangStudy());
        StringBuilder ids = new StringBuilder();
        StringBuilder types = new StringBuilder();
        StringBuilder wordIds = new StringBuilder();
        Document doc = Jsoup.parse(dicModel.getVocaDisplayRuby());
        Elements spans = doc.select(Constant.RUBY.KEY.SPAN);
        List<Integer> listIdMeaning = new ArrayList<>();
        List<Integer> listIds = new ArrayList<>();
        HashMap<Integer, RubyTextModel> mapRubyDiffultWordToDisplay = new HashMap<>();
        HashMap<Integer, RubyTextModel> mapRubyAllWord = new HashMap<>();
        for (Element e : spans) {
            int id = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ID));
            int type = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_TYPE));
            if (!listIds.contains(id)) {
                ids.append(",").append(id);
                types.append(",").append(type);
                listIds.add(id);
            }

            if (rubyTextModels != null) {
                for (RubyTextModel rubyModel : rubyTextModels) {
                    if (rubyModel.getVocaId() == id) {
                        mapRubyAllWord.put(id, rubyModel);
                        String display = rubyModel.getVoca();
//                        String meaningTemp = rubyModel.getMeaning();
                        String meaningTemp = getEnglishMeaningIfEmptyMeaning(rubyModel);
                        //
                        if (isAddDifficultWordToDisplay(dicModel.getLangStudy(), rubyModel)) {
                            if (!listIdMeaning.contains(id)) {
                                if (!Utils.isEmpty(meaning)) {
                                    meaning += ", ";
                                }
                                if (showPronounceBeforeMeaning) {
                                    if (Utils.isEmpty(rubyModel.getPronounce())) {
                                        if (Utils.isEmpty(meaningTemp)) {
                                            meaning += display;
                                        } else {
                                            meaning += display + "(" + meaningTemp + ")";
                                        }
                                    } else{
                                        if (Utils.isEmpty(meaningTemp)) {
                                            meaning += display + "(" + rubyModel.getPronounce() + ")";
                                        } else {
                                            meaning += display + "(" + rubyModel.getPronounce() + "," + meaningTemp + ")";
                                        }
                                    }
                                } else {
                                    meaning += display + "(" + meaningTemp + ")";
                                }
                                wordIds.append(",").append(id);
                                listIdMeaning.add(id);
                                mapRubyDiffultWordToDisplay.put(id, rubyModel);
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (ids.length() > 0) {
            dicModel.setRubyIds(ids.substring(1));
        }
        if (types.length() > 0) {
            dicModel.setRubyTypes(types.substring(1));
        }
        if (wordIds.length() > 0) {
            dicModel.setWordIds(wordIds.substring(1));
        }

        if (!mapRubyAllWord.isEmpty()) {
            dicModel.setListRubyTextModel(mapRubyAllWord);
        }
//        if (!mapRubyDiffultWord.isEmpty()) {
//            dicModel.setListRubyTextModel(mapRubyDiffultWord);
//        }
        dicModel.setDifficultWordsCount(listIdMeaning.size());
        dicModel.setMeaningWords(meaning);
    }

    private static String getEnglishMeaningIfEmptyMeaning(RubyTextModel rubyModel) {
        String strMeaning = "";
        if (Utils.isEmpty(rubyModel.getMeaning())) {
            strMeaning = getEnglishMeaningIfExists(rubyModel, strMeaning);
            strMeaning = getJmdtMeaningForJapaneseIfMeaningIsNotExist(rubyModel, strMeaning);
        } else {
            strMeaning = rubyModel.getMeaning();
        }
        return strMeaning;
    }

    private static String getEnglishMeaningIfExists(RubyTextModel rubyModel, String strMeaning) {
        if (!Utils.isEmpty(rubyModel.getMeaningEng())) {
            strMeaning = StringUtils.cutMeaningIfTooLongAboveSubtitle(rubyModel.getMeaningEng());
        }
        return strMeaning;
    }

    private static String getJmdtMeaningForJapaneseIfMeaningIsNotExist(RubyTextModel rubyModel, String strMeaning) {
//        if (studyLang == EnumLanguage.JAPANESE.getIdApi() && Utils.isEmpty(strMeaning)) {
            if (!Utils.isEmpty(rubyModel.getJmdictMeaning())) {
                strMeaning = StringUtils.cutMeaningIfTooLongAboveSubtitle(rubyModel.getJmdictMeaning());
            } else if (!Utils.isEmpty(rubyModel.getJmdictMeaningEng())) {
                strMeaning = StringUtils.cutMeaningIfTooLongAboveSubtitle(rubyModel.getJmdictMeaningEng());
            }
//        }
        return strMeaning;
    }

    //Don't add if it shows only word (Need to show meaning or prounce(JP/CN case))
    private static boolean isAddDifficultWordToDisplay(int langStudy, RubyTextModel rubyModel) {
//        return rubyModel.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && !Utils.isEmpty(tmpMeaning);
        if (EnumLanguage.findByIdApi(langStudy) == EnumLanguage.ENGLISH) {
            return rubyModel.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && !Utils.isEmpty(rubyModel.getMeaning());
        } else {
            if ((rubyModel.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    && ((!Utils.isEmpty(rubyModel.getPronounce())) || (!Utils.isEmpty(rubyModel.getMeaning())))) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static List<VocaKnowMeaning> generateStudyWritingModel(String wordDisplay, String dictationWordRuby) {
        final List<VocaKnowMeaning> data = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(dictationWordRuby);
            Elements spans = doc.select(Constant.RUBY.KEY.SPAN);
            for (Element e : spans) {
                int vocaType = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_TYPE));
                int vocaId = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ID));
                String voca = Constant.BASE_BLANK;
                String meaning = Constant.BASE_BLANK;
                for (Node childNodes2 : e.childNodes()){
                    if (childNodes2 instanceof Element) {
                        if (childNodes2.childNodes().size() > 1) {
                            voca = StringUtils.removeLineBreaks(childNodes2.childNode(0).childNode(0).toString());
                            meaning = StringUtils.removeLineBreaks(childNodes2.childNode(1).childNode(0).toString());
                        } else if (childNodes2.childNodes().size() == 1) {
                            voca = StringUtils.removeLineBreaks(childNodes2.childNode(0).childNode(0).toString());
                        }

                        break;
                    }
                }
                //Dalnim : will update VocaKnow data from DIC table af instead of e.hasAttr (We don't have KNOW value in the attr anymore)
                data.add(new VocaKnowMeaning(vocaId, vocaType, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, voca, meaning));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    public static void generateMeaning(SubModel subModel) {
        String meaning = generateMeaning(subModel.getContentRuby());
        subModel.setMeaningWords(meaning);
    }

    public static String generateMeaning(String content) {
        return generateMeaning(content, false);
    }

    public static String generateMeaning(String content, boolean isSort) {
        String meaning = Constant.BASE_BLANK;
        if (Utils.isEmpty(content)) return meaning;
        Document doc = Jsoup.parse(content);
        Elements spans = doc.select(Constant.RUBY.KEY.SPAN);
        List<Integer> listId = new ArrayList<>();
        List<String> listMeaning = new ArrayList<>();
        for (Element e : spans) {
            int id = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_ID));
            String display = e.attr(Constant.RUBY.KEY.VOCA_DISPLAY);
            String tmp = e.attr(Constant.RUBY.KEY.MEANING);
            int know = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            if (e.hasAttr(Constant.RUBY.KEY.VOCA_KNOW)) {
                know = Utils.parseInt(e.attr(Constant.RUBY.KEY.VOCA_KNOW));
            }
            if (know != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && !Utils.isEmpty(tmp)) {
                if (!listId.contains(id)) {
                    listId.add(id);
                    listMeaning.add(display + "(" + tmp + ")");
                }
            }
        }
        if (isSort) {
            Collections.sort(listMeaning, (o1, o2) -> o1.compareTo(o2));
        }
        for (String s : listMeaning) {
            meaning = StringUtils.addString(meaning, s);
        }
        return meaning;
    }
}
