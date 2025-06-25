package com.dalread.dialog;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dalread.adapter.VocaPopupAdapter;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.databinding.VocaBottomSheetDialogBinding;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.util.ResourceUtils;

import java.util.List;

//리싸이클뷰만 있으면 상단에 코너가 안보인다. 그래서 이미지뷰나 텍스트뷰등을 상단에 줘야 한다.
public class VocaBottomSheetDialog extends BaseBottomSheetDialog<VocaBottomSheetDialogBinding> {
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private boolean show4Buttons;
    private List<IVocaFullPlayTTSItem> vocaList;
    private VocaPopupAdapter adapter;
    public VocaBottomSheetDialog(Context context, VocaBottomSheetDialogBinding binding, List<IVocaFullPlayTTSItem> vocaList, OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow, boolean show4Buttons) {
        super(context, binding);
        setContentView(binding.getRoot());
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
        this.show4Buttons = show4Buttons;
        this.vocaList = vocaList;
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
        setVocaList();
    }

    public VocaPopupAdapter getAdapter() {
        return adapter;
    }

    private void setVocaList() {
        adapter = new VocaPopupAdapter(context, vocaList, onDoubleClickListenerOnBaseVocaKnow, show4Buttons);
        binding.popupRecyclerview.setAdapter(adapter);
        binding.popupRecyclerview.setLayoutManager(new LinearLayoutManager(context));
        binding.popupRecyclerview.addItemDecoration(new SeparatorDecoration(context, ResourceUtils.getDefaultCellBottomColor(context), ResourceUtils.getDefaultCellDimension(context)));
    }
}
