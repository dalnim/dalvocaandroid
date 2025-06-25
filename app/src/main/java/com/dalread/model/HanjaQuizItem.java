package com.dalread.model;

import java.io.Serializable;

public interface HanjaQuizItem extends Serializable {

    String getHQIVoca();

    String getHQIMeaningWithPronounceForHanja();

    String getHQIParentVoca();

    void setHQIParentVoca(String parentVoca);

    int getHQIIndexHanja();

    void setHQIIndexHanja(int indexHanja);
}
