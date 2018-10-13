package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import M5.seshealthpatient.Models.DataPacket;
import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.Models.PlaceResult;
import M5.seshealthpatient.R;
import M5.seshealthpatient.Services.RequestQueueSingleton;
import M5.seshealthpatient.Utils.Helpers;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecommendFacility extends BaseActivity implements OnMapReadyCallback, OnMapClickListener, OnMarkerClickListener {
    private GeoDataClient mGeoDataClient;
    private GoogleMap mGoogleMap;
    private TextView mapTV;
    private MapView mMapView;
    private RelativeLayout selectFacilityContainer;

    private String mPatientId;
    private String mDataPacketId;
    private DataPacket mDataPacket;
    private double patientLatitude;
    private double patientLongitude;
    private LinkedList<PlaceResult> mMedicalFacilities;
    private PlaceResult selectedMedicalFacility;

    private RequestQueueSingleton requestQueue;
    private DatabaseReference mDataPacketDb;
    private ValueEventListener mDataPacketEventListener;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_facility;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Recommend a Facility");
        bindViewComponents();
        ButterKnife.bind(this);
        getDataFromIntent();
        setDbReferences();
        setUpGoogleMaps(savedInstanceState);
        requestQueue = RequestQueueSingleton.getInstance(this);
    }

    @Override
    public void onStart() {
        mDataPacketEventListener = getDataPacketEventListener();
        mDataPacketDb.addValueEventListener(mDataPacketEventListener);
        mMapView.onStart();
        super.onStart();
    }

    @Override
    public void onStop() {
        mDataPacketDb.removeEventListener(mDataPacketEventListener);
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void bindViewComponents() {
        mapTV = findViewById(R.id.mapTV);
        mMapView = findViewById(R.id.mapView);
        selectFacilityContainer = findViewById(R.id.selectFacilityContainer);
        hideSelectFacilityButton();
    }

    public void getDataFromIntent() {
        mPatientId = (String)getIntent().getSerializableExtra("PATIENT_ID");
        mDataPacketId = (String)getIntent().getSerializableExtra("DATA_PACKET_ID");
    }

    public void setDbReferences() {
        mDataPacketDb = FirebaseDatabase.getInstance()
                .getReference("Users/" + mPatientId)
                .child("Queries")
                .child(mDataPacketId);
    }

    public void setUpGoogleMaps(Bundle savedInstanceState) {
        mGeoDataClient = Places.getGeoDataClient(this);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume();
    }

    public ValueEventListener getDataPacketEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataPacket = dataSnapshot.getValue(DataPacket.class);
                setUpMarkersAndLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
    }

    public void setUpMarkersAndLocation() {
        if (mDataPacket != null) {
            setPatientLocation();
            getMedicalPlaces();
        }
    }

    public void setPatientLocation() {
        patientLatitude = mDataPacket.getLatitude();
        patientLongitude = mDataPacket.getLongitude();

        LatLng latLng =  new LatLng(patientLatitude, patientLongitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Patient's Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, LocationDefaults.DEFAULT_ZOOM));
    }

    private void getMedicalPlaces() {
        JsonObjectRequest medicalPlacesRequest = getMedicalPlacesRequest();
        requestQueue.addToRequestQueue(medicalPlacesRequest);
    }

    private JsonObjectRequest getMedicalPlacesRequest() {
        String url = Helpers.getNearbyPlacesUrl(this, patientLatitude, patientLongitude, "hospital");
        return new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addMedicalPlacesToMap(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void addMedicalPlacesToMap(JSONObject response) {
        JSONArray results;
        try {
            results = response.getJSONArray("results");
            LinkedList<PlaceResult> places = new LinkedList<>();
            for(int i = 0; i < results.length(); i++) {
                PlaceResult place = new PlaceResult(results.getJSONObject(i));

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(place.getLat(), place.getLng()))
                        .title(place.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                mGoogleMap.addMarker(markerOptions);
                places.add(place);
            }
            mMedicalFacilities = places;

        } catch (JSONException e) {
            Log.d("GooglePlaceResults", e.toString());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getTitle().equals("Patient's Location")) {
            showSelectFacilityButton();
            setSelectedPlace(marker);
        } else {
            hideSelectFacilityButton();
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideSelectFacilityButton();
    }

    public void showSelectFacilityButton() {
        if (selectFacilityContainer.getVisibility() != View.VISIBLE)
            selectFacilityContainer.setVisibility(View.VISIBLE);
    }

    public void hideSelectFacilityButton() {
        if (selectFacilityContainer.getVisibility() != View.GONE)
            selectFacilityContainer.setVisibility(View.GONE);
    }

    public void setSelectedPlace(Marker marker) {
        for (PlaceResult place : mMedicalFacilities) {
            if (place.getName().equals(marker.getTitle())) {
                selectedMedicalFacility = place;
                return;
            }
        }
    }

    @OnClick(R.id.selectBtn)
    public void onSelectBtnClick() {
        Intent intent = new Intent();
        intent.putExtra("SELECTED_FACILITY", selectedMedicalFacility);
        setResult(RESULT_OK, intent);
        finish();
    }
}
