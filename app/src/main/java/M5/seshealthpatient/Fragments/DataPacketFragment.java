package M5.seshealthpatient.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.Models.dataPacket;
import M5.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataPacketFragment extends Fragment {


    private FragmentManager manager;
    private FragmentTransaction ft;


    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // heart rate stuff
    private static Button btn = null;
    private static String str;

    // location
    private static Button btnLocation;
    private TextView txtLocation;
    private Location mLastKnownLocation;
    private String queryText;

    public DataPacketFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data_packet, container, false);


        EditText queryBox = (EditText) v.findViewById(R.id.queryBox);
        queryText = queryBox.getText().toString();

        Button sendPacketbtn = (Button) v.findViewById(R.id.btnSendQ);
        sendPacketbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();

                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users/" + uid);

                dataPacket dp = new dataPacket(txtLocation.getText().toString(), str, queryText);

                dbref.child("Doctors").child("sampleDoctorId").child("1").setValue(dp);

                Toast.makeText(getActivity(),
                        "Query Sent Successfully, try and stay alive", Toast.LENGTH_LONG).show();




            }


        });











        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        txtLocation = v.findViewById(R.id.txtLocation);
        btnLocation = v.findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                setDeviceLocation();
            }
        });

        btn = v.findViewById( R.id.btnSHR );

        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager = getFragmentManager();
                HeartRateFragment myJDEditFragment = new HeartRateFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.fragment_container, myJDEditFragment);
                ft.addToBackStack(null);
                ft.commit();

            }
            
        } );


        TextView tvObj = (TextView)v.findViewById(R.id.fm01tvid);


        Bundle arguments = getArguments();
        if(arguments == null)
        {
            str = "";
            tvObj.setText( str );
        }
        else {
            String x = (String)getArguments().get( "str");
            tvObj.setText( x );

        }




        return v;
    }

    private void setDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        txtLocation.setText(mLastKnownLocation.getLatitude() + ", " + mLastKnownLocation.getLongitude());

                    } else {
                        // can't set location
                    }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LocationDefaults.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults ) {
        switch (requestCode) {
            case LocationDefaults.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        setDeviceLocation();
    }

}
