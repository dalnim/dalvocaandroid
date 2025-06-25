package com.dalread.model;

import java.io.Serializable;

public interface AmkiItem extends Serializable {

    int getAmkiId();

    int getAmkiType();

    int getAmkiKnow();

    int getAmkiKnowPronounce();

    String getAmkiEvaluationGrade();

    String getAmki();
}
