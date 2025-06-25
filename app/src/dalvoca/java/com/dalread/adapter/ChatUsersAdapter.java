package com.dalread.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.activity.MyProfileActivity;
import com.dalread.activity.OtherProfileActivity;
import com.dalread.base.BaseVocaActivity;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.User;
import com.dalread.util.Constant;
import com.dalread.util.Voca;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatUsersAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private SharedPreferencesDB sharedPreferences;
    private ArrayList<User> users;
    private boolean isChatMode;

    public ChatUsersAdapter(Activity activity, SharedPreferencesDB sharedPreferences) {
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
        users = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatUserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatUserHolder) {
            ((ChatUserHolder) holder).bind(users.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(ArrayList<User> users, boolean isChatMode) {
        this.users.clear();
        if (users != null) {
            this.users.addAll(users);
        }
        this.isChatMode = isChatMode;
        notifyDataSetChanged();
    }

    class ChatUserHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.iv_action)
        ImageView ivAction;
        @BindView(R.id.iv_arrow)
        ImageView ivArrow;

        private User user;

        ChatUserHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            ivAction.setVisibility(View.GONE);
            ivArrow.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (activity instanceof BaseVocaActivity && user != null) {
                        if (user.getUid() == sharedPreferences.getRealUid()) {
                            ((BaseVocaActivity) activity).openNewScreen(MyProfileActivity.class);
                        } else {
                            Intent intent = new Intent(activity, OtherProfileActivity.class);
                            intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, user.getUid());
                            ((BaseVocaActivity) activity).openNewScreen(intent);
                        }
                    }
                }
            });
        }

        void bind(User user) {
            this.user = user;

            ivAvatar.setImageResource(Voca.getAvatarResource(user.getSex(), user.getAge()));
            String name = Voca.getUserDisplayName(sharedPreferences, user);
            tvName.setText(name);
            if (isChatMode) {
                String location = Voca.getUserDisplayLocation(itemView.getContext(), user);
                tvLocation.setText(location);
            } else {
                tvLocation.setText(Voca.getUserStudyRoleTitle(itemView.getContext(), user, true));
            }
        }
    }
}
