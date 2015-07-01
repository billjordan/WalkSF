package info.billjordan.walksf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
    private Button compareToGoogleButton;
    private Button clearMapButton;
    private Button calcPathButton;
    private MyMapView mapView;
    private AnnotationView annotation;
    private TerminalNodesOverlay terminalNodesOverlay;
    private IntersectionCollection intersectionCollection;
    private MapFragment thisFragment;

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
        thisFragment = this;
        //initialize mapView
        // set the zoom level, center point and enable the default zoom controls
        mapView = (MyMapView) rootView.findViewById(R.id.map);
        mapView.getController().setZoom(12);
        mapView.getController().setCenter(new GeoPoint(37.761078, -122.446142));
        mapView.setBuiltInZoomControls(true);

        // initialize the annotation to be shown later
        annotation = new AnnotationView(mapView);

        //initialize the buttons
        compareToGoogleButton = (Button) rootView.findViewById(R.id.compare_google);
        clearMapButton = (Button) rootView.findViewById(R.id.clear_map);
        calcPathButton = (Button) rootView.findViewById(R.id.calc_path);
        compareToGoogleButton.setOnClickListener(new View.OnClickListener() {
            //if start and end nodes are set open google maps with intent
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "add start", Toast.LENGTH_SHORT).show();
                boolean startNodeSet = terminalNodesOverlay.isStartNodeSet();
                boolean endNodeSet = terminalNodesOverlay.isEndNodeSet();
                if (!startNodeSet && !endNodeSet) {
                    Toast.makeText(getActivity(), "Set the starting and ending locations by long-clicking.", Toast.LENGTH_SHORT).show();
                } else if (!startNodeSet) {
                    Toast.makeText(getActivity(), "Set the starting location.", Toast.LENGTH_SHORT).show();
                } else if (!endNodeSet) {
                    Toast.makeText(getActivity(), "Set the ending location.", Toast.LENGTH_SHORT).show();
                } else {
                    Intersection startIntersection = terminalNodesOverlay.getStartIntersection();
                    Intersection endIntersection = terminalNodesOverlay.getEndIntersection();
                    String gMapsURL = "http://maps.google.com/maps/?saddr="
                            + startIntersection.getLatitude()
                            + ","
                            + startIntersection.getLongitude()
                            + "&daddr="
                            + endIntersection.getLatitude()
                            + ","
                            + endIntersection.getLongitude()
                            + "&dirflg=w";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gMapsURL));
                    startActivity(browserIntent);
                }
            }
        });
        clearMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "add end", Toast.LENGTH_SHORT).show();
                terminalNodesOverlay.removeNodes();
                removePaths();  //remove Paths redraws the map
            }
        });
        calcPathButton.setOnClickListener(new View.OnClickListener() {
            //if start and end nodes are set get path from api server and add it to map
            @Override
            public void onClick(View v) {

                //if there is already a path on the map, remove it
                removePaths();


                boolean startNodeSet = terminalNodesOverlay.isStartNodeSet();
                boolean endNodeSet = terminalNodesOverlay.isEndNodeSet();
                if (!startNodeSet && !endNodeSet) {
                    Toast.makeText(getActivity(), "Set the starting and ending locations by long-clicking.", Toast.LENGTH_SHORT).show();
                } else if (!startNodeSet) {
                    Toast.makeText(getActivity(), "Set the starting location.", Toast.LENGTH_SHORT).show();
                } else if (!endNodeSet) {
                    Toast.makeText(getActivity(), "Set the ending location.", Toast.LENGTH_SHORT).show();
                } else {
                    new FetchPathTask(
                            thisFragment,
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

    private void removePaths() {
        //remove all lineOverlays
        while(mapView.getOverlays().remove(lineOverlay)){
            String pass = "do nothing";
        }
        //redraw map
        mapView.postInvalidate();
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
        Intersection closestIntersection =
                intersectionCollection.getClosestIntersection(geoPoint.getLatitude(), geoPoint.getLongitude());

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
