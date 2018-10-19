package M5.seshealthpatient.Fragments;

import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.Models.PlaceResult;
import M5.seshealthpatient.Services.RequestQueueSingleton;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import M5.seshealthpatient.R;
import M5.seshealthpatient.Utils.Helpers;



/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LinkedList<PlaceResult> mMedicalFacilities;


    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("View Nearby Facilities");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mGeoDataClient = Places.getGeoDataClient(getActivity());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.onResume();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

        getLocationPermission();

        updateLocationUI();
        getDeviceLocation();
    }

    private void getMedicalPlaces(double lat, double lng) {
				// get the api url for getting nearby medical facilities
        String url = Helpers.getNearbyPlacesUrl(getActivity(), lat, lng, "hospital");
        Log.d("getUrl", url);

				// Send out a HTTP request to find all medical facilities (hospitals)
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("googlePlacesResponse", response.toString());
                JSONArray results;
                try {
                    results = response.getJSONArray("results");
										LinkedList<PlaceResult> places = new LinkedList<>();
										// for each place that was returned from the request, create a marker for it,
										// and add it to the map
                    for(int i = 0; i < results.length(); i++) {
                        PlaceResult place = new PlaceResult(results.getJSONObject(i));
                        Helpers.addPlaceResultMarker(mGoogleMap, place, BitmapDescriptorFactory.HUE_RED);
                        places.add(place);
                    }
                    mMedicalFacilities = places;

                } catch (JSONException e) {
                    Log.d("GooglePlaceResults", e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
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

                            if (location == null) {
                                location.setLatitude(LocationDefaults.DEFAULT_LOCATION.latitude);
                                location.setLongitude(LocationDefaults.DEFAULT_LOCATION.longitude);
                            }
                            mLastKnownLocation = location;
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), LocationDefaults.DEFAULT_ZOOM));

                            // get nearby medical places using known location
                            getMedicalPlaces(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    openGooglePlaceBrowser(marker.getTitle());
                                }
                            });

                        } else {
                            mGoogleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(LocationDefaults.DEFAULT_LOCATION, LocationDefaults.DEFAULT_ZOOM));
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

                            // get nearby places using default location
                            getMedicalPlaces(LocationDefaults.DEFAULT_LOCATION.latitude, LocationDefaults.DEFAULT_LOCATION.longitude);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
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
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void openGooglePlaceBrowser(String placeName) {
        for (PlaceResult place : mMedicalFacilities) {
            if (place.getName().equals(placeName)) {
                String url = Helpers.getPlaceDetailsUrl(getActivity(), place.getPlaceId());
                RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String placeUrl = response.getJSONObject("result").getString("url");
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeUrl));
                            startActivity(browserIntent);
                        } catch (JSONException e) {
                            Log.d("GooglePlaceDetailResult", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }));
            }
        }
    }
}
