package com.dalread.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.ActivityGptWebMenuBinding;
import com.dalread.databinding.ItemGptWebMenuBinding;
import com.dalread.dialog.ChatGptMenuEditDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperAdapter;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperCallback;
import com.dalread.listener.OnDragListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.GptWebMenuHelper;
import com.dalread.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GptWebMenuActivity extends BaseConvActivity {
    private static LinkedHashMap<String, String> menuMap;
    private MenuAdapter adapter;
    private GptWebMenuHelper gptMenuHelper;
    private ActivityGptWebMenuBinding binding;
    protected View getContentView() {
        binding = ActivityGptWebMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle("GPT 웹 메뉴");
        gptMenuHelper = new GptWebMenuHelper(this);
        menuMap = gptMenuHelper.getMenuList();
        initRecyclerView();
        binding.addButton.setOnClickListener(v -> {
            ChatGptMenuEditDialog dialog = new ChatGptMenuEditDialog(GptWebMenuActivity.this, "", "", (shortCut, expandedPhrase) -> {
                if (menuMap.containsKey(shortCut)) {
                    ToastUtil.getInstance(GptWebMenuActivity.this).show("메뉴 명이 존재합니다. 다른 메뉴명을 사용하세요.");
                } else {
                    menuMap.put(shortCut, expandedPhrase);
                    gptMenuHelper.saveMenuList();
                    adapter.notifyItemInserted(menuMap.size() - 1);
                }
            });
            dialog.showDialog();
        });
    }

    protected void initRecyclerView() {
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(adapter = new MenuAdapter(menuMap))
        );
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        adapter.setDragListener(new OnDragListener() {
            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onDragStopped() {
                gptMenuHelper.saveMenuList();
            }
        });

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    public static void swapItems(LinkedHashMap<String, String> map, int fromPosition, int toPosition) {
        List<String> keyList = new ArrayList<>(map.keySet());
        List<String> valueList = new ArrayList<>(map.values());

        Collections.swap(keyList, fromPosition, toPosition);
        Collections.swap(valueList, fromPosition, toPosition);

        map.clear();
        for (int i = 0; i < keyList.size(); i++) {
            map.put(keyList.get(i), valueList.get(i));
        }
    }

//    private void getMenuList() {
//        String webMenuString = sharedPreferences.getChatGptWebMenu();
//        Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
//        menuMap = sharedPreferences.fromJson(webMenuString, type);
//
//        if (menuMap == null || menuMap.isEmpty()) {
//            menuMap = new LinkedHashMap<>();
//            menuMap.put("Menu Item 1", "Value 1");
//            menuMap.put("Menu Item 2", "Value 2");
//            menuMap.put("Menu Item 3", "Value 3");
//            saveMenuMap();
//        }
//    }

//    private void saveMenuMap() {
//        Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
//        String webMenuString = sharedPreferences.toJson(menuMap, type);
//        sharedPreferences.setChatGptWebMenu(webMenuString);
//    }

    public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> implements ItemTouchHelperAdapter {
        private OnDragListener onDragListener;
        private LinkedHashMap<String, String> menus;

        public MenuAdapter(LinkedHashMap<String, String> menus) {
            this.menus = menus;
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ItemGptWebMenuBinding binding = ItemGptWebMenuBinding.inflate(inflater, parent, false);
            return new MenuViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            Map.Entry<String, String> menu = getMapAtIndex(menus, position);
//            String menu = menus.get(position);
            holder.bind(menu, this);
        }

        private Map.Entry<String, String> getMapAtIndex(LinkedHashMap<String, String> menuMap, int index) {
            Set<Map.Entry<String, String>> entrySet = menuMap.entrySet();
            List<Map.Entry<String, String>> entryList = new ArrayList<>(entrySet);
            return entryList.get(index);
        }


        @Override
        public int getItemCount() {
            return menus.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            swapItems(menuMap, fromPosition - 1, toPosition - 1);
            notifyItemMoved(fromPosition, toPosition);
        }
        public void setDragListener(OnDragListener onDragListener) {
            this.onDragListener = onDragListener;
        }
        private class MenuViewHolder extends RecyclerView.ViewHolder {

            private final ItemGptWebMenuBinding binding;

            public MenuViewHolder(@NonNull ItemGptWebMenuBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

            }

            public void bind(Map.Entry<String, String> menu, MenuAdapter adapter) {
                String originalShortCut = menu.getKey();
                String originalExpandedPhrase = menu.getValue();
                binding.tvShortcut.setText(originalShortCut);
                binding.tvExpandedPhrase.setText(originalExpandedPhrase);
                onDeleteItem(originalShortCut);
                onEditItem(adapter, originalShortCut, originalExpandedPhrase);
            }

            private void onEditItem(MenuAdapter adapter, String originalShortCut, String originalExpandedPhrase) {
                binding.llRoot.setOnClickListener( v -> {
                    ChatGptMenuEditDialog dialog = new ChatGptMenuEditDialog(GptWebMenuActivity.this, originalShortCut, originalExpandedPhrase, (shortCut, expandedPhrase) -> {
                        if (!shortCut.equals(originalShortCut)) {
                            menuMap.remove(originalShortCut);
                        }
                        menuMap.put(shortCut, expandedPhrase);
                        gptMenuHelper.saveMenuList();
                        adapter.notifyDataSetChanged();
                    });
                    dialog.showDialog();
                });
            }

            private void onDeleteItem(String originalShortCut) {
                binding.ivDelete.setOnClickListener(v -> {
                    final YesNoDialog dialog = new YesNoDialog(GptWebMenuActivity.this, R.string.warning, R.string.messsage_ask_to_delete_item, null, new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            menuMap.remove(originalShortCut);
                            notifyItemRemoved(getBindingAdapterPosition());
                        }
                        @Override
                        public void onNoClick(View view, Object object) {

                        }
                    });
                    dialog.show();

                });
            }
        }
    }

    @Override
    public void onDestroy() {
        gptMenuHelper.saveMenuList();
        super.onDestroy();
    }
}


