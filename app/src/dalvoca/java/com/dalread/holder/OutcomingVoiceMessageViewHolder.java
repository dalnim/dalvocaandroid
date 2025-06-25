package com.dalread.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dalread.R;
import com.dalread.listener.OnChatVoiceMessageClickListener;
import com.dalread.model.ChatMessage;
import com.dalread.util.DateUtils;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

public class OutcomingVoiceMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<ChatMessage> {

    private TextView tvDuration;
    private TextView tvTime;
    private ImageButton icPlay;

    public OutcomingVoiceMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);

        tvDuration = itemView.findViewById(R.id.duration);
        tvTime = itemView.findViewById(R.id.time);
        icPlay = itemView.findViewById(R.id.ic_play);
    }

    @Override
    public void onBind(final ChatMessage message) {
        super.onBind(message);

        String strDuration;
        int totalMillis = message.getTotalMillis();
        if (totalMillis > 0) {
            int remainMillis = totalMillis - message.getPlayingMillis();
            strDuration = DateUtils.getTimeToMinuteFormat().format(new Date(remainMillis));
        } else if (!TextUtils.isEmpty(message.getDuration())) {
            strDuration = message.getDuration();
        } else {
            strDuration = "--:--";
        }
        tvDuration.setText(strDuration);
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
        icPlay.setImageResource(message.isPlaying() ? R.drawable.ic_stop_black_24dp : R.drawable.ic_play_black_24dp);
        icPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (payload instanceof OnChatVoiceMessageClickListener) {
                    ((OnChatVoiceMessageClickListener) payload).onClick(message);
                }
            }
        });
    }
}
