package com.dalread.util;

import android.content.Context;
import android.speech.tts.Voice;

import com.dalread.util.studylang.AbstractStudyLang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TtsVoiceListUtil {
    public static List<Voice> getTotalCountToPlayVoiceOrTts(Context context, int totalCountToPlayVoiceOrTts, Set<Voice> voices, AbstractStudyLang studyLang) {
        if (LanguageUtil.isStudyLangEnglish(context)) {
            return getTotalCountToPlayVoiceOrTtsForEnglish(totalCountToPlayVoiceOrTts, voices, studyLang);
        } else {
            return getTotalCountToPlayVoiceOrTtsForNonEnglish(totalCountToPlayVoiceOrTts, voices, studyLang);
        }
    }

    public static List<Voice> getTotalCountToPlayVoiceOrTtsForNonEnglish(int totalCountToPlayVoiceOrTts, Set<Voice> voices, AbstractStudyLang studyLang) {
        Set<Voice> setVoices = new HashSet<>();
        for (Voice voice : voices) {
            DLog.e("studyLocaleName", voice.getLocale().getDisplayName() + ", " + voice.getLocale().getLanguage());
            if (voice.getLocale() != null
                    && studyLang.getLocaleDisplayLanguage().equals(voice.getLocale().getDisplayLanguage())
                    && !voice.isNetworkConnectionRequired()) {
                setVoices.add(voice);
            }
            int count = setVoices.size();
            if (count == Constant.VOICE_MAX_SIZE || count == totalCountToPlayVoiceOrTts) {
                break;
            }
        }
        return new ArrayList<>(setVoices);
    }

    public static List<Voice> getTotalCountToPlayVoiceOrTtsForEnglish(int totalCountToPlayVoiceOrTts, Set<Voice> voices, AbstractStudyLang studyLang) {
        LinkedHashSet<Voice> usVoices = new LinkedHashSet<>();
        LinkedHashSet<Voice> caVoices = new LinkedHashSet<>();
        LinkedHashSet<Voice> auVoices = new LinkedHashSet<>();
        LinkedHashSet<Voice> nzVoices = new LinkedHashSet<>();
        LinkedHashSet<Voice> gbVoices = new LinkedHashSet<>();

        for (Voice voice : voices) {
            Locale locale = voice.getLocale();
            if (locale != null && studyLang.getLocaleDisplayLanguage().equals(locale.getDisplayLanguage()) && !voice.isNetworkConnectionRequired()) {
                String countryCode = locale.getCountry().toUpperCase();
                if ("en".equals(locale.getLanguage())) {
                    switch (countryCode) {
                        case "US":
                            usVoices.add(voice);
                            break;
                        case "CA":
                            caVoices.add(voice);
                            break;
                        case "AU":
                            auVoices.add(voice);
                            break;
                        case "NZ":
                            nzVoices.add(voice);
                            break;
                        case "GB":
                            gbVoices.add(voice);
                            break;
                    }
                }
            }
            int count = usVoices.size() + caVoices.size() + auVoices.size() + nzVoices.size() + gbVoices.size();
            if (count == Constant.VOICE_MAX_SIZE || count == totalCountToPlayVoiceOrTts) {
                break;
            }
        }
        LinkedHashSet<Voice> orderedVoices = new LinkedHashSet<>();
        orderedVoices.addAll(usVoices);
        orderedVoices.addAll(caVoices);
        orderedVoices.addAll(auVoices);
        orderedVoices.addAll(nzVoices);
        orderedVoices.addAll(gbVoices);


        return new ArrayList<>(orderedVoices);
    }
}
