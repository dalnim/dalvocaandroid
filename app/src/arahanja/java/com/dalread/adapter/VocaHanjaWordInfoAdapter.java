package com.dalread.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.FuriganaView;
import com.dalread.databinding.HeaderSimpleBinding;
import com.dalread.databinding.ItemHanjaWordInfoGeneralBinding;
import com.dalread.databinding.ItemVocaHanjaBinding;
import com.dalread.databinding.ItemVocaHanjaExampleBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.DIC_HANJA;
import com.dalread.model.DIC_HANJA_SENTENCE;
import com.dalread.model.DIC_VOCA_GROUP_CONFUSED;
import com.dalread.model.HanjaGroupType;
import com.dalread.model.HanjaGroupTypeModel;
import com.dalread.model.HanjaItem;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.HanjaVoca;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

@SuppressLint("NonConstantResourceId")
public class VocaHanjaWordInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GENERAL = 0;
    private static final int TYPE_HEADER = TYPE_GENERAL + 1;
    private static final int TYPE_RADICAL = TYPE_HEADER + 1;
    private static final int TYPE_STROKES = TYPE_RADICAL + 1;
    private static final int TYPE_HANJA_GROUP_TYPE = TYPE_STROKES + 1;
    private static final int TYPE_DECOMPOSITION = TYPE_HANJA_GROUP_TYPE + 1;
    private static final int TYPE_CJK = TYPE_DECOMPOSITION + 1;
    private static final int TYPE_CONFUSED = TYPE_CJK + 1;
    private static final int TYPE_EXAMPLE = TYPE_CONFUSED + 1;
    private FuriganaView.OnTextSelectedListener rubyWordClickListener;
//    private enum HANJA_GROUP_TYPE {LEVEL, LEVEL_KOREA_TYPE_1, LEVEL_KOREA_TYPE_2, LEVEL_KOREA_TYPE_3, COMMON_USE_KOREA, COMMON_USE_JAPAN};
    private final List<Object> data = new ArrayList<>();
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private OnDoubleClickListener onDoubleClickListenerForCommon;
    private OnDoubleClickListener hanjaGroupTypeListener;
    private Activity activity;
    private AbstractPointUtil pointUtil;
    private boolean firsAddComponent;
//    private String radicalPronounce;
//    private String radicalMeaning;


    public VocaHanjaWordInfoAdapter(Activity activity, AbstractPointUtil pointUtil, FuriganaView.OnTextSelectedListener rubyWordClickListener, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, OnDoubleClickListener onDoubleClickListenerForCommon, OnDoubleClickListener hanjaGroupTypeListener) {
        this.activity = activity;
        this.pointUtil = pointUtil;
        this.rubyWordClickListener = rubyWordClickListener;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListenerForCommon = onDoubleClickListenerForCommon;
        this.hanjaGroupTypeListener = hanjaGroupTypeListener;

    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof Integer)
            return TYPE_HEADER;
        if (data.get(position) instanceof RadicalModel)
            return TYPE_RADICAL;
        if (data.get(position) instanceof StrokesModel)
            return TYPE_STROKES;
        if (data.get(position) instanceof HanjaGroupTypeModel)
            return TYPE_HANJA_GROUP_TYPE;
        if (data.get(position) instanceof DecompositionModel)
            return TYPE_DECOMPOSITION;
