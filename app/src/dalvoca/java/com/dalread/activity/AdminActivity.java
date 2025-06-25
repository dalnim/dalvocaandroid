package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.OnClick;
import io.realm.Realm;

import static com.dalread.network.events.BaseEvent.Screen;

public class AdminActivity extends BaseVocaActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_admin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initEventBus();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();

        super.onDestroy();
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

        Intent intent = getIntent();
        final int lessonType = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
        intent.removeExtra(Constant.BUNDLE.KEY_LESSON_TYPE);
        ReceiveCallModel receiveCallModel = (ReceiveCallModel) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOICE_DATA);
        intent.removeExtra(Constant.BUNDLE.KEY_VOICE_DATA);
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
            if (toolbar != null) {
                toolbar.postDelayed(() -> openLessonListScreen(receiveCallModel), Constant.ON_RESUME_DELAY);
            }
        }
    }

    @OnClick({R.id.v_lesson_admin, R.id.v_realm_db, R.id.v_ruby_table, R.id.v_ruby_text_view, R.id.v_unicode})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_lesson_admin:
                openLessonListScreen();
                break;
            case R.id.v_realm_db:
                copyRealmDB();
                break;
            case R.id.v_ruby_table:
                openRubyScreen(Constant.RUBY.TYPE.TABLE);
                break;
            case R.id.v_ruby_text_view:
                openRubyScreen(Constant.RUBY.TYPE.TEXT);
                break;
            case R.id.v_unicode:
                openUnicodeScreen();
                break;
        }

    }

    private void openLessonListScreen() {
        openLessonListScreen(null);
    }

    private void openLessonListScreen(ReceiveCallModel receiveCallModel) {
        Intent i = new Intent(this, LessonListActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_ADMIN);
        if (receiveCallModel != null) {
            i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, Utils.parseInt(receiveCallModel.getLessonId()));
            i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        }
        openNewScreen(i);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyRealmDB() {
        File folder = Voca.getTempFolderOnLocal(this);
        if (folder == null) {
            ToastUtil.getInstance(this).show("Get temp folder failed");
        } else {
            try (Realm realm = Realm.getDefaultInstance()) {
                File file = new File(folder, Realm.DEFAULT_REALM_NAME);
                if (file.exists()) {
                    file.delete();
                }
                realm.writeCopyTo(file);
                ToastUtil.getInstance(this).show("Copy Realm file successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openRubyScreen(int type) {
        Intent i = new Intent(this, RubyActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_RUBY_TYPE, type);
        openNewScreen(i);
    }

    private void openUnicodeScreen() {
        openNewScreen(UnicodeActivity.class);
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == Screen.ADMIN_ACTIVITY) {
            switch (successEvent.getEventType()) {
                case EXIT:
                    finish();
                    break;
                case ADMIN_API:
                    openLessonListScreen((ReceiveCallModel) successEvent.getModel());
                    break;
            }
        }
    }
}
