package info.billjordan.walksf;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by bill on 6/27/15.
 */
public class IntersectionCollection implements java.io.Serializable {
    /*
    this class needs to support fast lookup by cnn and
    return the nearest intersection to a random lat/long location
     */

    //for lookup by cnn
    private Hashtable<Integer, Intersection> intersectionHashtable;

    //for finding closest intersection
    private ArrayList<Intersection> intersectionArrayList;

    public IntersectionCollection(){
        intersectionHashtable = new Hashtable<Integer, Intersection>();
        intersectionArrayList = new ArrayList<Intersection>();
    }

    public void addIntersection(Intersection intersection){
        intersectionHashtable.put(intersection.getCnn(), intersection);
        intersectionArrayList.add(intersection);
    }

    public Intersection getClosestIntersection(double latitude, double longitude){
        double closestDistance = getComparableDistance(latitude, longitude, intersectionArrayList.get(0));
        Intersection closestIntersection = intersectionArrayList.get(0);
        for(int i = 1; i < intersectionArrayList.size(); i++){
            double currentDistance = getComparableDistance(latitude, longitude, intersectionArrayList.get(i));
            if(currentDistance < closestDistance){
                closestDistance = currentDistance;
                closestIntersection = intersectionArrayList.get(i);
            }
        }
        return  closestIntersection;
    }

    /*
    returns a comaparable distance between the point specified by latitude, longitude and intersection
     */
    private double getComparableDistance(double latitude, double longitude, Intersection intersection){
        return Math.abs(latitude - intersection.getLatitude()) + Math.abs(longitude -intersection.getLongitude());
    }

    public Intersection getIntersection(int cnn){
        return intersectionHashtable.get(cnn);
    }
}