//        if (data.get(position) instanceof Pair)
//            return TYPE_CJK;
        if (data.get(position) instanceof CJKModel)
            return TYPE_CJK;
        if (data.get(position) instanceof ConfusedModel)
            return TYPE_CONFUSED;
        if (data.get(position) instanceof DIC_HANJA_SENTENCE)
            return TYPE_EXAMPLE;
        return TYPE_GENERAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(HeaderSimpleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_DECOMPOSITION) {
            return new DecompositionHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_CJK) {
            return new CJKHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_CONFUSED) {
            return new ConfusedHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_EXAMPLE) {
            return new ExampleHolder(ItemVocaHanjaExampleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_RADICAL) {
            return new RadicalHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_STROKES) {
            return new StrokesHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == TYPE_HANJA_GROUP_TYPE) {
            return new HanjaGroupTypeHolder(ItemVocaHanjaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }
        return new GeneralHolder(ItemHanjaWordInfoGeneralBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GeneralHolder) {
            ((GeneralHolder) holder).bind((DIC_HANJA) data.get(position));
        } else if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind((int) data.get(position));
        } else if (holder instanceof DecompositionHolder) {
            ((DecompositionHolder) holder).bind((DecompositionModel) data.get(position));
        } else if (holder instanceof CJKHolder) {
//            ((CJKHolder) holder).bind((Pair<String, Integer>) data.get(position));
            ((CJKHolder) holder).bind((CJKModel) data.get(position));
        } else if (holder instanceof ConfusedHolder) {
            ((ConfusedHolder) holder).bind((ConfusedModel) data.get(position));
        } else if (holder instanceof ExampleHolder) {
            ((ExampleHolder) holder).bind((DIC_HANJA_SENTENCE) data.get(position));
        } else if (holder instanceof RadicalHolder) {
            ((RadicalHolder) holder).bind((RadicalModel) data.get(position));
        } else if (holder instanceof StrokesHolder) {
            ((StrokesHolder) holder).bind((StrokesModel) data.get(position));
        } else if (holder instanceof HanjaGroupTypeHolder) {
            ((HanjaGroupTypeHolder) holder).bind((HanjaGroupTypeModel) data.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

//    public void setOnRubyWordClickListener(FuriganaView.OnTextSelectedListener rubyWordClickListener) {
//        this.rubyWordClickListener = rubyWordClickListener;
//    }

    private void setRadicalData(DIC_HANJA hanja) {
        String radical = hanja.getRADICAL();
        if (!TextUtils.isEmpty(radical)) {
            BaseVoca.executeRealmTransaction(realm -> {
                DIC_HANJA radicalHanja = realm.where(DIC_HANJA.class)
                        .equalTo(Constant.REALMDB.KEY_VOCA, radical)
                        .findFirst();
                Voca.searchAlternativeMeaningPronounceFirst(realm, radicalHanja);
                if (radicalHanja != null) {
                    data.add(new RadicalModel(realm.copyFromRealm(radicalHanja)));
                }
            });
        }
    }

    private void setStrokeData(DIC_HANJA hanja) {
        Long strokes = hanja.getSTROKES();
        if (strokes > 0) {
//            data.add(R.string.hanja_info_divide_title_strokes);
            data.add(new StrokesModel(hanja, strokes));
        }
    }

    private void setHanjaGroupTypeData(DIC_HANJA hanja) {
        data.add(R.string.hanja_info_divide_title_group_type);
        boolean hasData = false;
        Long level = hanja.getVOCA_LEVEL();
        if (level >= Constant.VOCA_LEVEL_INDIC_MIN && level < Constant.VOCA_LEVEL_INDIC_MAX) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.VOCA_LEVEL));
        }

        if (!Utils.isEmpty(hanja.getLEVEL_KOREA_TYPE_1OrEmptyString())) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.LEVEL_KOREA_TYPE_1));
        }

        if (!Utils.isEmpty(hanja.getLEVEL_KOREA_TYPE_2OrEmptyString())) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.LEVEL_KOREA_TYPE_2));
        }


        if (!Utils.isEmpty(hanja.getLEVEL_KOREA_TYPE_3OrEmptyString())) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.LEVEL_KOREA_TYPE_3));
        }

        if (hanja.getCOMMON_USE_KOREA().equals(Constant.HANJA.COMMON_USE.commonMiddleSchool)) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.COMMON_MIDDLE_SCHOOL));
        } else if (hanja.getCOMMON_USE_KOREA().equals(Constant.HANJA.COMMON_USE.commonHighSchool)) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.COMMON_HIGH_SCHOOL));
        }

        if (!Utils.isEmpty(hanja.getCOMMON_USE_JAPANOrEmptyString())) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.COMMON_USE_JAPAN));
        }

        if (!Utils.isEmpty(hanja.getLEVEL_HSK())) {
            hasData = true;
            data.add(new HanjaGroupTypeModel(hanja, HanjaGroupType.COMMON_HSK));
        }

        if (!hasData) {
            data.remove(data.size() - 1);
        }

    }

    private void setDecompositionData(Realm realm, DIC_HANJA hanja, int level) {
        String leftComponent = hanja.getLEFTCOMPONENT();
        String rightComponent = hanja.getRIGHTCOMPONENT();
        if (isValidComponent(hanja, leftComponent, rightComponent)) {
            String[] leftComponents = leftComponent.split("(?!^)");
            String[] rightComponents = rightComponent.split("(?!^)");
            String[] components = new String[leftComponents.length + rightComponents.length];
            System.arraycopy(leftComponents, 0, components, 0, leftComponents.length);
            System.arraycopy(rightComponents, 0, components, leftComponents.length, rightComponents.length);
            RealmResults<DIC_HANJA> hanjaResults = realm.where(DIC_HANJA.class)
                    .in(Constant.REALMDB.KEY_VOCA, components)
                    .findAll();
            Map<String, DIC_HANJA> hanjaMap = new HashMap<>();
            DIC_HANJA dicHanja;
            for (DIC_HANJA dh : hanjaResults) {
                dicHanja = realm.copyFromRealm(dh);
                hanjaMap.put(dicHanja.getVOCA(), dicHanja);
            }
            for (String key : components) {
                dicHanja = hanjaMap.get(key);
                if (dicHanja != null) {
                    Voca.searchAlternativeMeaningPronounceFirst(realm, dicHanja);
                    if (firsAddComponent) {
                        data.add(R.string.hanja_info_divide_title_decomposition);
                        firsAddComponent = false;
                    }
                    data.add(new DecompositionModel(dicHanja, level));
                    setDecompositionData(realm, dicHanja, level + 1);
                }
            }
        }
    }

    private boolean isValidComponent(DIC_HANJA hanja, String leftComponent, String rightComponent) {
        boolean result = true;
        if ((TextUtils.isEmpty(leftComponent)) && (TextUtils.isEmpty(leftComponent))) {
            result = false;
        } else {
            String leftRightComponentCombined = leftComponent + rightComponent;
            if (hanja.getVOCA().equals(leftRightComponentCombined))
                result = false;
        }
        return result;

//        return !TextUtils.isEmpty(leftComponent) && !TextUtils.isEmpty(rightComponent)
//                && !leftComponent.equals("*") && !rightComponent.equals("*")
//                && !leftComponent.equals(hanja.getVOCA()) && !rightComponent.equals(hanja.getVOCA());
    }

    private void setCJKData(DIC_HANJA hanja) {
        String hanjaTraditionalChinese = hanja.getVOCAORI();
        String hanjaKorea = hanja.getHANJA_KOREA();
        String hanjaTaiwan = hanja.getHANJA_TAIWAN();
        String hanjaJapan = hanja.getHANJA_JAPAN();
        String hanjaSimplifiedChinese = hanja.getHANJA_SIMPLIFIED();
        String hanjaShortForm = hanja.getHANJA_SHORT_FORM();
        String hanjaVariant1 = hanja.getHANJA_VARIANT_1();
        String hanjaVariant2 = hanja.getHANJA_VARIANT_2();
        String hanjaSokja = hanja.getHANJA_SOKJA();

        String hanjaTraditionalChineseTitle = activity.getString(R.string.traditional_chinese);
        String hanjaKoreaTitle = activity.getString(R.string.hanja_korea);
        String hanjaTaiwanTitle = activity.getString(R.string.hanja_taiwan);
        String hanjaJapanTitle = activity.getString(R.string.hanja_japan);
        String hanjaSimplifiedChineseTitle = activity.getString(R.string.simplified_chinese);
        String hanjaShortFormTitle = activity.getString(R.string.hanja_short_form);
        String hanjaVariant1Title = activity.getString(R.string.hanja_variant_1);
        String hanjaVariant2Title = activity.getString(R.string.hanja_variant_2);
        String hanjaSokjaTitle = activity.getString(R.string.hanja_sokja);

        LinkedHashMap<String, String> mapHanjaCJK = new LinkedHashMap<>();
        mapHanjaCJK.put(hanjaTraditionalChinese, hanjaTraditionalChineseTitle);
        if (mapHanjaCJK.containsKey(hanjaKorea)) {
            mapHanjaCJK.put(hanjaKorea, mapHanjaCJK.get(hanjaKorea) + ", " + hanjaKoreaTitle);
        } else {
            mapHanjaCJK.put(hanjaKorea, hanjaKoreaTitle);
        }
        if (mapHanjaCJK.containsKey(hanjaJapan)) {
            mapHanjaCJK.put(hanjaJapan, mapHanjaCJK.get(hanjaJapan) + ", " + hanjaJapanTitle);
        } else {
            mapHanjaCJK.put(hanjaJapan, hanjaJapanTitle);
        }
        if (mapHanjaCJK.containsKey(hanjaSimplifiedChinese)) {
            mapHanjaCJK.put(hanjaSimplifiedChinese, mapHanjaCJK.get(hanjaSimplifiedChinese) + ", " + hanjaSimplifiedChineseTitle);
        } else {
            mapHanjaCJK.put(hanjaSimplifiedChinese, hanjaSimplifiedChineseTitle);
        }
        if (mapHanjaCJK.containsKey(hanjaTaiwan)) {
            mapHanjaCJK.put(hanjaTaiwan, mapHanjaCJK.get(hanjaTaiwan) + ", " + hanjaTaiwanTitle);
        } else {
            mapHanjaCJK.put(hanjaTaiwan, hanjaTaiwanTitle);
        }
        if (mapHanjaCJK.containsKey(hanjaShortForm)) {
            mapHanjaCJK.put(hanjaShortForm, mapHanjaCJK.get(hanjaShortForm) + ", " + hanjaShortFormTitle);
        } else {
            mapHanjaCJK.put(hanjaShortForm, hanjaShortFormTitle);
        }
        if (mapHanjaCJK.containsKey(hanjaVariant1)) {
            mapHanjaCJK.put(hanjaVariant1, mapHanjaCJK.get(hanjaVariant1) + ", " + hanjaVariant1Title);
        } else {
            mapHanjaCJK.put(hanjaVariant1, hanjaVariant1Title);
        }
        if (mapHanjaCJK.containsKey(hanjaVariant2)) {
            mapHanjaCJK.put(hanjaVariant2, mapHanjaCJK.get(hanjaVariant2) + ", " + hanjaVariant2Title);
        } else {
            mapHanjaCJK.put(hanjaVariant2, hanjaVariant2Title);
        }
        if (mapHanjaCJK.containsKey(hanjaSokja)) {
            mapHanjaCJK.put(hanjaSokja, mapHanjaCJK.get(hanjaSokja) + ", " + hanjaSokjaTitle);
        } else {
            mapHanjaCJK.put(hanjaSokja, hanjaSokjaTitle);
        }

        mapHanjaCJK.remove("");
        if (mapHanjaCJK.size() > 0) {
//            if (hasOneSameHanjaVariant(hanja, mapHanjaCJK))
//                return;

            data.add(R.string.hanja_info_divide_title_variants);
            for (String hanjaInMap : mapHanjaCJK.keySet()) {
                data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaInMap), hanjaInMap, mapHanjaCJK.get(hanjaInMap)));
            }
        }
    }

    private boolean hasOneSameHanjaVariant(DIC_HANJA hanja, LinkedHashMap<String, String> mapHanjaCJK) {
        boolean result = false;
        if (mapHanjaCJK.size() == 1) {
            String hanjaVoca = hanja.getVOCA();
            String hanjaVocaInMap = "";
            for (String hanjaInMap : mapHanjaCJK.keySet()) {
                hanjaVocaInMap = hanjaInMap;
            }
            if (hanjaVoca.equals(hanjaVocaInMap))
                result = true;
        }
        return result;
    }

