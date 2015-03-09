package info.billjordan.walksf;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.MapView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button testButton;

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
        // set the zoom level, center point and enable the default zoom controls
        MapView map = (MapView) rootView.findViewById(R.id.map);
        map.getController().setZoom(12);
        map.getController().setCenter(new GeoPoint(37.761078, -122.446142));
        map.setBuiltInZoomControls(true);
        testButton = (Button) rootView.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "button clicked", Toast.LENGTH_SHORT).show();
            }
        });
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
