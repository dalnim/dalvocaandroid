package com.dalread.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.OnNavigationItemClickListener;
import com.dalread.databinding.ItemMenuBinding;
import com.dalread.model.MenuModel;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.AraThemeUtil;

import java.util.List;

/**
 * Created by JetVHS on 3/1/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private final Activity activity;
    private List<MenuModel> menuModels;

    public MenuAdapter(Activity activity, List<MenuModel> menuModels) {
        this.activity = activity;
        this.menuModels = menuModels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuBinding binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        AraThemeUtil.setBackgroundColor(activity, binding.root, AppFlavorUtil.isAraMultiPlayerApp() ? R.color.multiPlayerBackgroundLightBlackColor : R.color.home_side_menu_item_background);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final MenuModel obj = menuModels.get(position);
        holder.menuModel = obj;
        holder.binding.ivIcon.setImageResource(obj.getIcon());
        holder.binding.tvTitle.setText(obj.getTitle());
    }

    @Override
    public int getItemCount() {
        return menuModels.size();
    }

    public void updateData(List<MenuModel> menuModels) {
        this.menuModels = menuModels;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MenuModel menuModel;
        public ItemMenuBinding binding;

        public MyViewHolder(ItemMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (activity instanceof OnNavigationItemClickListener) {
                ((OnNavigationItemClickListener) activity).onClick(menuModel.getId());
            }
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