//    //Don't Delete this one, may we can use it later.
//    private void setCJKDataOld(DIC_HANJA hanja) {
//        int pos = 0;
//        String hanjaVariant = "";
//        if (!TextUtils.isEmpty(hanja.getVOCAORI())) {
//            hanjaVariant = hanja.getVOCAORI();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.traditional_chinese)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_KOREA())) {
//            hanjaVariant = hanja.getHANJA_KOREA();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.hanja_korea)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_TAIWAN())) {
//            hanjaVariant = hanja.getHANJA_TAIWAN();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.hanja_taiwan)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_JAPAN())) {
//            hanjaVariant = hanja.getHANJA_JAPAN();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.chinese_in_japan)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_SIMPLIFIED())) {
//            hanjaVariant = hanja.getHANJA_SIMPLIFIED();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.simplified_chinese)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_SHORT_FORM())) {
//            hanjaVariant = hanja.getHANJA_SHORT_FORM();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.short_form)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_VARIANT_1())) {
//            hanjaVariant = hanja.getHANJA_VARIANT_1();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.hanja_variant_1)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_VARIANT_2())) {
//            hanjaVariant = hanja.getHANJA_VARIANT_2();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.hanja_variant_2)));
//            pos++;
//        }
//        if (!TextUtils.isEmpty(hanja.getHANJA_SOKJA())) {
//            hanjaVariant = hanja.getHANJA_SOKJA();
//            data.add(new CJKModel(Voca.searchHanjaWordByWord(hanjaVariant), hanjaVariant, context.getString(R.string.hanja_sokja)));
//            pos++;
//        }
//        //Don't add cjk_character and hanjaVariant if it's same as voca
//        if ((pos == 1) && (hanja.getVOCA().equals(hanjaVariant))) {
//            data.remove(getItemCount() - 1);
//        } else if (pos > 1) {
//            data.add(getItemCount() - pos, R.string.hanja_info_divide_title_variants);
//        }
//    }

    private void setConfusedData(DIC_HANJA hanja) {
        BaseVoca.executeRealmTransaction(realm -> {
            RealmResults<DIC_VOCA_GROUP_CONFUSED> results1 = realm.where(DIC_VOCA_GROUP_CONFUSED.class)
                    .equalTo(Constant.REALMDB.KEY_VOCA, hanja.getVOCA())
                    .findAll();
            int size1 = results1.size();
            if (size1 > 0) {
                Long[] groupIds = new Long[size1];
                for (int i = 0; i < size1; i++) {
                    groupIds[i] = Objects.requireNonNull(results1.get(i)).getGROUP_ID();
                }
                RealmResults<DIC_VOCA_GROUP_CONFUSED> results2 = realm.where(DIC_VOCA_GROUP_CONFUSED.class)
                        .in(Constant.REALMDB.KEY_GROUP_ID, groupIds)
                        .sort(Constant.REALMDB.KEY_GROUP_ID, Sort.ASCENDING, Constant.REALMDB.KEY_DISP_ORDER, Sort.ASCENDING)
                        .findAll();
                int size2 = results2.size();
                if (size2 > 0) {
                    String[] vocas = new String[size2];
                    for (int i = 0; i < size2; i++) {
                        vocas[i] = Objects.requireNonNull(results2.get(i)).getVOCA();
                    }
                    RealmResults<DIC_HANJA> results = realm.where(DIC_HANJA.class)
                            .in(Constant.REALMDB.KEY_VOCA, vocas)
                            .findAll();
                    if (!results.isEmpty()) {
                        data.add(R.string.hanja_info_divide_title_confused_words);
                        for (DIC_HANJA hanja1 : results) {
                            data.add(new ConfusedModel(realm.copyFromRealm(hanja1)));
                        }
                    }
                }
            }
        });
    }

    private void setExampleData(DIC_HANJA hanja) {
        BaseVoca.executeRealmTransaction(realm -> {
            RealmResults<DIC_HANJA_SENTENCE> resultsWitMeaning = realm.where(DIC_HANJA_SENTENCE.class)
                    .contains(Constant.REALMDB.KEY_VOCA, hanja.getVOCA())
                    .notEqualTo(Constant.REALMDB.KEY_MEANING_KO, "")
                    .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING, Constant.REALMDB.KEY_VOCA_LEVEL, Sort.ASCENDING)
                    .findAll();
            RealmResults<DIC_HANJA_SENTENCE> resultsWithoutMeaning = realm.where(DIC_HANJA_SENTENCE.class)
                    .contains(Constant.REALMDB.KEY_VOCA, hanja.getVOCA())
                    .equalTo(Constant.REALMDB.KEY_MEANING_KO, "")
                    .sort(Constant.REALMDB.KEY_PRONOUNCE, Sort.ASCENDING, Constant.REALMDB.KEY_VOCA_LEVEL, Sort.ASCENDING)
                    .findAll();
            if ((!resultsWitMeaning.isEmpty()) || (!resultsWithoutMeaning.isEmpty())) {
                data.add(R.string.example_sentences);
                data.addAll(realm.copyFromRealm(resultsWitMeaning));
                data.addAll(realm.copyFromRealm(resultsWithoutMeaning));
            }
        });
    }

