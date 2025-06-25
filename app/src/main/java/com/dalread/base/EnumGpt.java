package com.dalread.base;

public enum EnumGpt {

    CHATGPT(0, "Chat Gpt", "https://chat.openai.com/chat"),
    BARD(1, "Google Bard", "https://bard.google.com");
    private int id;
    private String name;
    private String url;

    EnumGpt(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public static String[] getNames() {
        int count = values().length;
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = values()[i].getName();
        }
        return names;
    }

    public static String getNameFromId(int id) {
        String name = CHATGPT.name;
        for (EnumGpt enumGpt : EnumGpt.values()) {
            if (enumGpt.id == id) {
                name = enumGpt.name;
                break;
            }
        }
        return name;
    }

    public static String getUrlFromId(int id) {
        String url = CHATGPT.url;
        for (EnumGpt enumGpt : EnumGpt.values()) {
            if (enumGpt.id == id) {
                url = enumGpt.getUrl();
                break;
            }
        }
        return url;
    }

}
