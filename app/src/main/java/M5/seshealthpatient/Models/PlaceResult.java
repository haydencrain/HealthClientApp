package M5.seshealthpatient.Models;

import android.util.Log;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PlaceResult implements Serializable {
    private String Id;
    private String PlaceId;
    private String Name;
    private String Address;
    private double Lat;
    private double Lng;

    public PlaceResult(JSONObject jsonRes) {
        try {
            Id = jsonRes.getString("id");
            PlaceId = jsonRes.getString("place_id");
            Name = jsonRes.getString("name");
            Address = jsonRes.getString("vicinity");
            Lat = jsonRes.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            Lng = jsonRes.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            Log.d("PlaceResult", e.toString());
        }
    }

    public PlaceResult() {
        Id = "";
        PlaceId = "";
        Name = "";
        Address = "";
        Lat = 0;
        Lng = 0;
    }


    public String getId() { return Id; }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getPlaceId() { return PlaceId; }

    public void setPlaceId(String PlaceId) {
        this.PlaceId = PlaceId;
    }

    public String getName() { return Name; }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getAddress() { return Address; }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public Double getLat() { return Lat; }

    public void setLat(double Lat) {
        this.Lat = Lat;
    }

    public Double getLng() { return Lng; }

    public void setLng(double Lng) {
        this.Lng = Lng;
    }

    @Override
    public String toString() {
        return "(" + Id + ": " + Name + ") " + Lat + ", " + Lng;
    }
}
