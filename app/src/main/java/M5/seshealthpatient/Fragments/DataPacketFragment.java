package M5.seshealthpatient.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.Date;

import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataPacketFragment extends Fragment {

    private FragmentManager manager;
    private FragmentTransaction ft;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    // view components
    private EditText queryTextBox;
    private TextView tvHeartRate;
    private Button btnHeartRate;
    private Button btnLocation;
    private TextView txtLocation;
    private Button btnSendPacket;
    private Button btnNote;
    private TextView txtNote;
    private Button btnPicture;
    private ImageView imgPicture;
    private final int PICK_IMAGE_REQUEST = 71;

    // data packet
    DataPacket dataPacket;


    public DataPacketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Create New Data Packet");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// Inflate the layout for this fragment
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        View view = inflater.inflate(R.layout.fragment_data_packet, container, false);
        bindViewComponents(view);

        dataPacket = new DataPacket();

        Bundle arguments = getArguments();
        if(arguments != null)
        {
            String heartRate = (String)arguments.get("str");
            dataPacket.setHeartRate(heartRate);
            tvHeartRate.setText(heartRate);
        }

        setEventListeners();
        return view;
    }

    private void bindViewComponents(View view) {
        queryTextBox = view.findViewById(R.id.queryBox);
        txtLocation = view.findViewById(R.id.txtLocation);
        btnLocation = view.findViewById(R.id.btnLocation);
        btnHeartRate = view.findViewById(R.id.btnSHR);
        tvHeartRate = view.findViewById(R.id.tvHeartRate);
        btnSendPacket = view.findViewById(R.id.btnSendQ);
        btnNote = view.findViewById(R.id.btnNote);
        txtNote = view.findViewById(R.id.txtNote);
        btnPicture = view.findViewById(R.id.btnPicture);
        imgPicture = view.findViewById(R.id.imgPicture);
    }

    private void setEventListeners() {
        btnHeartRate.setOnClickListener( new View.OnClickListener() {
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

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                setDeviceLocation();
            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            final TextInputEditText text=getView().findViewById(R.id.addedittext);
            @Override
            public void onClick(View v) {
                String value = text.getText().toString();
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(getActivity(), "Please enter a note!", Toast.LENGTH_SHORT).show();
                } else {
                    dataPacket.setNote(value);
                    txtNote.setText(value);
                }
            }
        });

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnSendPacket.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                //getting the db directory for the currently logged in user using their id
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users/" + uid);

                dataPacket.setQuery(queryTextBox.getText().toString());
                dataPacket.setSentDate(new Date());




                String queryKey = dbRef.child("Queries").push().getKey();
                dbRef.child("Queries").child(queryKey).setValue(dataPacket);
                Toast.makeText(getActivity(), "Query Sent Successfully", Toast.LENGTH_LONG).show();
            }


        });
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
                        Location location = task.getResult();
                        dataPacket.setLocation(location);
                        txtLocation.setText(String.format("%s, %s", location.getLatitude(), location.getLongitude()));
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            dataPacket.setPhotoFilePath(data.getData());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPicture.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
