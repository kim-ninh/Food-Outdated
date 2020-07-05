package com.ninh.foodoutdated;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.selection.ItemKeyProvider;

public class MyItemKeyProvider extends ItemKeyProvider<Long> {
    private RecyclerView recyclerView;

    protected MyItemKeyProvider(RecyclerView recyclerView) {
        super(SCOPE_MAPPED);
        this.recyclerView = recyclerView;
    }


    @Nullable
    @Override
    public Long getKey(int i) {
        return recyclerView.getAdapter().getItemId(i);
    }

    @Override
    public int getPosition(@NonNull Long aLong) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForItemId(aLong);
        if (viewHolder == null)
            return RecyclerView.NO_POSITION;
        return viewHolder.getLayoutPosition();
    }

}