//    public void setContext(Context context) {
//        this.context = context;
//    }
    public void setData(DIC_HANJA hanja) {
        firsAddComponent = true;
        data.clear();
        data.add(hanja);
        setRadicalData(hanja);
        setStrokeData(hanja);

        BaseVoca.executeRealmTransaction(realm -> setDecompositionData(realm, hanja, 1));
        setHanjaGroupTypeData(hanja);
        setCJKData(hanja);
        setConfusedData(hanja);
        setExampleData(hanja);
    }

//    public void setOnDoubleClickListenerOnBaseVocaKnow(OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow) {
//        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
//    }
//
//    public void setHanjaGroupTypeListener(OnDoubleClickListener listener) {
//        this.hanjaGroupTypeListener = listener;
//    }

    class GeneralHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DIC_HANJA hanja;
        private ItemHanjaWordInfoGeneralBinding binding;

        public GeneralHolder(ItemHanjaWordInfoGeneralBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            binding.tvMeaningDetailedFurigana.setOnTextSelectedListener(rubyWordClickListener);
        }

        private void setOnClickListeners() {
            binding.ivKnow.setOnClickListener(this);
            binding.tvVoca.setOnClickListener(this);
            binding.ivBookmark.setOnClickListener(this);
            binding.ivWebSearch.setOnClickListener(this);
            binding.ivWebDictionary.setOnClickListener(this);
            binding.ivWebDictionaryChS.setOnClickListener(this);
            binding.ivWebDictionaryJp.setOnClickListener(this);
            binding.ivWebDictionaryEn.setOnClickListener(this);
            binding.ivEditView.setOnClickListener(this);
            binding.ivWordList.setOnClickListener(this);
            binding.ivCopy.setOnClickListener(this);
        }

        void bind(DIC_HANJA hanja) {
            this.hanja = hanja;
            // 아라한자에서는 포인트 표시 비활성화
            // int point = pointUtil.getPoint();
            // binding.tvRemainPoint.setText(activity.getResources().getQuantityString(R.plurals.point, point, point));
            binding.tvRemainPoint.setVisibility(View.GONE);
//            binding.tvRemainPoint.setOnClickListener(v -> {
//                activity.startActivity(new Intent(activity, InAppPointListActivity.class));
//            });

            binding.ivKnow.setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, hanja));
            VocaKnow.updateVocaColor(activity, binding.tvVoca, hanja.getVOCA_KNOW().intValue() );
