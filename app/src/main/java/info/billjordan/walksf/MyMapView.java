package info.billjordan.walksf;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.MapView;

/**
 * Created by bill on 6/20/15.
 */
public class MyMapView extends MapView{
    private static final String TAG = MyMapView.class.getSimpleName();

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, String apiKey) {
        super(context, apiKey);
    }

    public MyMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //duration of touch event in milliseconds
        long touchDuration = event.getEventTime() - event.getDownTime();
        long longTouch = 500;
        if (touchDuration >=  longTouch) {
            Toast.makeText(getContext(), String.valueOf(touchDuration), Toast.LENGTH_LONG);
            GeoPoint touchLocation = new GeoPoint(this.getProjection().fromPixels(((int) event.getX()), (int) event.getY()));
            MapFragment mapFragment = (MapFragment) ((ActionBarActivity) this.getContext()).getSupportFragmentManager().findFragmentById(R.id.container);
            mapFragment.addNode(touchLocation);
        }
        Log.d(TAG, this.getContext().toString());
        Log.d(TAG, String.valueOf(touchDuration));
        return super.onTouchEvent(event);
    }
}
