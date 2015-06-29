package info.billjordan.walksf;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mapquest.android.maps.AnnotationView;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.LineOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.OverlayItem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment implements AddNodeDialogFragment.NoticeDialogListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button addStart;
    private Button addEnd;
    private Button calcPath;
    private MyMapView mapView;
    private AnnotationView annotation;
    private TerminalNodesOverlay terminalNodesOverlay;
    private IntersectionCollection intersectionCollection;
    private MapFragment thisFragemnt;

    private LineOverlay lineOverlay;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        thisFragemnt = this;
        //initialize mapView
        // set the zoom level, center point and enable the default zoom controls
        mapView = (MyMapView) rootView.findViewById(R.id.map);
        mapView.getController().setZoom(12);
        mapView.getController().setCenter(new GeoPoint(37.761078, -122.446142));
        mapView.setBuiltInZoomControls(true);

        // initialize the annotation to be shown later
        annotation = new AnnotationView(mapView);

        //initialize the buttons
        addStart = (Button) rootView.findViewById(R.id.add_start);
        addEnd = (Button) rootView.findViewById(R.id.add_end);
        calcPath = (Button) rootView.findViewById(R.id.calc_path);
        addStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add start", Toast.LENGTH_SHORT).show();
            }
        });
        addEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "add end", Toast.LENGTH_SHORT).show();
            }
        });
        calcPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "calc path", Toast.LENGTH_SHORT).show();
//                fetchPath(terminalNodesOverlay.getStartIntersection(), terminalNodesOverlay.getEndIntersection());
                boolean startNodeSet = terminalNodesOverlay.isStartNodeSet();
                boolean endNodeSet = terminalNodesOverlay.isEndNodeSet();
                if(!startNodeSet && !endNodeSet){
                    Toast.makeText(getActivity(), "Set the starting and ending locations by long-clicking.", Toast.LENGTH_LONG).show();
                } else if(!startNodeSet){
                    Toast.makeText(getActivity(), "Set the starting location.", Toast.LENGTH_LONG).show();
                } else if(!endNodeSet){
                    Toast.makeText(getActivity(), "Set the ending location,", Toast.LENGTH_LONG).show();
                } else {
                    new FetchPathTask(
                            thisFragemnt,
                            terminalNodesOverlay.getStartIntersection().getCnn(),
                            terminalNodesOverlay.getEndIntersection().getCnn()
                    ).execute();
                }
            }
        });
        // use a custom POI marker by referencing the bitmap file directly,

        // using the filename as the resource ID
        Drawable startMarker = getResources().getDrawable(R.mipmap.ic_start_marker);
        Drawable endMarker = getResources().getDrawable(R.mipmap.ic_end_marker);
        //Not sure why I only need to do this for second marker
        endMarker.setBounds(
                0 - endMarker.getIntrinsicWidth() / 2, 0 - endMarker.getIntrinsicHeight(),
                endMarker.getIntrinsicWidth() / 2, 0);

        terminalNodesOverlay = new TerminalNodesOverlay(startMarker, endMarker);

        // set GeoPoints and title/snippet to be used in the annotation view
//        OverlayItem poi =  new OverlayItem(new GeoPoint(37.761078, -122.446142), "TITLE", "subtitle");
//        terminalNodesOverlay.addItem(poi);

//        terminalNodesOverlay.addStartNode(new GeoPoint(37.765045, -122.419761), "16th and mission");
//        terminalNodesOverlay.addEndNode(new GeoPoint(37.779270, -122.503136), "42 and geary");

        // add a tap listener for the POI overlay
        terminalNodesOverlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
            @Override
            public void onTap(GeoPoint pt, MapView mapView) {
                // when tapped, show the annotation for the overlayItem
                int lastTouchedIndex = terminalNodesOverlay.getLastFocusedIndex();
                if (lastTouchedIndex > -1) {
                    OverlayItem tapped = terminalNodesOverlay.getItem(lastTouchedIndex);
                    annotation.setScaleX((float)(.5));
                    annotation.setScaleY((float) (.5));
                    annotation.setClickable(true);
                    annotation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            annotation.hide();
                        }
                    });
                    annotation.showAnnotationView(tapped);
                }
            }
        });
        mapView.getOverlays().add(terminalNodesOverlay);


        //read in intersectionCollection
        readIntersectionCollection();

