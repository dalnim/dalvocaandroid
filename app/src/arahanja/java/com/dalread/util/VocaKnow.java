package com.dalread.util;

import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.model.HanjaItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VocaKnow extends BaseVocaKnow {
    public static List<HanjaItem> getUnknowAndLessVoca(List<HanjaItem> list, int limit) {
        Collections.shuffle(list);
        return list.stream()
                .filter( e -> VocaKnow.isUnknownAndLess((IVocaBasicItem) e))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
