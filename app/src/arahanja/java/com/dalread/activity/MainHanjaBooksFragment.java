package com.dalread.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.BillingFlowParams;
import com.dalread.R;
import com.dalread.adapter.ClassicsAdapter;
import com.dalread.base.BaseHanjaFragment;
import com.dalread.component.SeparatorDecoration;
import com.dalread.databinding.FragmentClassicsBinding;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.AraHanjaBillingClientHelper;
import com.dalread.helper.BillingClientHelper;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnToolbarLeftButtonChangeListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.VOCABOOKS_HANJA_CLASSICS;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AraHanjaInAppProductsClass;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.PurchasedBooksUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;

public class MainHanjaBooksFragment extends BaseHanjaFragment {
    private VOCABOOKS_HANJA_CLASSICS parentBook;
    private List<VOCABOOKS_HANJA_CLASSICS> books;
    private ClassicsAdapter adapter;
    private FragmentClassicsBinding binding;
    private VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics;

    @Override
    protected View getContentView() {
        binding = FragmentClassicsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initData();
        initLayout();
        bindData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof OnToolbarLeftButtonChangeListener) {
            if (parentBook == null) {
                ((OnToolbarLeftButtonChangeListener) getActivity()).onChangeToMenu();
            } else {
                ((OnToolbarLeftButtonChangeListener) getActivity()).onChangeToBack();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void initData() {
        parentBook = (VOCABOOKS_HANJA_CLASSICS) getArguments().getSerializable(Constant.BUNDLE.KEY_VOCA_BOOK);
        BaseVoca.executeRealmTransaction(realm -> {
            RealmResults<VOCABOOKS_HANJA_CLASSICS> realmResults = realm.where(VOCABOOKS_HANJA_CLASSICS.class)
                    .equalTo(Constant.REALMDB.KEY_PARENT_ID, parentBook == null ? 0 : parentBook.getID())
                    .greaterThanOrEqualTo(Constant.REALMDB.KEY_USED, UserUtil.getVocabooksUsedByUserType(getActivity()))
                    .sort(Constant.REALMDB.KEY_DISP_ORDER, Sort.ASCENDING, Constant.REALMDB.KEY_NAME_KO, Sort.ASCENDING)
                    .findAll();
            books = realm.copyFromRealm(realmResults);
        });
    }

    private void initLayout() {
        adapter = new ClassicsAdapter(getActivity(), onBookClickListener);
        binding.rvBooks.setAdapter(adapter);
        binding.rvBooks.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvBooks.addItemDecoration(new SeparatorDecoration(getActivity(), ContextCompat.getColor(getActivity(), R.color.color_divider), getActivity().getResources().getDimension(R.dimen.divider_height)));
    }

    private void bindData() {
        if (books != null) {
            PurchasedBooksUtil.updateBooks(books, getContext());
            adapter.setBooks(books);
            adapter.notifyDataSetChanged();
        }
    }

    private final OnClickListener onBookClickListener = (view, object) -> {
        if (object instanceof VOCABOOKS_HANJA_CLASSICS) {
            vocabooksHanjaClassics = (VOCABOOKS_HANJA_CLASSICS) object;
            switch (view.getId()) {
                case R.id.v_item:
                   if (vocabooksHanjaClassics.getHAS_SUB_LIST() == 1) {
                        openChildBook(vocabooksHanjaClassics);
                    } else {
                       checkToOpenHanjaBookContent(vocabooksHanjaClassics);
                    }

                    break;
                case R.id.ivInformation:
                    openHanjaBookInformationScreen(vocabooksHanjaClassics);
                    break;
            }
        }
    };

    private void openHanjaBookInformationScreen(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        openNewScreen(
                HanjaBookInfoActivity.createIntent(getContext(), vocabooksHanjaClassics)
        );
    }

    private void openChildBook(VOCABOOKS_HANJA_CLASSICS parentBook) {
        if (getActivity() instanceof AppCompatActivity) {
            Fragment fragment = new MainHanjaBooksFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.BUNDLE.KEY_VOCA_BOOK, parentBook);
            fragment.setArguments(bundle);
            Utils.loadFragment((AppCompatActivity) getActivity(), fragment, getFragmentContainerId(), true, getLogTag() + parentBook.getID());
        }
    }


    private void checkToOpenHanjaBookContent(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        if (isNeedToPurchase(vocabooksHanjaClassics)) {
            askToPurchaseBookContent(vocabooksHanjaClassics);
            return;
        }
        openHanjaBookContent(vocabooksHanjaClassics);
    }

    private void askToPurchaseBookContent(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        final YesNoDialog dialog = new YesNoDialog(getActivity(), R.string.warning, getString(R.string.msg_warning_buy_inapp_book), null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                BillingClientHelper billingClientHelper = AraHanjaBillingClientHelper.getInstance(getActivity());
                List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = billingClientHelper.getSpecificProductDetailsParams();
                for (BillingFlowParams.ProductDetailsParams productDetailsParams : productDetailsParamsList) {
                    if (productDetailsParams.zza().getProductId().equals(AraHanjaInAppProductsClass.book_thousand_character_classic)) {
                        billingClientHelper.handleSelectedInApp(getActivity(), productDetailsParams);
                    }
                }
            }

            @Override
            public void onNoClick(View view, Object object) {
                openHanjaBookContent(vocabooksHanjaClassics);
            }
        });
        dialog.show();
    }

    private void openHanjaBookContent(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        if (vocabooksHanjaClassics == null) {
            return;
        }
        openNewScreen(
                HanjaBookContentActivity.createIntent(getContext(), vocabooksHanjaClassics)
        );
//
//        Intent intent = new Intent(getActivity(), HanjaBookContentActivity.class);
//        intent.putExtra(Constant.BUNDLE.KEY_VOCA_BOOK, vocabooksHanjaClassics);
//        startActivity(intent);
    }

    private boolean isNeedToPurchase(VOCABOOKS_HANJA_CLASSICS vocabooksHanjaClassics) {
        if (vocabooksHanjaClassics.getID() == PurchasedBooksUtil.bookIdOfThousandCharacter) {
            if (PurchasedBooksUtil.isPurchasedThousandCharacter(sharedPreferences)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        switch (successEvent.getEventType()) {
            case PURCHASED_IN_APP_CLASSIC:
                bindData();
                openHanjaBookContent(vocabooksHanjaClassics);
                break;
        }
    }
}