//        addPath();
        return rootView;
    }

    public void addPath(ArrayList<Integer> path) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setAlpha(100);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeWidth(5);
        lineOverlay = new LineOverlay(paint);
        List<GeoPoint> pathGeoPoints = new ArrayList<GeoPoint>();
//        geoPoints.add(new GeoPoint(37.721635, -122.472554));
//        geoPoints.add(new GeoPoint(37.765045, -122.419761));
//        geoPoints.add(new GeoPoint(37.779270, -122.503136));

        /////////////////////////
//        geoPoints.add(new GeoPoint(37.78885651,-122.4488358));
//        geoPoints.add(new GeoPoint(37.78907139,-122.447148));
//        geoPoints.add(new GeoPoint(37.78924842,-122.445758));
//        geoPoints.add(new GeoPoint(37.78945708,-122.4441195));
//        geoPoints.add(new GeoPoint(37.78966296, -122.4424764));
//        geoPoints.add(new GeoPoint(37.7898723,-122.4408056));
//        geoPoints.add(new GeoPoint(37.79008145, -122.439136));
//        geoPoints.add(new GeoPoint(37.79029235,-122.4374952));
//        geoPoints.add(new GeoPoint(37.7905018,-122.435849));
//        geoPoints.add(new GeoPoint(37.79071117,-122.4342032));
//        geoPoints.add(new GeoPoint(37.78983697,-122.4340271));
//        geoPoints.add(new GeoPoint(37.78890628,-122.4338397));
//        geoPoints.add(new GeoPoint(37.78795027,-122.4336471));
//        geoPoints.add(new GeoPoint(37.78748416,-122.4335533));
//        geoPoints.add(new GeoPoint(37.78702067,-122.4334599));
//        geoPoints.add(new GeoPoint(37.78608543,-122.4332716));
//        geoPoints.add(new GeoPoint(37.78515094,-122.4330834));
//        geoPoints.add(new GeoPoint(37.78537265,-122.4313664));
//        geoPoints.add(new GeoPoint(37.78556385,-122.4297826));
//        geoPoints.add(new GeoPoint(37.78578449,-122.4281482));
//        geoPoints.add(new GeoPoint(37.78495388,-122.4279889));
//        geoPoints.add(new GeoPoint(37.78533462,-122.424687));
//        geoPoints.add(new GeoPoint(37.78454661,-122.4228381));
//        geoPoints.add(new GeoPoint(37.78408368,-122.4227455));
//        geoPoints.add(new GeoPoint(37.78428329,-122.421171));
//        geoPoints.add(new GeoPoint(37.78382412,-122.4210084));
//        geoPoints.add(new GeoPoint(37.78402912,-122.4193642));
//        geoPoints.add(new GeoPoint(37.78423574,-122.4177067));
//        geoPoints.add(new GeoPoint(37.78444884,-122.4160717));
//        geoPoints.add(new GeoPoint(37.78461295,-122.4147785));
//        geoPoints.add(new GeoPoint(37.78465714,-122.4144302));
//        geoPoints.add(new GeoPoint(37.78486625,-122.4127821));
//        geoPoints.add(new GeoPoint(37.7850746,-122.4111398));
//        geoPoints.add(new GeoPoint(37.78528292,-122.4094975));
//        geoPoints.add(new GeoPoint(37.78538825,-122.4086671));
//        geoPoints.add(new GeoPoint(37.78549156,-122.4078525));
//        geoPoints.add(new GeoPoint(37.78574399,-122.405831));
//        geoPoints.add(new GeoPoint(37.7852651,-122.4052357));
//        geoPoints.add(new GeoPoint(37.7849083,-122.4047951));
//        geoPoints.add(new GeoPoint(37.78447874,-122.4042669));
//        geoPoints.add(new GeoPoint(37.78404444,-122.4037118));
//        geoPoints.add(new GeoPoint(37.78325924,-122.4027082));
//        geoPoints.add(new GeoPoint(37.78245274,-122.4016962));
//        geoPoints.add(new GeoPoint(37.78202427,-122.4011597));
//        geoPoints.add(new GeoPoint(37.78158767,-122.4006131));
//        geoPoints.add(new GeoPoint(37.78121752,-122.4001496));
//        geoPoints.add(new GeoPoint(37.78079148,-122.3996162));
//        geoPoints.add(new GeoPoint(37.78036289,-122.3990796));
//        geoPoints.add(new GeoPoint(37.7799794,-122.3985995));
//        geoPoints.add(new GeoPoint(37.77956691,-122.3980599));
//        geoPoints.add(new GeoPoint(37.77913222,-122.3975389));
//        geoPoints.add(new GeoPoint(37.77875105,-122.3970617));
//        geoPoints.add(new GeoPoint(37.77832683,-122.3965306));
//        geoPoints.add(new GeoPoint(37.77769842,-122.395744));
//        geoPoints.add(new GeoPoint(37.77708933,-122.3949815));
//        geoPoints.add(new GeoPoint(37.77633298,-122.3940351));
//        geoPoints.add(new GeoPoint(37.77578732,-122.3933551));
//        geoPoints.add(new GeoPoint(37.77502686,-122.3923462));
//        geoPoints.add(new GeoPoint(37.77468036,-122.3919115));
//        geoPoints.add(new GeoPoint(37.77432755,-122.3916341));
//        geoPoints.add(new GeoPoint(37.77346692,-122.3914343));
//        geoPoints.add(new GeoPoint(37.772831,-122.3913735));
//        geoPoints.add(new GeoPoint(37.77293079,-122.3897167));
//        geoPoints.add(new GeoPoint(37.77199794,-122.3896115));
//        geoPoints.add(new GeoPoint(37.77110995,-122.3895266));
//        geoPoints.add(new GeoPoint(37.77121526,-122.387778));
//        geoPoints.add(new GeoPoint(37.77076204,-122.3877346));
//        geoPoints.add(new GeoPoint(37.7698666,-122.3876491));
        /////////////////////////

        //add a geoPoint for every intersection in path
        for(int intersectionCnn : path){
            Intersection intersection = intersectionCollection.getIntersection(intersectionCnn);
            pathGeoPoints.add(new GeoPoint(
                    intersection.getLatitude(),
                    intersection.getLongitude()
                    ));
        }

        //add the points tot the overlay and add it to the mapview
        lineOverlay.setData(pathGeoPoints);
        lineOverlay.setShowPoints(true, paint);
        mapView.getOverlays().add(lineOverlay);
        mapView.postInvalidate();
    }

    private void readIntersectionCollection() {
        try {
//            InputStream is = getResources().getAssets().open("intersectionCollection.ser");
           InputStream fileInputStream = getActivity().getAssets().open("intersectionCollection.ser");
           ObjectInputStream intersectionsInputStream = new ObjectInputStream(fileInputStream);
           intersectionCollection = (IntersectionCollection) intersectionsInputStream.readObject();
       } catch (IOException e) {
           e.printStackTrace();
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /**
     * Adds a terminal node
     * <p>
     *     Displays dialog to user to choose start or end node for geopoint
     *     If user selects node and presses ok result is handled by onDialogPositiveClick
     *     nothing happens on cancel
     * </p>
     * @param geoPoint
     */
    public void addNode(GeoPoint geoPoint) {
        AddNodeDialogFragment addNodeDialogFragment = new AddNodeDialogFragment();
//        addNodeDialogFragment.setIntersectionString("Example Haight & Ashburry");
//        addNodeDialogFragment.setIntersectionGeoPoint(geoPoint);
        Intersection closestIntersection =
                intersectionCollection.getClosestIntersection(geoPoint.getLatitude(), geoPoint.getLongitude());
//        String intersectionDescription =
//                intersectionCollection.getClosestIntersection(geoPoint.getLatitude(), geoPoint.getLongitude()).getDescription();
//        addNodeDialogFragment.setIntersectionString(closestIntersection.getDescription());
        addNodeDialogFragment.setIntersection(closestIntersection);
        addNodeDialogFragment.show(getFragmentManager(), null);
    }

    @Override
    public void onDialogPositiveClick(AddNodeDialogResult result) {
        if(result.isStart()) {
            terminalNodesOverlay.addStartNode(result.getIntersection());
        }else if(result.isEnd()){
            terminalNodesOverlay.addEndNode(result.getIntersection());
        }

        //postInvalidate() updates the map and redraws the overlays
        mapView.postInvalidate();
    }


}
