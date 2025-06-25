package com.dalread.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dalread.R;
import com.dalread.component.Toolbar;
import com.dalread.helper.PlayVocaHelper;
import com.dalread.interfaces.IVocaFullPlayTTSItem;

import java.util.List;

//Dalnim, Is this needed in AraHanja? (Main package needs this, but in AraHanja where it is used?)
public class WordInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_word_info);
    }

    public void stopPlayVoca() {
    }

    public PlayVocaHelper getPlayVocaHelper() {
        return null;
    }

    public Toolbar getToolbar() {
        return null;
    }

    public void preparePlayStudentVoice(IVocaFullPlayTTSItem item) {
    }

    public void preparePlayStudentVoice(List<IVocaFullPlayTTSItem> items) {
    }

    public void clearDownloadList() {
    }
}
