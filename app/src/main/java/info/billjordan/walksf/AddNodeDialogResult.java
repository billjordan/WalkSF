package info.billjordan.walksf;

import com.mapquest.android.maps.GeoPoint;

/**
 * Created by bill on 6/22/15.
 */
public class AddNodeDialogResult {


    private GeoPoint location;
    private int terminus;

    /**
     * 0 defines this as a start node, 1 as and end node
     * @param terminus
     */
    public void setTerminus(int terminus) {
        //Refactor this!!!!!!!!!!!!
        //0 and 1 have no meaning, but are the order used by the array to choose
        if (terminus != 0 && terminus != 1){
            throw new IllegalArgumentException("Arguement must be 0 or 1. Use 0 for start, 0 for end");
        }
        this.terminus = terminus;
    }


    public GeoPoint getLocation() {
        return location;
    }


    public void setLocation(GeoPoint location) {
        this.location = location;
    }


    public boolean isStart(){
        if(terminus == 0) {
            return true;
        }else if(terminus == 1){
            return false;
        }else{
            throw new IllegalStateException("Terminus must be set to start or end. Call " +
                    "setTerminus(int) prior to calling this method.");
        }
    }


    public boolean isEnd(){
        if(terminus == 1) {
            return true;
        }else if(terminus == 0){
            return false;
        }else{
            throw new IllegalStateException("Terminus must be set to start or end. Call " +
                    "setTerminus(int) prior to calling this method.");
        }
    }
}
