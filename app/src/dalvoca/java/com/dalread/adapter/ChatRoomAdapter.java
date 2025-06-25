package com.dalread.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnChatRoomClickListener;
import com.dalread.model.ChatRoom;
import com.dalread.util.DateUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatRoom> chatRooms;
    private OnChatRoomClickListener listener;

    public ChatRoomAdapter(ArrayList<ChatRoom> chatRooms, OnChatRoomClickListener listener) {
        this.chatRooms = chatRooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemHolder) {
            ((ItemHolder) viewHolder).bind(chatRooms.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return chatRooms == null ? 0 : chatRooms.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_room_avatar)
        ImageView ivRoomAvatar;
        @BindView(R.id.tv_room_name)
        TextView tvRoomName;
        @BindView(R.id.tv_last_message)
        TextView tvLastMessage;
        @BindView(R.id.tv_last_date)
        TextView tvLastDate;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(final ChatRoom chatRoom, final OnChatRoomClickListener listener) {
            String text = chatRoom.getChatRoomName();
            tvRoomName.setText(text);
            text = chatRoom.getLastMessage();
            tvLastMessage.setText(text);
            text = DateUtils.getDateChatRoom(chatRoom.getLastAccessDate());
            tvLastDate.setText(text);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(chatRoom);
                    }
                }
            });
        }
    }
}