//            if (hanja.getVOCA_KNOW() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                if (hanja.getVOCA_KNOWPRONOUNCE() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//                    binding.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_normal));
//                } else {
//                    binding.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_pronounce));
//                }
//            } else {
//                binding.tvVoca.setTextColor(ContextCompat.getColor(context, R.color.color_ruby_text_unknown_meaning));
//            }
            binding.tvVoca.setText(hanja.getVOCA());
            updateBookmarkIcon(hanja);
            VocaKnow.updateIconVocaKnow(activity, binding.ivKnow, hanja.getVOCA_KNOW().intValue());

            binding.tvMeaningPronounceHanja.setText(hanja.getMEANING_KO());

            if (Utils.isEmpty(hanja.getPINYIN())) {
                binding.tvPinyin.setVisibility(View.GONE);
            } else {
                binding.tvPinyin.setText(String.format(String.format(activity.getString(R.string.tpl_pinyin), hanja.getPINYIN())));
                binding.tvPinyin.setVisibility(View.VISIBLE);
            }
            if (Utils.isEmpty(hanja.getKUNYOMI())) {
                binding.tvKunyomi.setVisibility(View.GONE);
            } else {
                binding.tvKunyomi.setText(String.format(String.format(activity.getString(R.string.tpl_pinyin), hanja.getKUNYOMI())));
                binding.tvKunyomi.setVisibility(View.VISIBLE);
            }
            if (Utils.isEmpty(hanja.getONYOMI())) {
                binding.tvOnyomi.setVisibility(View.GONE);
            } else {
                binding.tvOnyomi.setText(String.format(String.format(activity.getString(R.string.tpl_pinyin), hanja.getONYOMI())));
                binding.tvOnyomi.setVisibility(View.VISIBLE);
            }
            binding.tvPinyin.setText(String.format(String.format(activity.getString(R.string.tpl_pinyin), hanja.getPINYINOrEmptyString())));
            binding.tvKunyomi.setText(String.format(String.format(activity.getString(R.string.tpl_kunyomi), hanja.getKUNYOMIOrEmptyString())));
            binding.tvOnyomi.setText(String.format(String.format(activity.getString(R.string.tpl_onyomi), hanja.getONYOMIOrEmptyString())));

            updateUnicodeHex();
            updateMeaningDetailed();
