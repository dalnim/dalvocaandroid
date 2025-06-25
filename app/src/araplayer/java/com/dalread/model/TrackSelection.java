package com.dalread.model;

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride;

import java.util.List;

public class TrackSelection {
    private int trackType;
    private boolean isDisable;
    private List<SelectionOverride> selectionOverrides;

    public TrackSelection(int trackType, boolean isDisable, List<SelectionOverride> selectionOverrides) {
        this.trackType = trackType;
        this.isDisable = isDisable;
        this.selectionOverrides = selectionOverrides;
    }

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public List<SelectionOverride> getSelectionOverrides() {
        return selectionOverrides;
    }

    public void setSelectionOverrides(List<SelectionOverride> selectionOverrides) {
        this.selectionOverrides = selectionOverrides;
    }
}
