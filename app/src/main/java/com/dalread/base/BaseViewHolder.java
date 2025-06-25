package com.dalread.base;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected int getPositionForAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Code for API level 29 and above
            return getBindingAdapterPosition();
        } else {
            // Code for API level below 29
            return getAdapterPosition();
        }
    }
}
