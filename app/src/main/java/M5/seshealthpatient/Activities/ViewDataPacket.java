package M5.seshealthpatient.Activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import M5.seshealthpatient.Models.Comment;
import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ViewDataPacket extends BaseActivity implements OnMapReadyCallback {

    private DatabaseReference mUserDb;
    private DatabaseReference mDataPacketDb;
    private DataPacket mDataPacket;
    private String mPatientId;
    private PatientUser mPatient;
    private TextView mSentFromTV;
    private TextView mQueryTV;
    private Button mQueryBtn;
    private TextView mHeartRateTV;
    private TextView mLocationTV;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GeoDataClient mGeoDataClient;
    private TextView videoTV;
    private Button mPlayVideo;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_data_packet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        bindViewComponents();
        ButterKnife.bind( this );
        mDataPacket = (DataPacket) getIntent().getSerializableExtra( "DATA_PACKET" );
        mPatientId = (String) getIntent().getSerializableExtra( "PATIENT_ID" );
        setTitle( "Data Packet - " + mDataPacket.getTitle() );
        if (mDataPacket.getQuery() == null || mDataPacket.getQuery().isEmpty()) {
            mQueryTV.setText("No query has been sent");
        } else {
            mQueryTV.setText( mDataPacket.getQuery() );
        }
        if (mDataPacket.getHeartRate() == null || mDataPacket.getHeartRate().isEmpty()) {
            mHeartRateTV.setText("A heart rate has not been sent");
        } else {
            mHeartRateTV.setText( mDataPacket.getHeartRate() );
        }

        if (mDataPacket.hasLocation()) {
            mLocationTV.setText("A red marker indicates the location of the Patient, where as blue markers indicate recommended medical facilities");
            setUpGoogleMaps( savedInstanceState );
        } else {
            mLocationTV.setText( "A location has not been sent" );
            mMapView.setVisibility( View.GONE );
        }

        if (mDataPacket.getFile() == null || mDataPacket.getFile().isEmpty()) {
            mPlayVideo.setVisibility(View.GONE);
            videoTV.setText("No video files have been sent");
        } else {
            videoTV.setVisibility(View.GONE);
        }
        addDbListeners();

        mPlayVideo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDataPacket.getFile() != null) {
                    Uri uri = Uri.parse( mDataPacket.getFile() );
                    Toast.makeText( ViewDataPacket.this, "Loading video...", Toast.LENGTH_LONG ).show();
                    Intent intent = new Intent( Intent.ACTION_VIEW, uri );
                    startActivity( intent );

                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    Date currentTime = Calendar.getInstance().getTime();

                    request.setDescription("download");
                    request.setTitle(""+currentTime.toString());
// in order for this if to run, you must use the android 3.2 to compile your app
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, ""+currentTime.toString()+".mp4");

// get download service and enqueue file
                    DownloadManager manager = (DownloadManager) getSystemService( Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);

                } else {
                    Toast.makeText( ViewDataPacket.this, "No video file uploaded", Toast.LENGTH_LONG ).show();
                }

            }
        } );
    }


    public void bindViewComponents() {
        mSentFromTV = findViewById(R.id.sentFromTV);
        mQueryTV = findViewById(R.id.queryTV);
        mQueryBtn = findViewById(R.id.queryBtn);
        mHeartRateTV = findViewById(R.id.heartRateTV);
        mLocationTV = findViewById(R.id.locationTV);
        mMapView = findViewById(R.id.mapView);
        videoTV = findViewById(R.id.videoTV);
        mPlayVideo = findViewById( R.id.playVideoBtn );
    }

    @OnClick(R.id.queryBtn)
    public void onQueryBtnClick() {
        navigateToFeedbackActivity("QUERY");
    }

    @OnClick(R.id.heartRateBtn)
    public void onHeartRateBtnClick() {
        navigateToFeedbackActivity("HEART_RATE");
    }

    @OnClick(R.id.locationBtn)
    public void onLocationBtnClick() {
        navigateToFeedbackActivity("LOCATION");
    }

    @OnClick(R.id.filesBtn)
    public void onFilesBtnClick() {
        navigateToFeedbackActivity("FILES");
    }

    public void navigateToFeedbackActivity(String feedbackType) {
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra("PATIENT_ID", mPatientId);
        intent.putExtra("DATA_PACKET_ID", mDataPacket.getId());
        intent.putExtra("DATA_PACKET_TITLE", mDataPacket.getTitle());
        intent.putExtra("FEEDBACK_TYPE", feedbackType);
        intent.putExtra("HAS_LOCATION", mDataPacket.hasLocation());
        startActivity(intent);
    }

    public void setUpGoogleMaps(Bundle savedInstanceState) {
        mGeoDataClient = Places.getGeoDataClient(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        LatLng latLng =  new LatLng(mDataPacket.getLatitude(), mDataPacket.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Patient's Location");
        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, LocationDefaults.DEFAULT_ZOOM));
    }


    @Override
    public void onResume() {
        if (mMapView.getVisibility() == View.VISIBLE)
            mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView.getVisibility() == View.VISIBLE)
            mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView.getVisibility() == View.VISIBLE)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView.getVisibility() == View.VISIBLE)
            mMapView.onLowMemory();
    }

    public void addDbListeners() {
        mUserDb = FirebaseDatabase.getInstance().getReference("Users/" + mPatientId);
        mDataPacketDb = mUserDb.child("Queries/" + mDataPacket.getId());
        addPatientDbListener();
    }

    public void addPatientDbListener() {
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPatient = dataSnapshot.getValue(PatientUser.class);
                mSentFromTV.setText(mPatient.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
