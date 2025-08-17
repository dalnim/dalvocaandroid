package com.dalread.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.util.DLog;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.TrackSelectionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exo_track_selection_dialog, container, /* attachToRoot= */ false);

        //이건 기존 코드.
        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setShowDisableOption(true);
        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
//        trackSelectionView.init(mappedTrackInfo, rendererIndex, isDisabled, overrides, null, this);



//        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
//        trackSelectionView.setShowDisableOption(true);
//        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
//        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);
        
        // ExoPlayer 2.19.1에서 안정적인 방식 사용
        try {
            // 새로운 API 시도
            TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
            List<Tracks.Group> trackGroups = new ArrayList<>();
            
            for (int i = 0; i < trackGroupArray.length; i++) {
                TrackGroup trackGroup = trackGroupArray.get(i);
                
                // trackGroup이 null이거나 길이가 0인 경우 건너뛰기
                if (trackGroup == null || trackGroup.length == 0) {
                    continue;
                }
                
                // trackSupport 배열을 정확한 길이로 생성
                boolean[] trackSupport = new boolean[trackGroup.length];
                int[] adaptiveTrackIndices = new int[0]; // 적응형 트랙 없음
                
                for (int j = 0; j < trackGroup.length; j++) {
                    trackSupport[j] = true; // 모든 트랙을 지원한다고 설정
                }
                
                try {
                    // Tracks.Group 생성 시 안전성 검사
                    Tracks.Group group = new Tracks.Group(trackGroup, false, adaptiveTrackIndices, trackSupport);
                    trackGroups.add(group);
                } catch (IllegalArgumentException e) {
                    DLog.e("TrackSelection", "Invalid track group at index " + i + ": " + e.getMessage());
                    continue; // 문제가 있는 그룹은 건너뛰기
                }
            }
            
            // 유효한 트랙 그룹이 있는 경우에만 초기화
            if (!trackGroups.isEmpty()) {
                trackSelectionView.init(
                    trackGroups,
                    isDisabled,
                    Collections.emptyMap(),
                    null,
                    this
                );
            } else {
                // 유효한 트랙이 없는 경우 빈 리스트로 초기화
                trackSelectionView.init(
                    Collections.emptyList(),
                    isDisabled,
                    Collections.emptyMap(),
                    null,
                    this
                );
            }
        } catch (Exception e) {
            DLog.e("TrackSelection", "Error initializing track selection: " + e.toString());
            // 에러 발생 시 가장 안전한 기본값으로 초기화
            trackSelectionView.init(
                Collections.emptyList(),
                isDisabled,
                Collections.emptyMap(),
                null,
                this
            );
        }
        
        return rootView;
    }

    @Override
    public void onTrackSelectionChanged(boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> overrides) {
        this.isDisabled = isDisabled;
        // 새로운 TrackSelectionOverride를 기존 SelectionOverride로 변환
        this.overrides = Collections.emptyList(); // TODO: 필요시 변환 로직 추가
    }
}