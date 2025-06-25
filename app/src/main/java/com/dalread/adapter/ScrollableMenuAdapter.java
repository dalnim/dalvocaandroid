package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemScrollableMenuInPlayerBinding;
import com.dalread.listener.OnScrollableMenuClickListener;
import com.dalread.model.ScrollableMenuModel;

import java.util.List;

public class ScrollableMenuAdapter extends RecyclerView.Adapter<ScrollableMenuAdapter.ScrollableMenuVH> {

    private List<ScrollableMenuModel> scrollableMenuModelList;
    private boolean isExpanded = false;
    private OnScrollableMenuClickListener listener;

    public ScrollableMenuAdapter(List<ScrollableMenuModel> scrollableMenuModelList) {
        this.scrollableMenuModelList = scrollableMenuModelList;
    }

    @Override
    public ScrollableMenuVH onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemScrollableMenuInPlayerBinding binding = ItemScrollableMenuInPlayerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ScrollableMenuVH(binding);
    }

    @Override
    public void onBindViewHolder(final ScrollableMenuVH holder, final int position) {
        ScrollableMenuModel data = scrollableMenuModelList.get(position);
        holder.bindData(data);
    }

    @Override
    public int getItemCount() {
        return scrollableMenuModelList.size();
    }

    public void setScrollableMenuModelList(List<ScrollableMenuModel> scrollableMenuModelList) {
        this.scrollableMenuModelList = scrollableMenuModelList;
        notifyDataSetChanged();
    }

    public void addItem(ScrollableMenuModel menu, int position) {
        scrollableMenuModelList.add(position, menu);
        notifyDataSetChanged();
    }

    public boolean isMenuExisted(ScrollableMenuModel menuModel) {
        return scrollableMenuModelList.contains(menuModel);
    }

    public void setExpanded(boolean expanded) {
        if (isExpanded == expanded) return;
        isExpanded = expanded;
        notifyDataSetChanged();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setScrollableMenuClickListener(OnScrollableMenuClickListener listener) {
        this.listener = listener;
    }

    public class ScrollableMenuVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        ScrollableMenuModel mMenu;
        ItemScrollableMenuInPlayerBinding binding;

        public ScrollableMenuVH(ItemScrollableMenuInPlayerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
        }

        private void setOnClickListeners() {
            binding.imgIcon.setOnClickListener(this);
            binding.tvMenuName.setOnClickListener(this);
        }

        public void bindData(ScrollableMenuModel menu) {
            this.mMenu = menu;

            binding.tvMenuName.setVisibility(isExpanded ? View.VISIBLE : View.INVISIBLE);
            binding.tvMenuName.setText(menu.getMenuName());
//            if (menu.getType() == BUTTON_EXPAND) {
//                binding.imgIcon.setRotation(isExpand ? 180 : 0);
//            } else  {
//                binding.tvMenuName.setVisibility(isExpand ? View.VISIBLE : View.INVISIBLE);
//            }
//            binding.imgIcon.setSelected(menu.isSelected());
            binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(binding.imgIcon.getContext(), menu.getDrawableIcon()));
//            binding.llScrollableMenu.setOnClickListener(v -> {
////                if (menu.getType() == BUTTON_EXPAND) {
////                    setExpand(!isExpand);
////                    if (isExpand) {
////                        listener.onClickExpand();
////                    } else {
////                        listener.onClickCollapse();
////                    }
////                } else {
//                    menu.setSelected(!menu.isSelected());
//                    notifyItemChanged(getAdapterPosition());
//                    listener.onClickMenu(menu);
////                }
//            });
        }

        @Override
        public void onClick(View view) {
            if (listener == null)
                return;

            if (isMenuNameInVisible(view))
                return;

            switch (view.getId()) {
                case R.id.imgIcon:
                case R.id.tvMenuName:
//                    mMenu.setSelected(!mMenu.isSelected());
//                    notifyItemChanged(getAdapterPosition());
                    listener.onClickMenu(mMenu);

                    break;
            }

        }

        private boolean isMenuNameInVisible(View view) {
            return (view.getId() == R.id.tvMenuName) && (binding.tvMenuName.getVisibility() == View.INVISIBLE);
        }

    }
}