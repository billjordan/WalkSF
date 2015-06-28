package info.billjordan.walksf;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.OverlayItem;

import java.io.Console;

/**
 * Created by bill on 6/21/15.
 */
public class TerminalNodesOverlay extends DefaultItemizedOverlay{

    private Drawable startMarker;
    private Drawable endMarker;
    private boolean startNodeSet;
    private boolean endNodeSet;
    private OverlayItem startNode;
    private OverlayItem endNode;
    private Intersection startIntersection;
    private Intersection endIntersection;


//    public TerminalNodesOverlay(Drawable defaultMarker) {
//        super(defaultMarker);
//    }

    /**
     * Creates new overlay for adding and removing terminal nodes
     * @param startMarker Marker garphic to represent starting intersection
     * @param endMarker Marker garphic to represent ending intersection
     */
    public TerminalNodesOverlay(Drawable startMarker, Drawable endMarker){
        super(startMarker);
        this.startMarker = startMarker;
        this.endMarker = endMarker;
        startNodeSet = false;
        endNodeSet = false;
    }


    /**
     * Returns true if both nodes are set
     * <p>
     *     This method should be called before route is calculated
     * </p>
     */
    public boolean areNodesSet(){
        return (startNodeSet && endNodeSet);
    }


    public boolean isStartNodeSet(){
        return startNodeSet;
    }


    public boolean isEndNodeSet(){
        return endNodeSet;
    }


    /**
     * Adds or replaces start node
     * @param geoPoint
     * @param intersection - string representing intersection ex: "16th & Mission"
     */
    public void addStartNode(GeoPoint geoPoint, String intersection){

        //remove all nodes
        //add endNode back if it is set
        //add startNode
        this.clear();

        if(endNodeSet){
            this.addItem(endNode);
        }

        startNode = new OverlayItem(geoPoint, "Starting Intersection", intersection);
        startNode.setMarker(startMarker);
        this.addItem(startNode);
        startNodeSet = true;
    }



    /**
     * Adds or replaces start node
     * @param intersection Intersection for startNode
     */
    public void addStartNode(Intersection intersection){

        //remove all nodes
        //add endNode back if it is set
        //add startNode
        this.clear();

        if(endNodeSet){
            this.addItem(endNode);
        }

        startNode = new OverlayItem(
                new GeoPoint(intersection.getLatitude(), intersection.getLongitude()),
                "Starting Intersection",
                intersection.getDescription()
        );
        startNode.setMarker(startMarker);
        this.addItem(startNode);
        startIntersection = intersection;
        startNodeSet = true;
    }




    /**
     * Adds or replaces start node
     * @param intersection Intersection for startNode
     */
    public void addEndNode(Intersection intersection){

        //remove all nodes
        //add startNode back if it is set
        //add endNode
        this.clear();

        if(startNodeSet){
            this.addItem(startNode);
        }

        endNode = new OverlayItem(
                new GeoPoint(intersection.getLatitude(), intersection.getLongitude()),
                "Ending Intersection",
                intersection.getDescription()
        );
        endNode.setMarker(endMarker);
        this.addItem(endNode);
        endIntersection = intersection;
        endNodeSet = true;
    }



    /**
     * Adds or replaces end node
     * @param geoPoint
     * @param intersection - string representing intersection ex: "16th & Mission"
     */
    public void addEndNode(GeoPoint geoPoint, String intersection){

        //remove all nodes
        //add startNode back if it is set
        //add endNode
        this.clear();

        if(startNodeSet){
            this.addItem(startNode);
        }

        endNode = new OverlayItem(geoPoint, "Ending Intersection", intersection);
        endNode.setMarker(endMarker);
        this.addItem(endNode);
        endNodeSet = true;
    }

//    @Override
//    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//        String str = canvas.toString();
//        System.out.println(str);
//        super.draw(canvas, mapView, shadow);
//    }
//    @Override
//    public void clear() {
//        super.clear();
//        startNodeSet = false;
//        endNodeSet = false;
//    }
}
