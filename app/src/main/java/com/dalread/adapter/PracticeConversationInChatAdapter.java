package com.dalread.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.dalread.R;
import com.dalread.databinding.ItemGptAppPromptBinding;
import com.dalread.databinding.ItemGptResponseBinding;
import com.dalread.databinding.ItemGptUserPromptBinding;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.ConversationModel;
import com.dalread.model.WordListType;
import com.dalread.util.Constant;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;
import com.dalread.util.VocaKnow;

import java.util.List;

//나중에 채팅 기능은 hosannahighertech/MessageKit를 사용하자.
public class PracticeConversationInChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PROMPT = 1;
    private static final int VIEW_TYPE_RESPONSE = 2;
    private static final int VIEW_TYPE_APP = 3;
    private ResponseViewHolder responseViewHolder;
    private static OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private static OnDoubleClickListener onDoubleClickListener;
    private List<ConversationModel> modelList;
    private static Context context;
    private static boolean isDisplayTranslation;
    private static WordListType wordListType;
    private RecyclerView recyclerView;

    public PracticeConversationInChatAdapter(Context context, List<ConversationModel> modelList, boolean isDisplayTranslation, WordListType wordListType, RecyclerView recyclerView, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow,  OnDoubleClickListener onDoubleClickListener) {
        this.context = context;
        this.isDisplayTranslation = isDisplayTranslation;
        this.wordListType = wordListType;
        this.recyclerView = recyclerView;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        this.onDoubleClickListener = onDoubleClickListener;
        this.modelList = modelList;
    }
    @Override
    public int getItemViewType(int position) {
        if (modelList.get(position).isRequest()) {
            return VIEW_TYPE_PROMPT;
        } else if (modelList.get(position).isResponse()) {
            return VIEW_TYPE_RESPONSE;
        } else {
            return VIEW_TYPE_APP;
        }
    }
    private static boolean isShowVocaOrMeaningOnly() {
        if ((wordListType == WordListType.SERVER_VOCA_BOOK_EXPRESSION_LIST)
                || (wordListType == WordListType.BOOKMARK)
                || (wordListType == WordListType.VOCA_TYPE_ID_LIST)) {

            return true;
        }
        return false;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_PROMPT:
                ItemGptUserPromptBinding userPromptBinding = ItemGptUserPromptBinding.inflate(inflater, parent, false);
                UserPromptBindingWrapper userPromptBindingWrapper = new UserPromptBindingWrapper(userPromptBinding);
                return new UserPromptViewHolder(userPromptBindingWrapper);
            case VIEW_TYPE_RESPONSE:
                ItemGptResponseBinding responseBinding = ItemGptResponseBinding.inflate(inflater, parent, false);
                ResponseBindingWrapper responseBindingWrapper = new ResponseBindingWrapper(responseBinding);
                responseViewHolder = new ResponseViewHolder(responseBindingWrapper);
                return responseViewHolder;
            case VIEW_TYPE_APP:
                ItemGptAppPromptBinding appPromptBinding = ItemGptAppPromptBinding.inflate(inflater, parent, false);
                return new AppPromptViewHolder(appPromptBinding);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConversationModel message = modelList.get(position);
        if (holder instanceof UserPromptViewHolder) {
            ((UserPromptViewHolder) holder).bind(message, position);
        } else if (holder instanceof ResponseViewHolder) {
            ((ResponseViewHolder) holder).bind(message, position);
        } else if (holder instanceof AppPromptViewHolder) {
            ((AppPromptViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void addModel(ConversationModel model) {
        if (model != null) {
            modelList.add(model);
            recyclerView.post(() -> {
                notifyDataSetChanged();
//                notifyItemInserted(modelList.size() - 1);
            });
            recyclerView.post(() -> {
                recyclerView.smoothScrollToPosition(modelList.size() - 1);
            });
        }
    }

    public ConversationModel getModel(int position) {
        if (Utils.isIndexInsideRange(modelList, position)) {
            return modelList.get(position);
        }
        return null;
    }

    public void resetAllPlayingIcons() {
        modelList.forEach(message -> message.setPlayingTts(false));
        notifyDataSetChanged();
    }

    public void setIsDisplayTranslation(boolean isDisplayTranslation) {
        PracticeConversationInChatAdapter.isDisplayTranslation = isDisplayTranslation;
        recyclerView.post(() -> {
            notifyDataSetChanged();
        });
    }

    public static abstract class BaseViewHolder<T extends ILayoutTopIcon> extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected T binding;
        protected ConversationModel model;

        public BaseViewHolder(T binding) {
            super(binding.getBinding().getRoot());
            this.binding = binding;
        }

        protected void bind(ConversationModel model, int position) {
            this.model = model;
            initListener();
            model.setAdapterPosition(position);
            toggleIconsVisibility();
            displayPrompt(model);
            updatePlayingIcon();
            VocaKnow.updateIconVocaBookmark(binding.getIvBookmark(), model, true);
            VocaKnow.updateIconVocaKnowGrayCircle(binding.getIvKnow(), model);
        }

        protected void initListener() {
            binding.getIbVocaList().setOnClickListener(this);
            binding.getIbSpeaker().setOnClickListener(this); //이건 더블 클릭이 안된다.
//            binding.getIbSpeaker().setOnClickListener(new DoubleClick(onDoubleClickListener, model)); //onDoubleClickListener를 쓰면 스피커 아이콘이 변하는데 조금 시간이 걸린다.
            binding.getIbCopy().setOnClickListener(this);
            binding.getIvTranslate().setOnClickListener(this);
            binding.getIvGpt().setOnClickListener(this);
            binding.getIvBookmark().setOnClickListener(this);
//            binding.getIvKnow().setOnClickListener(this);
            binding.getIvKnow().setOnClickListener(new DoubleClick(onDoubleClickListenerOnBaseVocaKnow, model));
            binding.getIvKnowPronounce().setOnClickListener(this);

        }
        protected void displayPrompt(ConversationModel model) {
            String messageContent = model.getMESSAGE_CONTENT();
            String messageTranslation = model.getMESSAGE_TRANSLATION();
            if (isDisplayTranslation && !Utils.isEmpty(messageTranslation)) {
                messageContent += Constant.BREAK_CHARACTER + "[" + messageTranslation + "]";
            }
            binding.getTvChatBubble().setText(messageContent);
        }
        protected void toggleIconsVisibility() {
//            int viewVisibility = View.VISIBLE;
//            if (Utils.isEmpty(model.getMESSAGE_CONTENT())) {
//                viewVisibility = View.INVISIBLE;
//            }
//            binding.getIbSpeaker().setVisibility(viewVisibility);
//            binding.getIbCopy().setVisibility(viewVisibility);
//            binding.getIbVocaList().setVisibility(viewVisibility);
        }

        protected void updatePlayingIcon() {
            if (model.isPlayingTts()) {
                binding.getIbSpeaker().setImageResource(R.drawable.ic_volume_up_48dp);
            } else {
                binding.getIbSpeaker().setImageResource(R.drawable.ic_volume_up_outline_48dp);
            }
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivBookmark:
                case R.id.iv_known:
                case R.id.iv_grade_1:
                case R.id.iv_grade_2:
                case R.id.iv_unknown:
                    if (UserUtil.isLoggedIn(context, true)) {
                        if (onDoubleClickListenerOnBaseVocaKnow != null) {
                            onDoubleClickListenerOnBaseVocaKnow.onClick(v, model);
                        }
                    }
                    break;
                default:
                    if (onDoubleClickListener != null) {
                        onDoubleClickListener.onClick(v, model);
                    }
                    break;

            }
        }
    }

    private interface ILayoutTopIcon {
        ImageButton getIbVocaList();
        ImageButton getIbSpeaker();
        ImageButton getIbCopy();
        ImageView getIvTranslate();
        ImageView getIvGpt();
        ImageView getIvBookmark();
        LinearLayout getLlKnowValue();
        ImageView getIvKnow();
        ImageView getIvKnowPronounce();
        TextView getTvChatBubble();
        ViewBinding getBinding();
    }


    public static class UserPromptBindingWrapper implements ILayoutTopIcon {
        private final ItemGptUserPromptBinding binding;

        public UserPromptBindingWrapper(ItemGptUserPromptBinding binding) {
            this.binding = binding;
        }

        @Override
        public ImageButton getIbVocaList() {
            return binding.layoutTopIcon.ibVocaList;
        }

        @Override
        public ImageButton getIbSpeaker() {
            return binding.layoutTopIcon.ibSpeaker;
        }

        @Override
        public ImageButton getIbCopy() {
            return binding.layoutTopIcon.ibCopy;
        }

        @Override
        public ImageView getIvTranslate() {
            return binding.layoutTopIcon.ivTranslate;
        }
        @Override
        public ImageView getIvGpt() {
            return binding.layoutTopIcon.ivGptIcon;
        }

        @Override
        public ImageView getIvBookmark() {
            return binding.layoutTopIcon.ivBookmark;
        }

        @Override
        public LinearLayout getLlKnowValue() {
            return binding.layoutTopIcon.llKnowValue;
        }

        @Override
        public ImageView getIvKnow() {
            return binding.layoutTopIcon.ivKnow;
        }

        @Override
        public ImageView getIvKnowPronounce() {
            return binding.layoutTopIcon.ivKnowPronounce;
        }

        @Override
        public TextView getTvChatBubble() {
            return binding.tvUserPrompt;
        }

        @Override
        public ItemGptUserPromptBinding getBinding() {
            return binding;
        }
    }

    public static class ResponseBindingWrapper implements ILayoutTopIcon {
        private final ItemGptResponseBinding binding;

        public ResponseBindingWrapper(ItemGptResponseBinding binding) {
            this.binding = binding;
        }

        @Override
        public ImageButton getIbVocaList() {
            return binding.layoutTopIcon.ibVocaList;
        }

        @Override
        public ImageButton getIbSpeaker() {
            return binding.layoutTopIcon.ibSpeaker;
        }

        @Override
        public ImageButton getIbCopy() {
            return binding.layoutTopIcon.ibCopy;
        }

        @Override
        public ImageView getIvTranslate() {
            return binding.layoutTopIcon.ivTranslate;
        }
        @Override
        public ImageView getIvGpt() {
            return binding.layoutTopIcon.ivGptIcon;
        }
        @Override
        public ImageView getIvBookmark() {
            return binding.layoutTopIcon.ivBookmark;
        }

        @Override
        public LinearLayout getLlKnowValue() {
            return binding.layoutTopIcon.llKnowValue;
        }

        @Override
        public ImageView getIvKnow() {
            return binding.layoutTopIcon.ivKnow;
        }

        @Override
        public ImageView getIvKnowPronounce() {
            return binding.layoutTopIcon.ivKnowPronounce;
        }
        @Override
        public TextView getTvChatBubble() {
            return binding.tvGptResponse;
        }

        @Override
        public ItemGptResponseBinding getBinding() {
            return binding;
        }
    }

    private static class UserPromptViewHolder extends BaseViewHolder<UserPromptBindingWrapper> {
        public UserPromptViewHolder(UserPromptBindingWrapper binding) {
            super(binding);
        }

        @Override
        public void bind(ConversationModel model, int position) {
            super.bind(model, position);
        }
        protected void displayPrompt(ConversationModel model) {
            super.displayPrompt(model);
            if (isShowVocaOrMeaningOnly()) {
                String messageContent = model.getMESSAGE_CONTENT();
                binding.getTvChatBubble().setText(messageContent);
            }
        }
    }

    private static class ResponseViewHolder extends BaseViewHolder<ResponseBindingWrapper> {
        public ResponseViewHolder(ResponseBindingWrapper binding) {
            super(binding);
        }

        @Override
        public void bind(ConversationModel model, int position) {
            super.bind(model, position);
        }
        protected void displayPrompt(ConversationModel model) {
            super.displayPrompt(model);
            if (isShowVocaOrMeaningOnly()) {
                String messageContent = model.getMESSAGE_TRANSLATION();
                binding.getTvChatBubble().setText(messageContent);
            }
        }
    }

    private static class AppPromptViewHolder extends RecyclerView.ViewHolder {
        private ItemGptAppPromptBinding binding;

        public AppPromptViewHolder(ItemGptAppPromptBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ConversationModel model) {
            binding.tvAppPrompt.setText(model.getMESSAGE_CONTENT());
        }
    }
}


