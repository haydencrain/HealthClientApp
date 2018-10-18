package M5.seshealthpatient.Extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.MapView;

/**
*   NoScrollMapView
*   extends: MapView
*   Allows a map to be scrolled when within a ScrollLayout element.
*/
public class NoScrollMapView extends MapView {
    public NoScrollMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                // on touch up, allow ScrollLayout to interact with touch event again.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_DOWN:
                // on touch down, prevent ScrollLayout from interacting with touch event
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
