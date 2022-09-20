package com.parasoft.demoapp.util;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecycleOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean isSlidingUp = false;

    // load more operation will be triggered only when the displayed item is the last item and it's slide-up behavior
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            if (lastItemPosition == (itemCount - 1) && isSlidingUp) {
                onLoadMore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isSlidingUp = dy > 0;
    }

    public abstract void onLoadMore();
}
