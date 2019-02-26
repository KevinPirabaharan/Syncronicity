package com.example.avjindersinghsekhon.minimaltodo;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemTouchHelperClass extends ItemTouchHelper.Callback{
    private ItemTouchHelperAdapter adapter;
    public interface ItemTouchHelperAdapter{
        void onItemMoved(int fromPosition, int toPosition);
        void onItemRemoved(int position);
    }

    public ItemTouchHelperClass(ItemTouchHelperAdapter ad){
        adapter = ad;
    }

    @Override
    //Returns whether ItemTouchHelper should start a drag and drop operation if an item is long pressed
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    //Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    //Should return a composite flag which defines the enabled move directions in each state (idle, swiping, dragging)
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int upFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(upFlags, swipeFlags);
    }

    @Override
    //Called when ItemTouchHelper wants to move the dragged item from its old position to the new position
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    //Called when a ViewHolder is swiped by the user.
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemRemoved(viewHolder.getAdapterPosition());

    }
}