//            binding.tvMeaningDetailed.setVisibility(Utils.isEmpty(hanja.getMEANING_KO_DETAILED()) == true ? View.GONE : View.VISIBLE );
//            binding.tvMeaningDetailed.setText(hanja.getMEANING_KO_DETAILED());
            binding.tvMeaningEng.setVisibility(Utils.isEmpty(hanja.getMEANING_ENG()) == true ? View.GONE : View.VISIBLE );
            binding.tvMeaningEng.setText(hanja.getMEANING_ENG());
            binding.tvMeaningEngDetailed.setVisibility(Utils.isEmpty(hanja.getMEANING_ENG_DETAILED()) == true ? View.GONE : View.VISIBLE );
            binding.tvMeaningEngDetailed.setText(hanja.getMEANING_ENG_DETAILED());

            binding.ivWordList.setVisibility(Voca.hasHanjaWordItemListFromContent(hanja.getALL_TEXT()) ? View.VISIBLE : View.GONE);
            hideMenusOnReleaseMode();
        }

        private void updateMeaningDetailed() {
            if (Utils.isEmpty(hanja.getMEANING_KO_DETAILED())) {
                binding.tvMeaningDetailedFurigana.setVisibility(View.GONE);
            } else {
                List<String> listRubyTextAndUnknownWord = Voca.getRubyTextAndUnknownWord(hanja.getMEANING_KO_DETAILED(), "");
                binding.tvMeaningDetailedFurigana.setFuriganViewForMeaning(listRubyTextAndUnknownWord.get(0));
//                binding.tvMeaningDetailedFurigana.setTutor(true);
//                binding.tvMeaningDetailedFurigana.resetText();
//                binding.tvMeaningDetailedFurigana.setIsKnownPronounceMeaning(true);
//                binding.tvMeaningDetailedFurigana.setIsShowFurigana(Constant.SHOW_FURIGANA_OFF);
//                binding.tvMeaningDetailedFurigana.setJText(rubyText);
                binding.tvMeaningDetailedFurigana.setVisibility(View.VISIBLE );
            }
        }

        private void updateUnicodeHex() {
            binding.tvUnicodeHex.setText(hanja.getUNICODE_HEX() + " " + HanjaVoca.getUnicodeGroupName(hanja.getUNICODE_DEC()));
        }

        private void updateBookmarkIcon(HanjaItem hanja) {
            binding.ivBookmark.setImageResource(hanja.getHI_BOOKMARK() != null && hanja.getHI_BOOKMARK() == 1
                    ? R.drawable.ic_favorite_new_on
                    : R.drawable.ic_favorite_new_off);
        }

        private void hideMenusOnReleaseMode() {
            if (UserUtil.isEditContentFullOrAdminUser(activity)) {
                binding.ivEditView.setVisibility(View.VISIBLE);
            } else {
                binding.ivEditView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (onDoubleClickListenerOnBaseVocaKnow != null) {
                switch (view.getId()) {
                    case R.id.ivBookmark:
                        if (UserUtil.isLoggedIn(activity, true)) {
//                            hanja.swapBOOKMARK(); //여기서 하지말고 onDoubleClickListenerOnBaseVocaKnow에서 event를 post해서 업데이트하는게 맞을듯.
//                            updateBookmarkIcon(hanja);
                            onDoubleClickListenerOnBaseVocaKnow.onClick(view, hanja);
                        }
                        break;
                    case R.id.ivKnow:
                    case R.id.ivEditView:
                    case R.id.ivWordList:
                    case R.id.ivCopy:
                        onDoubleClickListenerForCommon.onClick(view, hanja);
                        break;
                    case R.id.tvVoca:
                        onDoubleClickListenerForCommon.onClick(view, hanja.getVOCA());
                        break;
                    case R.id.tv_radical:
                        onDoubleClickListenerForCommon.onClick(view, hanja.getRADICAL());
                        break;
//                    case R.id.tv_type_1:
//                        listener.onClick(view, hanja.getLEVEL_KOREA_TYPE_1OrEmptyString());
//                        break;
//                    case R.id.tv_type_2:
//                        listener.onClick(view, hanja.getLEVEL_KOREA_TYPE_2OrEmptyString());
//                        break;
//                    case R.id.tv_type_3:
//                        listener.onClick(view, hanja.getLEVEL_KOREA_TYPE_3OrEmptyString());
//                        break;
                    case R.id.ivWebSearch:
                        Utils.openWebSearchForHanja(activity, hanja.getVOCA());
                        break;
                    case R.id.iv_web_dictionary:
                        Utils.openWebDictionaryForHanja(activity, hanja.getVOCA());
                        break;
                    case R.id.iv_web_dictionary_ch_s:
                        Utils.openWebDictionaryForHanja_ch_s(activity, hanja.getVOCA());
                        break;
                    case R.id.iv_web_dictionary_jp:
                        Utils.openWebDictionaryForHanja_jp(activity, hanja.getVOCA());
                        break;
                    case R.id.iv_web_dictionary_en:
                        Utils.openWebDictionaryForHanja_en(activity, hanja.getMEANING_KO());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder{

        private HeaderSimpleBinding binding;

        HeaderHolder(HeaderSimpleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int text) {
            binding.tvHeader.setText(text);
        }
    }

    class DecompositionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        @BindDimen(R.dimen.voca_hanja_margin_start)
//        int marginStart;

        private DIC_HANJA hanja;
        DecompositionModel decompositionModel;
        private ItemVocaHanjaBinding binding;

        DecompositionHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.icRight.setOnClickListener(this);
            binding.ivShowVocaInGroup.setOnClickListener(this);
        }

        void bind(DecompositionModel decompositionModel) {
            this.decompositionModel = decompositionModel;
            hanja = decompositionModel.hanja;
            binding.ivShowVocaInGroup.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.tvVoca.getLayoutParams();
            layoutParams.setMarginStart((int) (activity.getResources().getDimension(R.dimen.voca_hanja_margin_start) * decompositionModel.level));
            binding.tvVoca.setLayoutParams(layoutParams);

            String text = (decompositionModel.level > 1) ? "⤷" + hanja.getVOCA() : hanja.getVOCA(); //↳➥
            if (!hanja.getVOCA().equals(hanja.getVOCAORI())) {
                text += " (" + hanja.getVOCAORI() + ")";
            }
            binding.tvVoca.setText(text);

            text = ": " + hanja.getMEANING1OrEmptyString() + " " + hanja.getPRONOUNCE1_FIRSTOrEmptyString();
            binding.tvMeaningPronounceHanja.setText(text);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivShowVocaInGroup:
                    decompositionModel.hanjaGroupTypeModel.setIsFindAllHanjasThatContainThisHanja(true);
                    if (hanjaGroupTypeListener != null) {
                        hanjaGroupTypeListener.onClick(view, decompositionModel.hanjaGroupTypeModel);
                    }
                    break;
                case R.id.v_item:
                    if (onDoubleClickListenerForCommon != null) {
                        onDoubleClickListenerForCommon.onClick(view, hanja);
                    }
                    break;
                case R.id.ic_right:
                    // expand / collapse
                    break;
                default:
                    break;
            }
        }
    }

    class RadicalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private DIC_HANJA hanja;
//        private HanjaGroupTypeModel hanjaGroupTypeModel;
        private RadicalModel radicalModel;
        private ItemVocaHanjaBinding binding;

        RadicalHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
        void bind(RadicalModel radicalModel) {
            this.radicalModel = radicalModel;
            binding.ivShowVocaInGroup.setVisibility(View.VISIBLE);
            String title = activity.getString(R.string.hanja_info_divide_title_radical);
            if (!Utils.isEmpty(radicalModel.hanja.getVOCA())) {
                title = title + " : " + radicalModel.hanja.getVOCA();
            }

            String radicalPronounce = radicalModel.hanja.getPRONOUNCE1_FIRSTOrEmptyString();
            String radicalMeaning = radicalModel.hanja.getMEANING1OrEmptyString();
            if (hasRadical()) {
                title = title + " (" + radicalMeaning + " " + radicalPronounce + ")";
                binding.icRight.setVisibility(View.VISIBLE);
            } else {
                title = title + " (" + activity.getString(R.string.error_msg_there_is_no_radical) + ")";
                binding.icRight.setVisibility(View.INVISIBLE);
            }

            binding.tvMeaningPronounceHanja.setText(title);

        }

        private boolean hasRadical() {
            String radicalPronounce = radicalModel.hanja.getPRONOUNCE1_FIRSTOrEmptyString();
            String radicalMeaning = radicalModel.hanja.getMEANING1OrEmptyString();
            if (Utils.isEmpty(radicalMeaning) && Utils.isEmpty(radicalPronounce)) {
                return false;
            } else {
                return true;
            }
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivShowVocaInGroup.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ivShowVocaInGroup) {
                radicalModel.hanjaGroupTypeModel.setIsFindAllHanjasThatContainThisHanja(true);
            } else {
                if (!hasRadical())
                    return;
                radicalModel.hanjaGroupTypeModel.setIsFindAllHanjasThatContainThisHanja(false);
            }

            if (hanjaGroupTypeListener != null) {
                hanjaGroupTypeListener.onClick(view, radicalModel.hanjaGroupTypeModel);
            }
        }
    }

    class StrokesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private StrokesModel strokesModel;
        private ItemVocaHanjaBinding binding;

        StrokesHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
        void bind(StrokesModel strokesModel) {
            this.strokesModel = strokesModel;

            binding.tvMeaningPronounceHanja.setText(activity.getString(R.string.hanja_info_divide_title_strokes) + " : " + strokesModel.strokes.toString());
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (hanjaGroupTypeListener != null) {
                hanjaGroupTypeListener.onClick(view, strokesModel.hanjaGroupTypeModel);
            }
        }
    }

    class HanjaGroupTypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private HanjaGroupTypeModel hanjaGroupTypeModel;
        private ItemVocaHanjaBinding binding;

        HanjaGroupTypeHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
        void bind(HanjaGroupTypeModel hanjaGroupTypeModel) {
            this.hanjaGroupTypeModel = hanjaGroupTypeModel;

//            updateIcRightVisibility();
            String text = "";
            if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.VOCA_LEVEL) {
                text = String.format(activity.getString(R.string.tpl_level), hanjaGroupTypeModel.getHanja().getVOCA_LEVEL());
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_1) {
                text = String.format(activity.getString(R.string.tpl_type_1), hanjaGroupTypeModel.getHanja().getLEVEL_KOREA_TYPE_1OrEmptyString());
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_2) {
                text = String.format(activity.getString(R.string.tpl_type_2), hanjaGroupTypeModel.getHanja().getLEVEL_KOREA_TYPE_2OrEmptyString());
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.LEVEL_KOREA_TYPE_3) {
                text = String.format(activity.getString(R.string.tpl_type_3), hanjaGroupTypeModel.getHanja().getLEVEL_KOREA_TYPE_3OrEmptyString());
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_MIDDLE_SCHOOL) {
                text = String.format(activity.getString(R.string.tpl_common_use_korea), activity.getString(R.string.menu_common_hanja_middle_school_in_information_view));
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HIGH_SCHOOL) {
                text = String.format(activity.getString(R.string.tpl_common_use_korea), activity.getString(R.string.menu_common_hanja_high_school_in_information_view));
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN) {
                text = String.format(activity.getString(R.string.tpl_common_use_japan), hanjaGroupTypeModel.getHanja().getCOMMON_USE_JAPAN());
            } else if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_HSK) {
                text = String.format(activity.getString(R.string.tpl_common_use_china), hanjaGroupTypeModel.getHanja().getLEVEL_HSK());
            }
            binding.tvMeaningPronounceHanja.setText(text);
        }

        private void updateIcRightVisibility() {
            if (hanjaGroupTypeModel.getHanjaGroupType() == HanjaGroupType.COMMON_USE_JAPAN) {
                binding.icRight.setVisibility(View.INVISIBLE);
            } else {
                binding.icRight.setVisibility(View.VISIBLE);
            }
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (hanjaGroupTypeListener != null) {
                hanjaGroupTypeListener.onClick(view, hanjaGroupTypeModel);
            }
        }
    }

    class CJKHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private String voca;
        private CJKModel cjkModel;
        private ItemVocaHanjaBinding binding;

        CJKHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
