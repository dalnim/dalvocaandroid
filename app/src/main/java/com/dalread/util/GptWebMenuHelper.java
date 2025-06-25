package com.dalread.util;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
//일단 이건 더이상 안쓴다. 메뉴는 고정적으로 할거다.
public class GptWebMenuHelper {
    private LinkedHashMap<String, String> menuMap;
    private SharedPreferencesDB sharedPreferences;
    private Context context;
    public GptWebMenuHelper(Context context) {
        this.context = context;
        sharedPreferences = SharedPreferencesDB.getInstance(context);
        getMenuList();
    }
    private void makeDefaultMenuList() {
        menuMap = new LinkedHashMap<>();
        menuMap.put("Menu Item 1", "Value 1");
        menuMap.put("Menu Item 2", "Value 2");
        menuMap.put("Menu Item 3", "Value 3");
        saveMenuList();
    }
    public LinkedHashMap<String, String> getMenuList() {
        String webMenuString = sharedPreferences.getChatGptWebMenu();
        Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
        menuMap = sharedPreferences.fromJson(webMenuString, type);

        if (menuMap == null || menuMap.isEmpty()) {
            makeDefaultMenuList();
        }
        return menuMap;
    }
    public void saveMenuList() {
        Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
        String webMenuString = sharedPreferences.toJson(menuMap, type);
        sharedPreferences.setChatGptWebMenu(webMenuString);
    }
    public String getValue(String key) {
        if (hasKey(key)) {
            return menuMap.get(key);
        }
        return "";
    }
    public boolean hasKey(String key) {
        return menuMap.containsKey(key);
    }
}
