package info.billjordan.walksf;

/**
 * Created by bill on 6/27/15.
 */
public class Intersection implements java.io.Serializable{

    private int cnn;
    private double latitude;
    private double longitude;
    private String description;

    public Intersection(int cnn, double latitude, double longitude, String description){
        this.cnn = cnn;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public int getCnn() {
        return cnn;
    }

    public void setCnn(int cnn) {
        this.cnn = cnn;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
