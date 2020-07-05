package com.ninh.foodoutdated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.selection.ItemDetailsLookup;

class MyDetailsLookup extends ItemDetailsLookup<Long> {

    private RecyclerView mRecyclerView;

    public MyDetailsLookup(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent motionEvent) {
        View view = mRecyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
            if (holder instanceof MyAdapter.MyViewHolder) {
                return ((MyAdapter.MyViewHolder) holder).getItemDetails();
            }
        }
        return null;
    }
}
