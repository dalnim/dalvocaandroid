package com.dalread.network;

public enum GptStudyMode {
    FREE_TALKING,
    DIALOGUE,
    VOCA;

    public boolean isFreeTalking() {
        return this == FREE_TALKING;
    }

    public boolean isDialogue() {
        return this == DIALOGUE;
    }

    public boolean isVoca() {
        return this == VOCA;
    }
}
