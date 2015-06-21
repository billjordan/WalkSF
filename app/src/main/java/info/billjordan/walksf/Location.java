package info.billjordan.walksf;

import com.mapquest.android.maps.GeoPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by billy on 3/10/15.
 */
public class Location {



    private HashSet<String> streets;
    private GeoPoint loc;
    private int cnn;

    public Location(int cnn, String street1, String street2, GeoPoint loc){
        streets = new HashSet();
        streets.add(street1);
        streets.add(street2);
        this.loc = loc;
    }

    public HashSet<String> getStreets() {
        return streets;
    }

    public void setStreets(HashSet<String> streets) {
        this.streets = streets;
    }

    public GeoPoint getLoc() {
        return loc;
    }

    public void setLoc(GeoPoint loc) {
        this.loc = loc;
    }

    public int getCnn() {
        return cnn;
    }

    public void setCnn(int cnn) {
        this.cnn = cnn;
    }
}
