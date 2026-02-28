package com.dalread.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.databinding.ItemMenuAppDownloadBinding;
import com.dalread.databinding.ItemMenuBinding;
import com.dalread.model.MenuModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

import java.util.List;

/**
 * Created by JetVHS on 3/1/2017.
 * Supports section header (e.g. "무료로 앱을 다운받으세요") and app download rows (click opens Play Store).
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SECTION_HEADER = 1;
    private static final int VIEW_TYPE_APP_DOWNLOAD = 2;

    private final Activity activity;
    private List<MenuModel> menuModels;

    /** 앱 다운로드 행 클릭 시 Play Store 열기. (appIdForFreeApps가 0일 때만 사용) */
    private OnOpenPlayStoreUrlListener onOpenPlayStoreUrlListener;
    /** 앱 다운로드 행 클릭 시 "무료 앱들" 화면 열기. 설정 시 appIdForFreeApps 있는 항목은 이걸 호출. */
    private OnOpenFreeAppListener onOpenFreeAppListener;

    public interface OnOpenPlayStoreUrlListener {
        void onOpenPlayStoreUrl(String url);
    }

    public interface OnOpenFreeAppListener {
        void onOpenFreeApp(int selectedAppId);
    }

    public void setOnOpenPlayStoreUrlListener(OnOpenPlayStoreUrlListener listener) {
        this.onOpenPlayStoreUrlListener = listener;
    }

    public void setOnOpenFreeAppListener(OnOpenFreeAppListener listener) {
        this.onOpenFreeAppListener = listener;
    }

    public MenuAdapter(Activity activity, List<MenuModel> menuModels) {
        this.activity = activity;
        this.menuModels = menuModels;
    }

    /** isShow == true인 항목만 세어 개수 반환 */
    private int getVisibleCount() {
        int count = 0;
        for (MenuModel m : menuModels) {
            if (m.isShow()) count++;
        }
        return count;
    }

    /** position번째로 보이는(visible) MenuModel 반환 */
    private MenuModel getVisibleModelAt(int position) {
        int idx = 0;
        for (MenuModel m : menuModels) {
            if (m.isShow()) {
                if (idx == position) return m;
                idx++;
            }
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MenuModel m = getVisibleModelAt(position);
        if (m == null) return VIEW_TYPE_ITEM;
        if (m.isSectionHeader()) return VIEW_TYPE_SECTION_HEADER;
        if (m.getPlayStoreUrl() != null) return VIEW_TYPE_APP_DOWNLOAD;
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_section_header, parent, false);
            AraThemeUtil.setBackgroundColor(activity, view, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.home_side_menu_item_background);
            return new SectionHeaderViewHolder(view);
        }
        if (viewType == VIEW_TYPE_APP_DOWNLOAD) {
            ItemMenuAppDownloadBinding binding = ItemMenuAppDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            AraThemeUtil.setBackgroundColor(activity, binding.root, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.home_side_menu_item_background);
            return new AppDownloadViewHolder(binding);
        }
        ItemMenuBinding binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        AraThemeUtil.setBackgroundColor(activity, binding.root, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.home_side_menu_item_background);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuModel obj = getVisibleModelAt(position);
        if (obj == null) return;
        if (holder instanceof SectionHeaderViewHolder) {
            ((SectionHeaderViewHolder) holder).tvTitle.setText(obj.getTitle());
        } else if (holder instanceof AppDownloadViewHolder) {
            AppDownloadViewHolder vh = (AppDownloadViewHolder) holder;
            vh.menuModel = obj;
            vh.binding.tvTitle.setText(obj.getTitle());
            vh.binding.ivIcon.setImageResource(obj.getIcon());
        } else {
            MyViewHolder vh = (MyViewHolder) holder;
            vh.menuModel = obj;
            vh.binding.ivIcon.setImageResource(obj.getIcon());
            vh.binding.ivIcon.setVisibility(obj.getIcon() != 0 ? View.VISIBLE : View.GONE);
            vh.binding.tvTitle.setText(obj.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return getVisibleCount();
    }

    public void updateData(List<MenuModel> menuModels) {
        this.menuModels = menuModels;
        notifyDataSetChanged();
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;

        SectionHeaderViewHolder(View itemView) {
            super(itemView);
            this.tvTitle = itemView.findViewById(R.id.tvSectionTitle);
        }
    }

    public class AppDownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MenuModel menuModel;
        final ItemMenuAppDownloadBinding binding;

        AppDownloadViewHolder(ItemMenuAppDownloadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handleItemClick(menuModel);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MenuModel menuModel;
        public ItemMenuBinding binding;

        MyViewHolder(ItemMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handleItemClick(menuModel);
        }
    }

    private void handleItemClick(MenuModel menuModel) {
        if (menuModel.getPlayStoreUrl() != null && onOpenFreeAppListener != null) {
            onOpenFreeAppListener.onOpenFreeApp(menuModel.getAppIdForFreeApps());
            return;
        }
        if (menuModel.getPlayStoreUrl() != null && onOpenPlayStoreUrlListener != null) {
            onOpenPlayStoreUrlListener.onOpenPlayStoreUrl(menuModel.getPlayStoreUrl());
            return;
        }
        if (activity instanceof OnNavigationItemClickListener) {
            ((OnNavigationItemClickListener) activity).onClick(menuModel.getId());
        }
    }
}
//public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
//
//    private final Activity activity;
//    private List<MenuModel> menuModels;
//
//
//    public MenuAdapter(Activity activity, List<MenuModel> menuModels) {
//        this.activity = activity;
//        this.menuModels = menuModels;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//        final MenuModel obj = menuModels.get(position);
//        holder.menuModel = obj;
//        holder.ivIcon.setImageResource(obj.getIcon());
//        holder.tvTitle.setText(obj.getTitle());
//    }
//
//    @Override
//    public int getItemCount() {
//        return menuModels.size();
//    }
//
//    public void updateData(List<MenuModel> menuModels) {
//        this.menuModels = menuModels;
//        notifyDataSetChanged();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public MenuModel menuModel;
//        @BindView(R.id.tvTitle)
//        TextView tvTitle;
//        @BindView(R.id.ivIcon)
//        ImageView ivIcon;
//
//        public MyViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (activity instanceof OnNavigationItemClickListener) {
//                ((OnNavigationItemClickListener) activity).onClick(menuModel.getId());
//            }
//        }
//    }
//
//}