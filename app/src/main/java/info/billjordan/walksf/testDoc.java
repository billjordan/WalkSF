package info.billjordan.walksf;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.mapquest.android.maps.BoundingBox;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.LineOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.Overlay;

import java.util.List;

/**
 * Created by bill on 6/26/15.
 */
public class testDoc extends LineOverlay {
    public testDoc() {
        super();
    }

    public testDoc(Paint linePaint) {
        super(linePaint);
    }

    @Override
    public void setData(List<GeoPoint> data, BoundingBox bbox) {
        super.setData(data, bbox);
    }

    @Override
    public void setData(List<GeoPoint> data, boolean recomputeBoundingBox) {
        super.setData(data, recomputeBoundingBox);
    }

    @Override
    public void setData(List<GeoPoint> data) {
        super.setData(data);
    }

    @Override
    public void setLinePaint(Paint paint) {
        super.setLinePaint(paint);
    }

    @Override
    public void setShowPoints(boolean showPoints, Paint pointPaint) {
        super.setShowPoints(showPoints, pointPaint);
    }

    @Override
    public void setPointPaint(Paint paint) {
        super.setPointPaint(paint);
    }

    @Override
    public void setBoundingBox(BoundingBox boundingBox) {
        super.setBoundingBox(boundingBox);
    }

    @Override
    public boolean isSimplify() {
        return super.isSimplify();
    }

    @Override
    public void setSimplify(boolean simplify, int tolerance) {
        super.setSimplify(simplify, tolerance);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
    }

    @Override
    public boolean onTap(GeoPoint gp, MapView mapView) {
        return super.onTap(gp, mapView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt, MapView mapView) {
        return super.onTouchEvent(evt, mapView);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent evt, MapView mapView) {
        return super.onTrackballEvent(evt, mapView);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void addPoint(GeoPoint geoPoint, int maxPoints) {
        super.addPoint(geoPoint, maxPoints);
    }

    @Override
    public void setTapListener(OverlayTapListener overlayTapListener) {
        super.setTapListener(overlayTapListener);
    }

    @Override
    public void setTouchEventListener(OverlayTouchEventListener touchListener) {
        super.setTouchEventListener(touchListener);
    }

    @Override
    public void setTrackballEventListener(OverlayTrackballEventListener trackballListener) {
        super.setTrackballEventListener(trackballListener);
    }

    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        return super.draw(canvas, mapView, shadow, when);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
        return super.onKeyDown(keyCode, event, mapView);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, MapView mapView) {
        return super.onKeyUp(keyCode, event, mapView);
    }

    @Override
    public int getZIndex() {
        return super.getZIndex();
    }

    @Override
    public void setZIndex(int zIndex) {
        super.setZIndex(zIndex);
    }

    @Override
    public String getKey() {
        return super.getKey();
    }

    @Override
    public void setKey(String key) {
        super.setKey(key);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
