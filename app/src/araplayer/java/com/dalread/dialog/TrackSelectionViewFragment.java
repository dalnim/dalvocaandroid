package com.dalread.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride;
import com.google.android.exoplayer2.ui.TrackSelectionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fragment to show a track selection in tab of the track selection dialog. */
public final class TrackSelectionViewFragment extends Fragment implements TrackSelectionView.TrackSelectionListener {

    private Tracks tracks;
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
            Tracks tracks,
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
            int rendererIndex,
            boolean initialIsDisabled,
            @Nullable DefaultTrackSelector.SelectionOverride initialOverride,
            boolean allowAdaptiveSelections,
            boolean allowMultipleOverrides) {
        this.tracks = tracks;
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
        TrackSelectionView trackSelectionView = rootView.findViewById(R.id.exo_track_selection_view);
        trackSelectionView.setShowDisableOption(true);
        trackSelectionView.setAllowMultipleOverrides(allowMultipleOverrides);
        trackSelectionView.setAllowAdaptiveSelections(allowAdaptiveSelections);

        Map<TrackGroup, TrackSelectionOverride> newOverrides = new HashMap<>();
        if (overrides != null && !overrides.isEmpty()) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            for (DefaultTrackSelector.SelectionOverride override : overrides) {
                if (override != null) {
                    TrackGroup trackGroup = trackGroups.get(override.groupIndex);
                    List<Integer> trackList = new ArrayList<>();
                    for (int track : override.tracks) {
                        trackList.add(track);
                    }
                    newOverrides.put(trackGroup, new TrackSelectionOverride(trackGroup, trackList));
                }
            }
        }

        List<Tracks.Group> groups = new ArrayList<>();
        for (Tracks.Group group : tracks.getGroups()) {
            if (group.getType() == mappedTrackInfo.getRendererType(rendererIndex)) {
                groups.add(group);
            }
        }

        trackSelectionView.init(groups, isDisabled, newOverrides, null, this);
        return rootView;
    }

    @Override
    public void onTrackSelectionChanged(boolean isDisabled, Map<TrackGroup, TrackSelectionOverride> newOverrides) {
        this.isDisabled = isDisabled;
        this.overrides = new ArrayList<>();
        if (mappedTrackInfo != null) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            for (TrackSelectionOverride override : newOverrides.values()) {
                int groupIndex = trackGroups.indexOf(override.mediaTrackGroup);
                if (groupIndex != -1) {
                    int[] trackIndices = new int[override.trackIndices.size()];
                    for (int i = 0; i < override.trackIndices.size(); i++) {
                        trackIndices[i] = override.trackIndices.get(i);
                    }
                    this.overrides.add(new DefaultTrackSelector.SelectionOverride(groupIndex, trackIndices));
                }
            }
        }
    }
}