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
import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.OverlayItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button addStart;
    private Button addEnd;
    private Button calcPath;
    private MapView map;
    private AnnotationView annotation;

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

        //initialize map
        // set the zoom level, center point and enable the default zoom controls
        map = (MapView) rootView.findViewById(R.id.map);
        map.getController().setZoom(12);
        map.getController().setCenter(new GeoPoint(37.761078, -122.446142));
        map.setBuiltInZoomControls(true);

        // initialize the annotation to be shown later
        annotation = new AnnotationView(map);

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
            }
        });
        // use a custom POI marker by referencing the bitmap file directly,

        // using the filename as the resource ID
        Drawable start_marker = getResources().getDrawable(R.mipmap.ic_start_marker);
        final DefaultItemizedOverlay poiOverlay = new DefaultItemizedOverlay(start_marker);

        // set GeoPoints and title/snippet to be used in the annotation view
        OverlayItem poi =  new OverlayItem(new GeoPoint(37.761078, -122.446142), "TITLE", "subtitle");
        poiOverlay.addItem(poi);

        // add a tap listener for the POI overlay
        poiOverlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
            @Override
            public void onTap(GeoPoint pt, MapView mapView) {
                // when tapped, show the annotation for the overlayItem
                int lastTouchedIndex = poiOverlay.getLastFocusedIndex();
                if(lastTouchedIndex>-1){
                    OverlayItem tapped = poiOverlay.getItem(lastTouchedIndex);
                    annotation.showAnnotationView(tapped);
                }
            }
        });

        map.getOverlays().add(poiOverlay);
        return rootView;
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
}
