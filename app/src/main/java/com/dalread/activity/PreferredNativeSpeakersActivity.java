package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.PreferredNativeSpeakersAdapter;
import com.dalread.base.VocaActivity;
import com.dalread.databinding.ActivityPreferredNativeSpeakersBinding;
import com.dalread.helper.itemtouchhelper.ItemTouchHelperCallback;
import com.dalread.listener.OnDragListener;
import com.dalread.model.NativeSpeaker;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferredNativeSpeakersActivity extends VocaActivity {

//    @BindView(R.id.rv_speaker)
//    RecyclerView rvSpeaker;
//    @BindColor(R.color.color_divider)
//    int clDivider;
//    @BindDimen(R.dimen.divider_height)
//    float dividerHeight;

    private Context context;
    private ArrayList<NativeSpeaker> allNativeSpeakers = new ArrayList<>();
    private ArrayList<NativeSpeaker> preferredNativeSpeakers = new ArrayList<>();
    private PreferredNativeSpeakersAdapter adapter;
    private RecyclerTouchListener recyclerTouchListener;

    private ActivityPreferredNativeSpeakersBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPreferredNativeSpeakersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initRecyclerView();
        initData();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        addOnItemTouchListener();
    }

    @Override
    protected void onPause() {
        removeOnItemTouchListener();

        super.onPause();
    }

    private void initRecyclerView() {
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelperCallback(adapter = new PreferredNativeSpeakersAdapter(allNativeSpeakers, preferredNativeSpeakers))
        );
        itemTouchHelper.attachToRecyclerView(binding.rvSpeaker);
        adapter.setDragListener(new OnDragListener() {

            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onDragStopped() {
                changePreferredNativeSpeakersServerSide();
            }
        });
        adapter.setClickListener((view, object) -> {
            if (object instanceof NativeSpeaker) {
                NativeSpeaker nativeSpeaker = (NativeSpeaker) object;
                int id = view.getId();
                switch (id) {
                    case R.id.v_container:
                        changePreferredNativeSpeakersClientSide(nativeSpeaker);
                        break;
                    case R.id.ic_avatar:
                    case R.id.ll_username_desc:
                        openProfileView(nativeSpeaker.getNativeSpeakerId());
                        break;
                    default:
                        break;
                }
            }
        });
        binding.rvSpeaker.setHasFixedSize(true);
        binding.rvSpeaker.setAdapter(adapter);
        binding.rvSpeaker.setLayoutManager(new LinearLayoutManager(context));
        binding.rvSpeaker.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        recyclerTouchListener = new RecyclerTouchListener(this, binding.rvSpeaker)
                .setSwipeOptionViews(R.id.tv_delete)
                .setSwipeable(R.id.v_foreground, R.id.v_background, (viewId, position) -> {
                    NativeSpeaker speaker = adapter.getNativeSpeaker(position);
                    if (speaker != null) {
                        changePreferredNativeSpeakersClientSide(speaker);
                    }
                });
    }

    private void initData() {
        int uid = getUserID();
        if (uid > 0 && Utils.isConnected(context)) {
            Loading.show(context);
            application.getDalAiImpl().getAllNativeSpeakers(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    new DalApiListener<List<NativeSpeaker>>() {

                        @Override
                        public void onSuccess(List<NativeSpeaker> response) {
                            for (NativeSpeaker nativeSpeaker : response) {
                                allNativeSpeakers.add(nativeSpeaker);
                                if (nativeSpeaker.isPreferred()) {
                                    preferredNativeSpeakers.add(nativeSpeaker);
                                }
                            }
                            if (!preferredNativeSpeakers.isEmpty()) {
                                Collections.sort(preferredNativeSpeakers, (o1, o2) -> o1.getRank() - o2.getRank());
                            }
                            adapter.notifyDataSetChanged();
                            setUnswipeableRows();
                            Loading.hide();
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        }
    }

    private void setUnswipeableRows() {
        ArrayList<Integer> unswipeableRows = new ArrayList<>();
        int count = adapter.getItemCount();
        for (int i = 0; i < count; i++) {
            if (!adapter.isSwipeable(i)) {
                unswipeableRows.add(i);
            }
        }
        recyclerTouchListener.setUnSwipeableRows(unswipeableRows.toArray(new Integer[0]));
    }

    private void addOnItemTouchListener() {
        binding.rvSpeaker.addOnItemTouchListener(recyclerTouchListener);
    }

    private void removeOnItemTouchListener() {
        binding.rvSpeaker.removeOnItemTouchListener(recyclerTouchListener);
    }

    private void changePreferredNativeSpeakersClientSide(NativeSpeaker speaker) {
        boolean preferred = !speaker.isPreferred();
        speaker.setPreferred(preferred);
        if (preferred) {
            preferredNativeSpeakers.add(speaker);
            adapter.notifyItemInserted(preferredNativeSpeakers.size());
        } else {
            int pos = preferredNativeSpeakers.indexOf(speaker);
            if (pos >= 0) {
                preferredNativeSpeakers.remove(pos);
                adapter.notifyItemRemoved(pos + 1);
            }
        }
        int pos = allNativeSpeakers.indexOf(speaker);
        if (pos >= 0) {
            adapter.notifyItemChanged(pos + preferredNativeSpeakers.size() + 2);
        }
        changePreferredNativeSpeakersServerSide();
    }

    private void changePreferredNativeSpeakersServerSide() {
        int uid = getUserID();
        if (uid > 0 && Utils.isConnected(context)) {
            StringBuilder speakerIds = new StringBuilder();
            int count = 0;
            for (NativeSpeaker speaker : preferredNativeSpeakers) {
                speakerIds.append(",").append(speaker.getNativeSpeakerId());
                count++;
            }
            if (speakerIds.length() > 0) {
                speakerIds = new StringBuilder(speakerIds.substring(1));
            }
            application.getDalAiImpl().changePreferredNativeSpeakers(
                    String.valueOf(uid),
                    sharedPreferences.getLangStudyCode(),
                    speakerIds.toString()
            );
            sharedPreferences.setPreferredNativeSpeakers(speakerIds.toString());
            sharedPreferences.setPreferredNativeSpeakersCount(count);
        }
    }

    private void openProfileView(int uid) {
        int myUid = sharedPreferences.getRealUid();
        if (uid == myUid) {
            openNewScreen(MyProfileActivity.class);
        } else {
            Intent intent = new Intent(context, OtherProfileActivity.class);
            intent.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, uid);
            openNewScreen(intent);
        }
    }
}