//        void bind(Pair<String, Integer> pair) {
        void bind(CJKModel cjkModel) {
            this.cjkModel = cjkModel;
            binding.ivShowVocaInGroup.setVisibility(View.VISIBLE);

            binding.tvVoca.setText(cjkModel.voca);
            binding.tvMeaningPronounceHanja.setText(cjkModel.cjkTitle);
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivShowVocaInGroup.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ivShowVocaInGroup) {
                if (hanjaGroupTypeListener != null) {
                    cjkModel.hanjaGroupTypeModel.setIsFindAllHanjasThatContainThisHanja(true);
                    hanjaGroupTypeListener.onClick(view, cjkModel.hanjaGroupTypeModel);
                }
            } else {
                if (onDoubleClickListenerForCommon != null) {
                    onDoubleClickListenerForCommon.onClick(view, cjkModel.voca);
                }
            }

//            if (listener != null) {
//                listener.onClick(view, voca);
//            }
        }
    }

    class ConfusedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DIC_HANJA hanja;
        ConfusedModel confusedModel;
        private ItemVocaHanjaBinding binding;

        ConfusedHolder(ItemVocaHanjaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        void bind(ConfusedModel confusedModel) {
            this.confusedModel = confusedModel;
            hanja = confusedModel.hanja;
            binding.ivShowVocaInGroup.setVisibility(View.VISIBLE);

            String text = hanja.getVOCA();
            if (!hanja.getVOCA().equals(hanja.getVOCAORI())) {
                text += " (" + hanja.getVOCAORI() + ")";
            }
            binding.tvVoca.setText(text);

            text = ": " + hanja.getMEANING1OrEmptyString() + " " + hanja.getPRONOUNCE1_FIRSTOrEmptyString();
            binding.tvMeaningPronounceHanja.setText(text);
        }

        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
            binding.ivShowVocaInGroup.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ivShowVocaInGroup) {
                if (hanjaGroupTypeListener != null) {
                    confusedModel.hanjaGroupTypeModel.setIsFindAllHanjasThatContainThisHanja(true);
                    hanjaGroupTypeListener.onClick(view, confusedModel.hanjaGroupTypeModel);
                }
            } else {
                if (onDoubleClickListenerForCommon != null) {
                    onDoubleClickListenerForCommon.onClick(view, hanja);
                }
            }
        }
    }

    class ExampleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DIC_HANJA_SENTENCE hanjaSentence;
        private ItemVocaHanjaExampleBinding binding;

        ExampleHolder(ItemVocaHanjaExampleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }
        private void setOnClickListeners() {
            binding.vItem.setOnClickListener(this);
        }

        void bind(DIC_HANJA_SENTENCE hanjaSentence) {
            this.hanjaSentence = hanjaSentence;

            String voca = hanjaSentence.getVOCA();
            binding.tvVoca.setText(voca);

            String pronounce = hanjaSentence.getPRONOUNCE();
            if (TextUtils.isEmpty(pronounce)) {
                binding.tvPronounceShort.setVisibility(View.GONE);
                binding.tvPronounceLong.setVisibility(View.GONE);
            } else {
                pronounce = "[ " + pronounce + " ]";
                if (voca.length() <= Constant.MAX_VOCA_COUNT_TO_USE_WORD_HOLDER) {
                    binding.tvPronounceShort.setText(pronounce);
                    binding.tvPronounceShort.setVisibility(View.VISIBLE);
                    binding.tvPronounceLong.setVisibility(View.GONE);
                } else {
                    binding.tvPronounceLong.setText(pronounce);
                    binding.tvPronounceLong.setVisibility(View.VISIBLE);
                    binding.tvPronounceShort.setVisibility(View.GONE);
                }
            }

            String meaning = hanjaSentence.getMEANING_KO();
            if (TextUtils.isEmpty(meaning)) {
                binding.tvMeaning.setVisibility(View.GONE);
            } else {
                binding.tvMeaning.setText(meaning);
                binding.tvMeaning.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            if (onDoubleClickListenerForCommon != null) {
                onDoubleClickListenerForCommon.onClick(view, hanjaSentence);
            }
        }
    }

    static class DecompositionModel {

        private final DIC_HANJA hanja;
        private final int level;
        private final HanjaGroupTypeModel hanjaGroupTypeModel;
        public DecompositionModel(DIC_HANJA hanja, int level) {
            this.hanja = hanja;
            this.level = level;
            hanjaGroupTypeModel = new HanjaGroupTypeModel(hanja, HanjaGroupType.DECOMPOSITION);
        }
    }

    static class CJKModel {
        private final DIC_HANJA hanja;
        private final HanjaGroupTypeModel hanjaGroupTypeModel;
        private String voca;
        private String cjkTitle;
        public CJKModel(DIC_HANJA hanja, String voca, String cjkTitle) {
            this.hanja = hanja;
            this.voca = voca;
            this.cjkTitle = cjkTitle;
            hanjaGroupTypeModel = new HanjaGroupTypeModel(hanja, HanjaGroupType.CJK);
        }
    }


    static class ConfusedModel {
        private final DIC_HANJA hanja;
        private final HanjaGroupTypeModel hanjaGroupTypeModel;
        public ConfusedModel(DIC_HANJA hanja) {
            this.hanja = hanja;
            hanjaGroupTypeModel = new HanjaGroupTypeModel(hanja, HanjaGroupType.CONFUSED);
        }
    }

    static class RadicalModel {
        private final DIC_HANJA hanja;
        private final HanjaGroupTypeModel hanjaGroupTypeModel;
        public RadicalModel(DIC_HANJA hanja) {
            this.hanja = hanja;
            hanjaGroupTypeModel = new HanjaGroupTypeModel(hanja, HanjaGroupType.RADICAL);
        }
    }

    static class StrokesModel {
        private final DIC_HANJA hanja;
        private final HanjaGroupTypeModel hanjaGroupTypeModel;
        private final Long strokes;
        public StrokesModel(DIC_HANJA hanja, Long strokes) {
            this.hanja = hanja;
            this.strokes = strokes;
            hanjaGroupTypeModel = new HanjaGroupTypeModel(hanja, HanjaGroupType.STROKES);
        }
    }
}
