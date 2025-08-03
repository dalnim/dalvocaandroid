package com.dalread.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.TrackSelectionView;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Tracks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;

/** Fragment to show a track selection in tab of the track selection dialog. */
public final class TrackSelectionViewFragment extends Fragment implements TrackSelectionView.TrackSelectionListener {

    private MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
    private int rendererIndex;
    private boolean allowAdaptiveSelections;
    private boolean allowMultipleOverrides;

    /* package */ boolean isDisabled;
    /* package */ List<DefaultTrackSelector.SelectionOverride> overrides;

    public TrackSelectionViewFragment() {
        // Retain instance across activity re-creation to prevent losing access to init data.
        setRetainInstance(true);
    }

    public void init(
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            int rendererIndex,
            boolean initialIsDisabled,
            @Nullable DefaultTrackSelector.SelectionOverride initialOverride,
            boolean allowAdaptiveSelections,
            boolean allowMultipleOverrides) {
        this.mappedTrackInfo = mappedTrackInfo;
        this.rendererIndex = rendererIndex;
        this.isDisabled = initialIsDisabled;
        this.overrides = initialOverride == null ? Collections.emptyList() : Collections.singletonList(initialOverride);
        this.allowAdaptiveSelections = allowAdaptiveSelections;
        this.allowMultipleOverrides = allowMultipleOverrides;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exo_track_selection_dialog, container, /* attachToRoot= */ false);

        //이건 기존 코드.
//        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
//        trackSelectionView.setShowDisableOption(true);
//        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
//        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
//        trackSelectionView.init(mappedTrackInfo, rendererIndex, isDisabled, overrides, null, this);



        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setShowDisableOption(true);
        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
        
        // ExoPlayer 2.19.1의 새로운 API 사용
        // TrackGroupArray를 List<Tracks.Group>으로 변환
        TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
        List<Tracks.Group> trackGroups = new ArrayList<>();
        for (int i = 0; i < trackGroupArray.length; i++) {
            TrackGroup trackGroup = trackGroupArray.get(i);
            // TrackGroup을 Tracks.Group으로 변환
            boolean[] trackSupport = new boolean[trackGroup.length];
            for (int j = 0; j < trackGroup.length; j++) {
                trackSupport[j] = true; // 모든 트랙을 지원한다고 가정
            }
            trackGroups.add(new Tracks.Group(trackGroup, false, new int[0], trackSupport));
        }
        Map<TrackGroup, TrackSelectionOverride> currentOverrides = Collections.emptyMap(); // TODO: 기존 overrides를 변환
        Comparator<Format> formatComparator = null; // 기본 정렬 사용
        
        trackSelectionView.init(
            trackGroups,
            isDisabled,
            currentOverrides,
            formatComparator,
            this
        );
        
        return rootView;
    }

    @Override
    public void onTrackSelectionChanged(boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> overrides) {
        this.isDisabled = isDisabled;
        // 새로운 TrackSelectionOverride를 기존 SelectionOverride로 변환
        this.overrides = Collections.emptyList(); // TODO: 필요시 변환 로직 추가
    }
}