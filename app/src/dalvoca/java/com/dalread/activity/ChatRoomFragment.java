package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.ChatRoomAdapter;
import com.dalread.base.BaseChatFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnChatRoomClickListener;
import com.dalread.model.ChatRoom;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class ChatRoomFragment extends BaseChatFragment {

    @BindView(R.id.rv_chat_room)
    RecyclerView rvChatRoom;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private AlertDialog alertDialog;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatRoom> chatRooms;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_chat_room;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();

        getData();
    }

    private void initData() {
        context = getContext();
        chatRooms = new ArrayList<>();
    }

    private void initLayout() {
        alertDialog = new AlertDialog(context);
        adapter = new ChatRoomAdapter(chatRooms, onChatRoomClickListener);
        rvChatRoom.setAdapter(adapter);
        rvChatRoom.setLayoutManager(new LinearLayoutManager(context));
        rvChatRoom.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
    }

    private void getData() {
        int uid = sharedPreferences.getRealUid();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().getChatroomList(new DalApiListener<List<ChatRoom>>() {

                    @Override
                    public void onSuccess(List<ChatRoom> response) {
                        if (response != null) {
                            chatRooms.addAll(response);
                            adapter.notifyDataSetChanged();
                        }
                        Loading.hide();
                    }

                    @Override
                    public void onFailure(String error) {
                        Loading.hide();
                    }
                });
            } else {
                alertDialog.showNoInternet();
            }
        } else {
            alertDialog.showLogInRequired();
        }
    }

    private OnChatRoomClickListener onChatRoomClickListener = new OnChatRoomClickListener() {

        @Override
        public void onClick(ChatRoom chatRoom) {
            Intent intent = new Intent(context, ChatDetailsActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_CHAT_ROOM, chatRoom);
            activity.openNewScreen(intent);
        }
    };
}
