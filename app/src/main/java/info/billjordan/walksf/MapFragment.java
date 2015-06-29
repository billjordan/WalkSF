package info.billjordan.walksf;

import android.app.Activity;
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
                Toast.makeText(getActivity(), "calc path", Toast.LENGTH_SHORT).show();
//                fetchPath(terminalNodesOverlay.getStartIntersection(), terminalNodesOverlay.getEndIntersection());
                ArrayList<Integer> path = (ArrayList<Integer>) (new FetchPathTask().execute());
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

        return rootView;
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
