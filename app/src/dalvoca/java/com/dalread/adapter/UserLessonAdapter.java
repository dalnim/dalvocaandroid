package com.dalread.adapter;

import android.content.Context;
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
import com.dalread.listener.OnClickListener;
import com.dalread.model.User;
import com.dalread.util.DateUtils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserLessonAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private String header;
    private List<User> fullUsers;
    private List<User> users;
    private OnClickListener listener;

    public UserLessonAdapter(Context context, SharedPreferencesDB sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        fullUsers = new ArrayList<>();
        users = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER)
            return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_simple, parent, false));
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_lesson, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind();
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + users.size();
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setData(List<User> users) {
        fullUsers.clear();
        this.users.clear();
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                user.setIndex(i + 1);
                fullUsers.add(user);
                this.users.add(user);
            }
        }
    }

    public void filterData(String searchString) {
        users.clear();
        if (TextUtils.isEmpty(searchString)) {
            users.addAll(fullUsers);
        } else {
            searchString = searchString.toLowerCase();
            for (User user : fullUsers) {
                String name = user.getName();
                if (!TextUtils.isEmpty(name) && name.toLowerCase().contains(searchString)) {
                    users.add(user);
                } else {
                    String location = Voca.getUserDisplayLocation(context, user);
                    if (!TextUtils.isEmpty(location) && location.toLowerCase().contains(searchString)) {
                        users.add(user);
                    }
                }
            }
        }
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header)
        TextView tvHeader;

        HeaderHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind() {
            tvHeader.setText(header);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_index)
        TextView tvIndex;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.tv_last_access_date)
        TextView tvLastAccessDate;
        @BindView(R.id.tv_mother_tongue)
        TextView tvMotherTongue;
        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.iv_action)
        ImageView ivAction;

        private User user;

        public ItemHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(int pos) {
            user = users.get(pos - 1);

            //
            String text = String.valueOf(user.getIndex());
            tvIndex.setText(text);
            //
            text = Voca.getUserDisplayName(sharedPreferences, user);
            tvName.setText(text);
            //
            text = Voca.getUserDisplayLocation(context, user);
            tvLocation.setText(text);
            //
            text = DateUtils.getDateFullFormat().format(new Date(DateUtils.secondsToMillis(user.getLastAccessDateTS())));
            tvLastAccessDate.setText(text);
            //
            text = user.getClientType() + ", " + user.getLangNative();
            tvMotherTongue.setText(text);
            //
            ivAvatar.setImageResource(Voca.getAvatarResource(user.getSex(), user.getAge()));
            //
            ivAction.setImageResource(R.drawable.ic_follow_on);
        }

        @OnClick({R.id.iv_avatar, R.id.iv_action})
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, user);
            }
        }
    }
}
