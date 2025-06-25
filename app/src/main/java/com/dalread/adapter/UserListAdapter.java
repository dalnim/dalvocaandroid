package com.dalread.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.User;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserListAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD_MORE = TYPE_ITEM + 1;

    private SharedPreferencesDB sharedPreferences;
    private ArrayList<Object> data = new ArrayList<>();
    private ArrayList<Object> backupData = new ArrayList<>();
    private int userListType;
    private boolean noAction;
    private OnUserClickListener listener;

    public UserListAdapter(SharedPreferencesDB sharedPreferences, int userListType, boolean noAction) {
        this.sharedPreferences = sharedPreferences;
        this.userListType = userListType;
        this.noAction = noAction;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) instanceof User)
            return TYPE_ITEM;
        return TYPE_LOAD_MORE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
        return new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_ITEM) {
            ((ItemHolder) viewHolder).bind(sharedPreferences, (User) data.get(position), userListType, noAction, listener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<User> users) {
        data.clear();
        data.addAll(users);
        backupData.clear();
        backupData.addAll(users);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<User> users) {
        int pos = data.size();
        data.addAll(users);
        backupData.addAll(users);
        notifyItemRangeInserted(pos, users.size());
    }

    public void filterData(String searchString) {
        data.clear();
        if (TextUtils.isEmpty(searchString)) {
            data.addAll(backupData);
        } else {
            String n2 = searchString.toLowerCase();
            for (Object object : backupData) {
                if (object instanceof User) {
                    User user = (User) object;
                    String n1 = user.getName().toLowerCase();
                    if (n1.contains(n2)) {
                        data.add(user);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setLoadMore(boolean enable) {
        int pos = data.size();
        if (enable) {
            data.add(null);
            notifyItemInserted(pos);
        } else if (data.remove(null)) {
            notifyItemRemoved(pos);
        }
    }

    public void setListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void notifyItemChanged(User user) {
        notifyItemChanged(data.indexOf(user));
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.iv_action)
        ImageView ivAction;

        private User user;
        private OnUserClickListener listener;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(SharedPreferencesDB sharedPreferences, User user, int userListType, boolean noAction, OnUserClickListener listener) {
            this.user = user;
            this.listener = listener;

            ivAvatar.setImageResource(BaseVoca.getAvatarResource(user.getSex(), user.getAge()));
            String name = BaseVoca.getUserDisplayName(sharedPreferences, user);
            tvName.setText(name);
            String location = BaseVoca.getUserDisplayLocation(itemView.getContext(), user);
            tvLocation.setText(location);
            if (noAction) {
                ivAction.setVisibility(View.GONE);
            } else {
                ivAction.setVisibility(View.VISIBLE);
                if (userListType == Constant.USER_LIST.FAVORITE) {
                    ivAction.setImageResource(user.isFavoriteUser() ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
                } else if (userListType == Constant.USER_LIST.BLOCK) {
                    ivAction.setImageResource(user.isBlockUser() ? R.drawable.ic_block_on : R.drawable.ic_block_off);
                } else {
                    ivAction.setImageResource(user.isFollowingUser() ? R.drawable.ic_follow_off : R.drawable.ic_follow_on);
                }
            }
        }

        @OnClick({R.id.v_item, R.id.iv_action})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.v_item:
                    if (listener != null) {
                        listener.onProfile(user);
                    }
                    break;
                case R.id.iv_action:
                    if (listener != null) {
                        listener.onAction(user);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnUserClickListener {

        void onProfile(User user);

        void onAction(User user);
    }
}
