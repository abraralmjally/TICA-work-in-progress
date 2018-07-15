package com.wilki.tica.dragAndTouchListeners;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by John Wilkie on 05/03/2017.
 * Is attached to views that can be dragged. Responsible for controlling the dragging behaviour.
 */

public class DragTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData dragData = new ClipData(view.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
            view.startDrag(dragData, myShadow, null, 0);
            return true;
        } else {
            return false;
        }
    }
}
