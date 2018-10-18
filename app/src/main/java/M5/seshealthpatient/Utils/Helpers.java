package M5.seshealthpatient.Utils;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import M5.seshealthpatient.Models.LocationDefaults;
import M5.seshealthpatient.Models.PlaceResult;
import M5.seshealthpatient.R;

public class Helpers {
    
    // Builds a NearbyPlaces Url string with specific query params
    public static String getNearbyPlacesUrl(Activity activity, double lat, double lng, String placeType) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
        url.append("&radius=" + String.valueOf(5000));
        url.append("&type=" + placeType);
        url.append("&key=" + activity.getResources().getString(R.string.api_key));
        return url.toString();
    }

    // Builds a NearbyPlaces Url string with specific query params
    public static String getPlaceDetailsUrl(Activity activity, String placeId) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        url.append("placeid=" + placeId);
        url.append("&key=" + activity.getResources().getString(R.string.api_key));
        return url.toString();
    }

    // helper to add a marker on a google map implementation
    public static void setPatientLocation(GoogleMap map, double latitute, double longitude, int zoom) {
        LatLng latLng =  new LatLng(latitute, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Patient's Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    // helper to add a medical facility place marker on a google map implementation
    public static void addPlaceResultMarker(GoogleMap map, PlaceResult place, float colour) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(place.getLat(), place.getLng()))
                .title(place.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(colour));
        map.addMarker(markerOptions);
    }
}
