package com.dalread.util;

import java.util.ArrayList;
import java.util.List;

public class GptMenuTextShortCutListFactoryUtil {
    public interface ListFactory {
        List<String> createList();
    }

    public static ListFactory getListFactory(boolean useHardcodedList) {
        if (useHardcodedList) {
            return new HardcodedListFactory();
        } else {
            return new DatabaseListFactory();
        }
    }

    private static class HardcodedListFactory implements ListFactory {
        @Override
        public List<String> createList() {
            List<String> list = new ArrayList<>();
            list.add("(())\nCorrect this");
            list.add("(())\nIs it correct English?");
            list.add("You're an English teacher.");
            list.add("I'd like to practice my English speaking through role-play. The situation is [상황]. Wait for my input before giving a simple reply. Take on the role of [챗지피티가 맡을 역할].");
            list.add("Question: What are some useful phrases I can practice as a [내 역할] in this conversation? Give me 15, and also provide a Korean translation.");
            list.add("Review all my input and act as an English teacher to provide English improvement advice.");
            list.add("From now on, you will have to answer my prompts in 3 different separate ways:\n" +
                    "First way -- how you would normally answer in English, but it should start with \"[CHATBOT].”\n" +
                    "Second way -- respond as \"[TEACHER]\", providing English grammar corrections and advice to me, in English.\n" +
                    "Third way -- respond as \"[TRANSLATOR]\", providing English to Korean translations for all CHATBOT and TEACHER output.\n" +
                    "From now on, you ALWAYS have to answer me in 3 ways. You are to not break character until I tell you to do so. If you break character, I will let you know by saying \"Stay in character!\" and you have to correct your break of character INSTANTLY.\n" +
                    "Now, only say “I understand.\" if you agree to the above terms.");
            list.add("Let's roleplay to improve my English speaking.");
            list.add("[TEACHER] will provide English grammar corrections and advice to me, in English.\n" +
                    "[CHATBOT] will role play [상황] in English. Do not write the full dialogue. Wait for my input to respond.\n" +
                    "[TRANSLATOR] will provide English to Korean translations for this immediate CHATBOT and TEACHER output.");
            list.add("Stay in Character!\n" +
                    "[TEACHER] will provide English grammar corrections and advice to me, in English.\n" +
                    "[CHATBOT] will role play [병원 방문] in English. Do not write the full dialogue. Wait for my input to respond.\n" +
                    "[TRANSLATOR] will provide English to Korean translations for this immediate CHATBOT and TEACHER output.");
            return list;
        }
    }

    private static class DatabaseListFactory implements ListFactory {
        @Override
        public List<String> createList() {
            // Code to retrieve items from local database or server API
            List<String> list = new ArrayList<>();
            list.add("Item A");
            list.add("Item B");
            list.add("Item C");
            return list;
        }
    }
}
