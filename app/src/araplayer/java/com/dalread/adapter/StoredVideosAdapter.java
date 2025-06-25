package com.dalread.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.dalread.R;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.databinding.ItemPlayerScreenStoredLayoutBinding;
import com.dalread.listener.OnClickListener;
import com.dalread.util.AraThemeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoredVideosAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private Context context;
    private OnClickListener listener;
    private List<List<MultiPlayerVideoStoredModel>> list = new ArrayList<>();

    public StoredVideosAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(ItemPlayerScreenStoredLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        return viewHolder ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((ViewHolder)viewHolder).bindData(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setData(List<MultiPlayerVideoStoredModel> playerFileModels) {
        this.list = groupByStoredId(playerFileModels);
        notifyDataSetChanged();
    }

    //리스트를 stored id별로 다시 내부 리스트로 묶는다. Stored Id의 역순으로 묶어서 최신께 맨위로 오게 한다. (헌번에 수 개의 비디오들을 로드할수 있으므로)
    public List<List<MultiPlayerVideoStoredModel>> groupByStoredId(List<MultiPlayerVideoStoredModel> list) {
        // Map에 storedId를 기준으로 그룹화
        Map<Integer, List<MultiPlayerVideoStoredModel>> groupedVideos = new HashMap<>();
        for (MultiPlayerVideoStoredModel model : list) {
            int storedId = model.getSTORED_ID();
            List<MultiPlayerVideoStoredModel> modelsWithSameId = groupedVideos.computeIfAbsent(storedId, k -> new ArrayList<>());
            modelsWithSameId.add(model);
        }

        // Map의 entries를 List로 변환하고 storedId를 기준으로 역순 정렬
        List<Map.Entry<Integer, List<MultiPlayerVideoStoredModel>>> entries = new ArrayList<>(groupedVideos.entrySet());
        entries.sort((e1, e2) -> Integer.compare(e2.getKey(), e1.getKey()));

        // 정렬된 결과를 List<List<MultiPlayerVideoStoredModel>>로 변환
        List<List<MultiPlayerVideoStoredModel>> sortedGroupedVideos = new ArrayList<>();
        for (Map.Entry<Integer, List<MultiPlayerVideoStoredModel>> entry : entries) {
            sortedGroupedVideos.add(entry.getValue());
        }

        return sortedGroupedVideos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private List<MultiPlayerVideoStoredModel> list;
        private int storedId = -1;
        private boolean isScrolling = false; // 스크롤 상태를 추적할 변수
        private ItemPlayerScreenStoredLayoutBinding binding;

        public ViewHolder(ItemPlayerScreenStoredLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setOnClickListeners();
            initColor();
        }
        private void setOnClickListeners() {
            binding.root.setOnClickListener(this);
            binding.llItem.setOnClickListener(this);
            binding.rvVideoSeasonList.setOnClickListener(this);
            binding.ivEdit.setOnClickListener(this);
        }
        private void initColor() {
            binding.getRoot().setCardBackgroundColor(ContextCompat.getColor(context, R.color.multiPlayerBackgroundBlackColor));
            AraThemeUtil.setBackgroundColor(context, binding.llItem, R.color.multiPlayerBackgroundBlackColor);
            ColorStateList colorStateList = context.getResources().getColorStateList(R.color.iconPrimaryWhiteColor, context.getTheme());
            AraThemeUtil.setTextColor(context, binding.tvTitle, R.color.textPrimaryWhiteColor);
            AraThemeUtil.setBackgroundColor(context, binding.tvTitle, R.color.multiPlayerBackgroundBlackColor);
        }
        public void bindData(List<MultiPlayerVideoStoredModel> list, int position) {
            this.list = list;
            if (list.isEmpty()) {
                return;
            }
            //일단 타이틀과 ivEdit은 보여주지 말자.
            binding.tvTitle.setVisibility(View.GONE);

            storedId = list.get(0).getSTORED_ID();
            binding.tvTitle.setText(list.get(0).getLAYOUT_NAME());
            binding.rvVideoSeasonList.setAdapter(null);
            binding.rvVideoSeasonList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            StoredVideoScreenAdapter videoSeasonListAdapter = new StoredVideoScreenAdapter(context, new OnClickListener() {
                @Override
                public void onClick(View view, Object object) {
                    //이건 StoredVideoScreenAdapter내의 쉘 한개를 클릭했을때.
                    ViewHolder.this.onClick(view);
                }
            });
            binding.rvVideoSeasonList.setAdapter(videoSeasonListAdapter);
            binding.rvVideoSeasonList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    isScrolling = true;
                }
            });
            binding.rvVideoSeasonList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isScrolling = false;
                            break;
                        case MotionEvent.ACTION_UP:
                            // 스크롤이 발생하지 않았으면 true 리턴 (탭으로 간주)
                            if (!isScrolling) {
                                isScrolling = false;
                                //이건 rvVideoSeasonList를 클릭했을때(비디오 쉘이 몇개 안되어서 리사이클 뷰 본체를 누를때)
                                ViewHolder.this.onClick(rv);
                                return true;
                            }
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
            videoSeasonListAdapter.setData(list);
            binding.rvVideoSeasonList.setVisibility(View.VISIBLE);
        }
        @Override
        public void onClick(View v) {
            if (listener != null && !list.isEmpty()) {
                listener.onClick(v, list);
            }
        }
    }
}
