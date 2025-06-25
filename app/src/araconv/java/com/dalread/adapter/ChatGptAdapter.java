package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.databinding.ItemGptAppPromptBinding;
import com.dalread.databinding.ItemGptResponseBinding;
import com.dalread.databinding.ItemGptUserPromptBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.GPT_CHAT_MESSAGE;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.util.TranslateUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;
//나중에 채팅 기능은 hosannahighertech/MessageKit를 사용하자.
public class ChatGptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PROMPT = 1;
    private static final int VIEW_TYPE_RESPONSE = 2;
    private static final int VIEW_TYPE_APP = 3;
    private GptResponseViewHolder gptResponseViewHolder;
    private static OnDoubleClickListener onDoubleClickListener;
//    private List<GptMessage> messageList;
    private List<GPT_CHAT_MESSAGE> messageList;
    private Context context;

    public ChatGptAdapter(Context context, OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.onDoubleClickListener = onDoubleClickListener;
        messageList = new ArrayList<>();
    }
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getREQUEST_RESONPSE_TYPE().equals(ChatCompletionRequest.RequestResponseType.REQUEST.name())) {
            return VIEW_TYPE_PROMPT;
        } else if (messageList.get(position).getREQUEST_RESONPSE_TYPE().equals(ChatCompletionRequest.RequestResponseType.RESPONSE.name())) {
            return VIEW_TYPE_RESPONSE;
        } else {
            return VIEW_TYPE_APP;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_PROMPT:
                ItemGptUserPromptBinding userPromptBinding = ItemGptUserPromptBinding.inflate(inflater, parent, false);
                return new UserPromptViewHolder(userPromptBinding);
            case VIEW_TYPE_RESPONSE:
                ItemGptResponseBinding responseBinding = ItemGptResponseBinding.inflate(inflater, parent, false);
                gptResponseViewHolder = new GptResponseViewHolder(responseBinding);
                return gptResponseViewHolder;
            case VIEW_TYPE_APP:
                ItemGptAppPromptBinding appPromptBinding = ItemGptAppPromptBinding.inflate(inflater, parent, false);
                return new AppPromptViewHolder(appPromptBinding);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        GptMessage message = messageList.get(position);
        GPT_CHAT_MESSAGE message = messageList.get(position);
        if (holder instanceof UserPromptViewHolder) {
            ((UserPromptViewHolder) holder).bind(message);
        } else if (holder instanceof GptResponseViewHolder) {
            ((GptResponseViewHolder) holder).bind(context, message, position);
        } else if (holder instanceof AppPromptViewHolder) {
            ((AppPromptViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(GPT_CHAT_MESSAGE message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    public void addMessages(List<GPT_CHAT_MESSAGE> messages) {
        messageList.addAll(0, messages);
        notifyItemRangeInserted(0, messages.size());
    }

    public void removeLastMessage() {
        if (!messageList.isEmpty()) {
            messageList.remove(messageList.size() - 1);
            notifyItemRemoved(messageList.size() - 1);
        }
    }
    public GPT_CHAT_MESSAGE getMessage(int position) {
        return messageList.get(position);
    }

    //지우지는 말것.
//    public void hideProgressBarOnResponse() {
//        if (gptResponseViewHolder != null) {
//            gptResponseViewHolder.hideProgressBar();
//        }
//    }
//
//    public void showProgressBarOnResponse() {
//        if (gptResponseViewHolder != null) {
//            gptResponseViewHolder.showProgressBar();
//        }
//    }

    private static class UserPromptViewHolder extends RecyclerView.ViewHolder {
        private ItemGptUserPromptBinding binding;

        public UserPromptViewHolder(ItemGptUserPromptBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GPT_CHAT_MESSAGE message) {
            binding.tvUserPrompt.setText(message.getMESSAGE_CONTENT());
        }
    }

    private static class AppPromptViewHolder extends RecyclerView.ViewHolder {
        private ItemGptAppPromptBinding binding;

        public AppPromptViewHolder(ItemGptAppPromptBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GPT_CHAT_MESSAGE message) {
            binding.tvAppPrompt.setText(message.getMESSAGE_CONTENT());
        }
    }

    private static class GptResponseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemGptResponseBinding binding;
        private GPT_CHAT_MESSAGE message;
        private Context context;
        public GptResponseViewHolder(ItemGptResponseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void initListener() {
            binding.layoutTopIcon.ibVocaList.setOnClickListener(this);
//            binding.ibSpeaker.setOnClickListener(this); //이건 더블 클릭이 안된다.
            binding.layoutTopIcon.ibSpeaker.setOnClickListener(new DoubleClick(onDoubleClickListener, message)); //onDoubleClickListener를 쓰면 스피커 아이콘이 변하는데 조금 시간이 걸린다.
            binding.layoutTopIcon.ibCopy.setOnClickListener(this);
            binding.layoutTopIcon.ivTranslate.setOnClickListener(view -> {
                TranslateUtil.openWebTranslate(context, message.getMESSAGE_CONTENT(), () -> {});
            });
        }

        public void bind(Context context, GPT_CHAT_MESSAGE message, int position) {
            this.context = context;
            this.message = message;
            initListener();
            message.setAdapterPosition(position);
            hideIcons();
            if (!Utils.isEmpty(message.getMESSAGE_CONTENT())) {
                binding.tvGptResponse.setVisibility(View.VISIBLE);
                binding.tvGptResponse.setText(message.getMESSAGE_CONTENT());
            }
            binding.tvDiffcultWords.setVisibility(Utils.isEmpty(message.getDifficultWordMeaning()) ? View.GONE : View.VISIBLE);
            binding.tvDiffcultWords.setText(message.getDifficultWordMeaning());
            updatePlayingIcon();
        }

        private void hideIcons() {
            int viewVisibility = View.VISIBLE;
            if (Utils.isEmpty(message.getMESSAGE_CONTENT())) {
                viewVisibility = View.INVISIBLE;
            }
            binding.layoutTopIcon.ibSpeaker.setVisibility(viewVisibility);
            binding.layoutTopIcon.ibCopy.setVisibility(viewVisibility);
            binding.layoutTopIcon.ibVocaList.setVisibility(viewVisibility);
        }
//        private void updatePlayingIcon() {
//            StateListDrawable stateListDrawable = new StateListDrawable();
//            stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_left));
//            stateListDrawable.addState(new int[] {android.R.attr.state_focused}, ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_right));
//            stateListDrawable.addState(new int[] {}, ContextCompat.getDrawable(itemView.getContext(), gptMessage.isPlayingTts() ? R.drawable.ic_volume_up_48dp : R.drawable.ic_volume_up_outline_48dp));
//            binding.ibSpeaker.setImageDrawable(stateListDrawable);
//
////            if (gptMessage.isPlayingTts()) {
//                // Delay the update to the image resource to ensure that the button is shown with the correct image resource
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        binding.ibSpeaker.setImageResource(gptMessage.isPlayingTts() ? R.drawable.ic_volume_up_48dp : R.drawable.ic_volume_up_outline_48dp);
////                    }
////                }, 200);
////            }
//        }

//        private void updatePlayingIcon() {
//            StateListDrawable stateListDrawable = new StateListDrawable();
//            stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_volume_up_white_36dp));
//            stateListDrawable.addState(new int[] {android.R.attr.state_focused}, ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_volume_off_white_36dp));
//            stateListDrawable.addState(new int[] {}, ContextCompat.getDrawable(itemView.getContext(), gptMessage.isPlayingTts() ? R.drawable.ic_volume_up_48dp : R.drawable.ic_volume_up_outline_48dp));
//            binding.ibSpeaker.setImageDrawable(stateListDrawable);
//        }

//
        private void updatePlayingIcon() {
            if (message.isPlayingTts()) {
                binding.layoutTopIcon.ibSpeaker.setImageResource(R.drawable.ic_volume_up_48dp);
            } else {
                binding.layoutTopIcon.ibSpeaker.setImageResource(R.drawable.ic_volume_up_outline_48dp);
            }
        }

        //아래는 버그가 있다. 현재 쉘 포지션을 잘 못찾는거 같다.
        public void hideProgressBar() {
//            binding.ivGptIcon.setVisibility(View.VISIBLE);
//            binding.progressBar.setVisibility(View.INVISIBLE);
        }
        public void showProgressBar() {
//            binding.ivGptIcon.setVisibility(View.INVISIBLE);
//            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (onDoubleClickListener != null) {
                onDoubleClickListener.onClick(v, message);
            }
        }
    }
}


