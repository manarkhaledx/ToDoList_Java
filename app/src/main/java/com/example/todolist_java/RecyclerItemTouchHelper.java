package com.example.todolist_java;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist_java.recyclerview.ToDoListAdapter;


/**
 * ItemTouchHelper implementation for handling swipe actions in the RecyclerView.
 */
public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private final ToDoListAdapter adapter;
    private final Context context;

    public RecyclerItemTouchHelper(ToDoListAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
    }

    // Callback method invoked when an item is moved in the RecyclerView (not used here)
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    // Callback method invoked when an item is swiped
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            // Display a confirmation dialog for task deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.delete_task_dialog_title);
            builder.setMessage(R.string.delete_task_dialog_message);
            builder.setPositiveButton(R.string.confirm, (dialog, which) -> adapter.deleteItem(position));
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> adapter.notifyItemChanged(viewHolder.getAdapterPosition()));
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Edit the task
            adapter.editItem(position);
        }
    }

    // Callback method invoked during the child view's draw operation
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Drawable icon;
        ColorDrawable background;
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // Set the icon and background based on the swipe direction
        if (dX > 0) { // Swiping to the right
            icon = ContextCompat.getDrawable(context, R.drawable.baseline_edit);
            background = new ColorDrawable(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.baseline_delete);
            background = new ColorDrawable(ContextCompat.getColor(context, android.R.color.holo_red_light));
        }

        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        // Set the icon and background bounds based on the swipe direction
        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // View is unswiped
            background.setBounds(0, 0, 0, 0);
        }

        // If the view is not actively being swiped, reset its position
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            background.setBounds(0, 0, 0, 0);
            icon.setBounds(0, 0, 0, 0);
        }

        // Draw the background and icon
        background.draw(c);
        icon.draw(c);
    }
}
