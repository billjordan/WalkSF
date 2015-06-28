package info.billjordan.walksf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;

import com.mapquest.android.maps.GeoPoint;

import javax.xml.transform.Result;

/**
 * Created by bill on 6/21/15.
 */
public class AddNodeDialogFragment extends android.support.v4.app.DialogFragment {


//    private String intersectionString;
    private String choice;
    private AddNodeDialogResult result;
    private Intersection intersection;

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
        result.setIntersection(intersection);
//        setIntersectionGeoPoint(new GeoPoint(intersection.getLatitude(), intersection.getLongitude()));
//        setIntersectionString(intersection.getDescription());
    }


//    public void setIntersectionGeoPoint(GeoPoint intersectionGeoPoint) {
//        this.intersectionGeoPoint = intersectionGeoPoint;
//        setIntersectionString(this.intersectionGeoPoint.toString());
////        result.setLocation(intersectionGeoPoint);
//    }

//    private GeoPoint intersectionGeoPoint;

    // Use this instance of the interface to deliver action events
    private NoticeDialogListener mListener;
    private AlertDialog alertDialog;


    public AddNodeDialogFragment() {
        super();
//        intersectionString = null;
        intersection = null;
        result = new AddNodeDialogResult();
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] choices = {"Start Intersection", "End Intersection"};
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (intersection == null){
            throw new IllegalStateException("The intersectionString must be set before showing this " +
                    "dialog. Call setIntersectionString()");
        }
//        builder.setMessage(intersectionString)
        builder.setTitle(intersection.getDescription())
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(choices, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                choice = choices[which];
                                //sets result as start or end
                                //which will be 0 if start, 1 if end
                                //unsemantic
                                result.setTerminus(which);
//                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(true);
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        mListener.onDialogPositiveClick(result);

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        alertDialog = builder.create();
        //set the ok button unclickable until node is picked
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //make button look unclickable
//                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setClickable(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
//        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        return alertDialog;
    }

//    public void setIntersectionString(String intersectionString) {
//        this.intersectionString = intersectionString;
//    }

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(AddNodeDialogResult result);
//        public void onDialogNegativeClick(DialogFragment dialog);
    }



    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) ((ActionBarActivity) activity).getSupportFragmentManager().findFragmentById(R.id.container);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }



}
