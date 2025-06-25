package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.WebDictionaryAdapter;
import com.dalread.base.BaseActivity;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.databinding.ActivityPlayerWebDictionaryBinding;
import com.dalread.dialog.WebDictionaryInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.WebDictionaryModel;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseVoca;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebDictionaryActivity extends BaseActivity {
    private WebDictionaryAdapter adapter;
    private List<WebDictionaryModel> list = new ArrayList<>();

    private ActivityPlayerWebDictionaryBinding binding;

    @Override
    protected View getContentView() {
        binding = ActivityPlayerWebDictionaryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    public void initView() {
        adapter = new WebDictionaryAdapter(this, binding.rvList, adapterClickListener);
        binding.rvList.setOnItemMoveListener(onItemMoveListener);
        binding.rvList.setLayoutManager(new CenterLayoutManager(this));
        binding.rvList.addItemDecoration(new SeparatorDecoration(context, ContextCompat.getColor(context, R.color.backgroundDialogBorderColor), BaseBindUtils.getDividerHeight(this)));
//        binding.rvList.addItemDecoration(new SeparatorDecoration(this, clDivider, dividerHeight));
        binding.rvList.setAdapter(adapter);

    }

    public void initData() {
        list = WebDictionaryQuery.getAll(BaseVoca.getRealm(), LanguageUtil.getStudyLanguageCode(this), LanguageUtil.getMotherTongueLanguageCode(this));
        binding.rvList.setItemViewSwipeEnabled(list.size() > 1);
        adapter.notifyDataSetChanged(list);
        if (Utils.isEmpty(list)) {
            showWebDictionaryDialog();
            //Don't delete this
            //showWarningWebDictionary();
        }
    }

    @Override
    public void onHeaderLeftClick() {
        finish();
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
        showWebDictionaryDialog();
    }

    private void showWebDictionaryDialog() {
        showWebDictionaryDialog(null);
    }

    private void showWebDictionaryDialog(WebDictionaryModel item) {
        final WebDictionaryInputDialog dialog = new WebDictionaryInputDialog(this, item, (view, object) -> {
            final WebDictionaryModel newItem = (WebDictionaryModel) object;
            if (item == null) {
                newItem.setIndex(WebDictionaryQuery.createIndex(BaseVoca.getRealm()));
                newItem.setStudyLanguage(LanguageUtil.getStudyLanguageCode(this));
                newItem.setMotherTongue(LanguageUtil.getMotherTongueLanguageCode(this));
            }
            WebDictionaryQuery.add(BaseVoca.getRealm(), newItem);
            initData();
        });
        dialog.show();
    }

    private OnClickListener adapterClickListener = (view, object) -> {
        showWebDictionaryDialog((WebDictionaryModel) object);
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
            if (viewHolder.getItemViewType() != viewHolder1.getItemViewType()) return false;

            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。
            int fromPosition = viewHolder.getAdapterPosition() - binding.rvList.getHeaderCount();
            int toPosition = viewHolder1.getAdapterPosition() - binding.rvList.getHeaderCount();

            swapPositionData(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            showWarningDeleteWebDictionary(position);
        }
    };

    private void swapPositionData(int fromPosition, int toPosition) {
        runOnUiThread(() -> {
            Loading.show(this);
            Collections.swap(list, fromPosition, toPosition);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setIndex(i);
                WebDictionaryQuery.update(BaseVoca.getRealm(), list.get(i));
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            Loading.hide();
        });
    }

    private void showWarningDeleteWebDictionary(int position) {
        final YesNoDialog dialog = new YesNoDialog(this, R.string.warning, R.string.msg_web_dictionary_delete, position, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                WebDictionaryQuery.deleteById(BaseVoca.getRealm(), list.get(position).getId());
                list.remove(position);
                adapter.notifyItemRemoved(position);
                binding.rvList.setItemViewSwipeEnabled(list.size() > 1);
            }

            @Override
            public void onNoClick(View view, Object object) {
                adapter.notifyItemChanged(position);
            }
        });
        dialog.show();
    }

    //Don't delete this. May use it later.
//    private void showWarningWebDictionary() {
//        final YesNoDialog dialog = new YesNoDialog(this,
//                R.string.warning,
//                R.string.msg_warning_web_dictionary_is_empty, null,
//                new OnYesNoClickListener() {
//            @Override
//            public void onYesClick(View view, Object object) {
//                showWebDictionaryDialog();
//            }
//
//            @Override
//            public void onNoClick(View view, Object object) {
//
//            }
//        });
//        dialog.show();
//    }
}
