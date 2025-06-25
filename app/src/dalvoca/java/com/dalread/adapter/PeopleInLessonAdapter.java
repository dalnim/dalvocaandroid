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
import com.dalread.listener.OnClickListener;
import com.dalread.model.User;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PeopleInLessonAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private String header;
    private List<User> fullUsers;
    private List<User> users;
    private OnClickListener listener;

    public PeopleInLessonAdapter(Context context) {
        this.context = context;
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
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind();
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).bind(users.get(position - 1));
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
        this.fullUsers.clear();
        this.users.clear();
        if (users != null) {
            this.fullUsers.addAll(users);
            this.users.addAll(users);
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

        @BindView(R.id.ic_left)
        ImageView icLeft;
        @BindView(R.id.ic_right)
        ImageView icRight;
        @BindView(R.id.tv_primary)
        TextView tvPrimary;
        @BindView(R.id.tv_secondary)
        TextView tvSecondary;

        private User user;

        ItemHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            initLayout();
        }

        private void initLayout() {
            icRight.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
        }

        void bind(User user) {
            this.user = user;

            icLeft.setImageResource(Voca.getAvatarResource(user.getSex(), user.getAge()));
            tvPrimary.setText(user.getName());
            tvSecondary.setText(Voca.getUserDisplayLocation(context, user));
        }

        @OnClick({R.id.v_item, R.id.ic_left})
        void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.v_item:
                    if (listener != null) {
                        listener.onClick(itemView, user);
                    }
                    break;
                case R.id.ic_left:
                    if (listener != null) {
                        listener.onClick(icLeft, user);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
