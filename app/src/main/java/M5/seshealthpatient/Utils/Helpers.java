package M5.seshealthpatient.Utils;

import android.app.Activity;

import M5.seshealthpatient.R;

public class Helpers {
    public static String getUrl(Activity activity, double lat, double lng, String placeType) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
        url.append("&radius=" + String.valueOf(5000));
        url.append("&type=" + placeType);
        url.append("&key=" + activity.getResources().getString(R.string.api_key));
        return url.toString();
    }
}
